package com.example.Service.Impl;

import com.example.Constant.KafkaConstant;
import com.example.Event.EventProducer;
import com.example.Exception.wrapper.*;
import com.example.Repository.UserRepository;
import com.example.Security.Jwt.JwtProvider;
import com.example.Security.UserPrinciple.UserDetailService;
import com.example.Security.UserPrinciple.UserPrinciple;
import com.example.Service.RoleService;
import com.example.Service.UserService;
import com.example.model.Entity.RoleName;
import com.example.model.Entity.User;
import com.example.model.dto.Request.ChangePasswordRequest;
import com.example.model.dto.Request.EmailDetails;
import com.example.model.dto.Request.Login;
import com.example.model.dto.Request.SignUp;
import com.example.model.dto.Response.JwtResponseMessage;
import com.example.model.dto.Response.PageResponse;
import com.example.model.dto.Response.UserResponse;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    private EventProducer eventProducer;

    @Value("${refresh.token.url}")
    private String jwtSecret;

    Gson gson = new Gson();

    @Override
    public Mono<User> register(SignUp signUp) {
        return Mono.fromCallable(() -> {
            if (existsByUsername(signUp.getUsername())) {
                throw new EmailOrUsernameNotFoundException(
                        "The username " + signUp.getUsername() + " is existed, please try again.");
            }
            if (existsByEmail(signUp.getEmail())) {
                throw new EmailOrUsernameNotFoundException(
                        "The email " + signUp.getUsername() + " is existed, please try again.");
            }
            if (existsByPhoneNumber(signUp.getPhone())) {
                throw new PhoneNumberNotFoundException(
                        "The phone number " + signUp.getPhone() + " is existed, please try again.");
            }

            User user = modelMapper.map(signUp, User.class);
            user.setPassword(passwordEncoder.encode(signUp.getPassword()));
            user.setRoles(signUp.getRoles()
                    .stream()
                    .map(role -> roleService.findByName(mapToRoleName(role))
                            .orElseThrow(() -> new RuntimeException("Role not found in the database.")))
                    .collect(Collectors.toSet())
            );

            return userRepository.save(user);
                }).subscribeOn(Schedulers.boundedElastic());
    }
    public static RoleName mapToRoleName(String role) {
        return switch (role) {
            case "ADMIN", "admin", "Admin" -> RoleName.ADMIN;
            case "Pm", "PM", "pm" -> RoleName.PM;
            case "User", "USER", "user" -> RoleName.USER;
            default -> null;
        };
    }

    @Override
    public Mono<JwtResponseMessage> login(Login signInForm) {
        return Mono.fromCallable(() -> {
            String userOrEmail = signInForm.getUserName();
            boolean isEmail = userOrEmail.contains("@gmail.com");
            UserDetails userDetails;
            if (isEmail) {
                userDetails = userDetailService.loadUserByEmail(userOrEmail);
            } else {
                userDetails = userDetailService.loadUserByUsername(userOrEmail);
            }

            if (userDetails == null) {
                throw new UserNotFoundException("User not found");
            }

            if (!passwordEncoder.matches(signInForm.getPassword(), userDetails.getPassword())) {
                throw new PasswordNotFoundException("Incorrect password");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtProvider.createToken(authentication);
            String refreshToken = jwtProvider.createRefreshToken(authentication);

            UserPrinciple userPrinciple = (UserPrinciple) userDetails;

            return JwtResponseMessage.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userInfo(UserResponse.builder()
                            .id(userPrinciple.id())
                            .fullName(userPrinciple.fullName())
                            .userName(userPrinciple.username())
                            .email(userPrinciple.email())
                            .phone(userPrinciple.phone())
                            .gender(userPrinciple.gender())
                            .avatar(userPrinciple.avatar())
                            .roles(userPrinciple.roles())
                            .build())
                    .build();
        }).subscribeOn(Schedulers.boundedElastic()).onErrorResume(Mono::error);
    }

    @Override
    public Mono<Void> logout() {
        return Mono.fromRunnable(() -> {
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

           SecurityContextHolder.getContext().setAuthentication(null);

            String currentToken = getCurrentToken();

            if (authentication != null && authentication.isAuthenticated()) {
                // Invalidate the current token by reducing its expiration time
                String updatedToken = jwtProvider.reduceTokenExpiration(currentToken);
            }

            SecurityContextHolder.clearContext();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();

            if (credentials instanceof String) {
                return (String) credentials;
            }
        }

        return null;
    }


    @Override
    @Transactional
    public Mono<User> update(Long userId, SignUp update) {
        return Mono.fromCallable(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found userId: " + userId + " for update"));

            modelMapper.map(update, user);

            user.setPassword(passwordEncoder.encode(update.getPassword()));

            return userRepository.save(user);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> changePassword(ChangePasswordRequest request) {
        return Mono.fromCallable(() -> doChangePassword(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public String doChangePassword(ChangePasswordRequest request) {
        UserDetails userDetails = getCurrentUserDetails();
        String username = userDetails.getUsername();

        User existingUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));

        if (passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            if (validateNewPassword(request.getNewPassword(), request.getConfirmPassword())) {
                existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(existingUser);

                // send email through kafka client
                EmailDetails emailDetails = emailDetailsConfig(username);

                eventProducer.send(KafkaConstant.PROFILE_ONBOARDING_TOPIC, gson.toJson(emailDetails))
                        .subscribeOn(Schedulers.boundedElastic())
                        .subscribe();

                return "Password changed successfully";
            }

            return "Password changed failed.";
        } else {
            throw new PasswordNotFoundException("Incorrect password");
        }
    }

    private EmailDetails emailDetailsConfig(String username) {
        return EmailDetails.builder()
                .recipient("dinhdang2208@gmail.com")
                .msgBody(textSendEmailChangePasswordSuccessfully(username))
                .subject("Password Change Successful: "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .attachment("Please be careful, don't let this information leak")
                .build();
    }

    public String textSendEmailChangePasswordSuccessfully(String username) {
        return "Hey " + username + "!\n\n" +
                "This is a confirmation that your password has been successfully changed.\n" +
                " If you did not initiate this change, please contact our support team immediately.\n" +
                "If you have any questions or concerns, feel free to reach out to us.\n\n" +
                "Best regards:\n\n" +
                "Contact: xxxx@gmail.com\n";
    }


    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            throw new UserNotAuthenticatedException("User not authenticated.");
        }
    }

    private boolean validateNewPassword(String newPassword, String confirmPassword) {
        return Objects.equals(newPassword, confirmPassword);
    }

    @Override
    public Mono<String> delete(Long id) {
        return Mono.fromCallable(() -> doDelete(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public String doDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for userId: " + id));

        try {
            userRepository.delete(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error deleting user with userId: " + id, e);
        }

        return "User with id " + id + " deleted successfully.";
    }


    public Mono<String> refreshToken(String refreshToken) {
        return webClient.build()
                .post()
                .uri(refreshTokenUrl)
                .header("Refresh-Token", refreshToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        clientResponse -> Mono.error(new IllegalArgumentException("Refresh token không hợp lệ")))
                .bodyToMono(JwtResponseMessage.class)
                .map(JwtResponseMessage::getAccessToken);
    }

    @Override
    public Mono<User> findById(Long userId) {
        return Mono.fromCallable(() -> userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<User> findByUsername(String userName) {
        return Mono.fromCallable(() -> userRepository.findByUsername(userName)
                        .orElseThrow(() -> new UserNotFoundException("User not found with userName: " + userName)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<PageResponse<UserResponse>> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        return Mono.fromCallable(() -> {
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Giả sử userRepository.findAll trả về Page<User> (JPA/Blocking)
            Page<User> userPage = userRepository.findAll(pageable);

            List<UserResponse> content = userPage.getContent().stream()
                    .map(user -> modelMapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());

            return PageResponse.<UserResponse>builder()
                    .content(content)
                    .pageNumber(userPage.getNumber())
                    .pageSize(userPage.getSize())
                    .totalElements(userPage.getTotalElements())
                    .totalPages(userPage.getTotalPages())
                    .last(userPage.isLast())
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phone) {
        return userRepository.existsByPhoneNumber(phone);
    }
}
