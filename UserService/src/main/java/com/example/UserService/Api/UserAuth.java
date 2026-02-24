package com.example.UserService.Api;

import com.example.UserService.Security.Validate.AuthorityTokenUtil;
import com.example.UserService.Security.Validate.ValidateToken;
import com.example.UserService.Service.UserService;
import com.example.UserService.model.dto.Request.Login;
import com.example.UserService.model.dto.Request.SignUp;
import com.example.UserService.model.dto.Response.JwtResponseMessage;
import com.example.UserService.model.dto.Response.ResponseMessage;
import com.example.UserService.model.dto.Response.TokenValidationResponse;
import com.example.UserService.model.dto.Response.UserResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Api(value = "User Authentication API", description = "APIs for user registration, login, and authentication")
@RequiredArgsConstructor
public class UserAuth {
    private final UserService userService;
    private final ValidateToken validateToken;
    private final AuthorityTokenUtil authorityTokenUtil;

    @ApiOperation(value = "Register a new user", notes = "Registers a new user with the provided details.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User created successfully", response = ResponseMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error",  response = ResponseMessage.class)
    })
    @PostMapping({ "/signup", "/register" })
    public Mono<ResponseEntity<ResponseMessage>> register(@Valid @RequestBody SignUp signUp) {
        return userService.register(signUp)
                .map(user -> {
                    ResponseMessage message =
                            new ResponseMessage("Create user: \" + signUp.getUsername() + \" successfully.");
                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(message);
                })
                .onErrorResume(ex -> Mono.just(
                ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(ex.getMessage())
                        )));
    }

    @ApiOperation(value = "User login", notes = "Logs in a user with the provided credentials.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login successful", response = JwtResponseMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ResponseEntity.class)
    })
    @PostMapping({ "/signin", "/login" })
    public Mono<ResponseEntity<JwtResponseMessage>> login(@Valid @RequestBody Login login) {
        return userService.login(login)
                .map(jwt -> {
                    JwtResponseMessage message = new JwtResponseMessage(
                            jwt.getAccessToken(),
                            jwt.getRefreshToken(),
                            jwt.getUserInfo()
                    );
                    return ResponseEntity.ok(message);
                })
                .onErrorResume(error -> {
                    JwtResponseMessage jwtResponseMessage = new JwtResponseMessage(
                      null,
                      null,
                      new UserResponse()
                    );
                    return Mono.just(new ResponseEntity<>(jwtResponseMessage, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @ApiOperation(value = "User logout", notes = "Logs out the authenticated user.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Logged out successfully", response = String.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseEntity.class)
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<ResponseEntity<String>> logout() {
        log.info("Logout endpoint called");

        return userService.logout()
                .then(Mono.just(new ResponseEntity<>("Logged out successfully.", HttpStatus.OK)))
                .onErrorResume(
                        error -> {
                            log.error("Logout failed", error);
                            return Mono.just(new ResponseEntity<>("Logout failed.", HttpStatus.BAD_REQUEST));

                }
                );
    }

    @GetMapping({ "/validateToken", "/validate-token" })
    public ResponseEntity<?> validateToken(@RequestHeader(name = "Authorization") String authorizationToken) {
        if (validateToken.validateToken(authorizationToken)) {
            return ResponseEntity.ok(new TokenValidationResponse("Valid token"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenValidationResponse("Invalid token"));
        }
    }

    @ApiOperation(value = "Check user authority", notes = "Checks if the user has the specified authority.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Role access API", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = TokenValidationResponse.class)
    })
    @GetMapping({ "/hasAuthority", "/authorization" })
    public ResponseEntity<?> getAuthority(@RequestHeader(name = "Authorization") String authorizationToken,
                                          @RequestParam String requiredRole) {
        List<String> authorities = authorityTokenUtil.checkPermission(authorizationToken);

        if (authorities.contains(requiredRole)) {
            return ResponseEntity.ok(new TokenValidationResponse("Role access api"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token"));
        }
    }
}
