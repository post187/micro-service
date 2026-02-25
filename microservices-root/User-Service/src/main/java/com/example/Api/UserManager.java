package com.example.Api;


import com.example.Exception.wrapper.TokenErrorOrAccessTimeOut;
import com.example.Http.HeaderGenerator;
import com.example.Security.Jwt.JwtProvider;
import com.example.Service.UserService;
import com.example.model.dto.Request.ChangePasswordRequest;
import com.example.model.dto.Request.SignUp;
import com.example.model.dto.Response.PageResponse;
import com.example.model.dto.Response.ResponseMessage;
import com.example.model.dto.Response.UserResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/manager")
@Api(value = "User API", description = "Operations related to users")
@RequiredArgsConstructor
public class UserManager {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "Update user information", notes = "Update the user information with the provided details.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User updated successfully", response = ResponseMessage.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseMessage.class)
    })
    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("id") Long id, @RequestBody SignUp signUp) {
        return userService.update(id, signUp)
                .map(user -> {
                    ResponseMessage message = new ResponseMessage("Update user: " + signUp.getUsername()
                            + " successfully.");

                    return ResponseEntity.ok(message);
                })
                .onErrorResume(
                    error -> Mono.just(new ResponseEntity<>(
                        new ResponseMessage("Update user: "
                                + signUp.getUsername() + " failed "
                                + error.getMessage()),
                        HttpStatus.BAD_REQUEST)));
    }
    @ApiOperation(value = "Change user password", notes = "Change the password for the authenticated user.")
    @ApiResponse(code = 200, message = "Password changed successfully", response = String.class)
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<String> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }

    @ApiOperation(value = "Delete user", notes = "Delete a user with the specified ID.")
    @ApiResponse(code = 200, message = "Password changed successfully", response = String.class)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Mono<ResponseEntity<String>> delete(@PathVariable("id") Long id) {

        return userService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorResume(error ->
                        Mono.just(
                                new ResponseEntity<>("Delete failed.",
                                        HttpStatus.BAD_REQUEST)
                        )
                );
    }

    @ApiOperation(value = "Get user by username", notes = "Retrieve user information based on the provided username.")
    @GetMapping("/user")
    @PreAuthorize("(isAuthenticated() and (hasAuthority('USER') and principal.username == #username) or hasAuthority('ADMIN'))")
    public Mono<ResponseEntity<UserResponse>> getUserByUsername(@RequestParam(value = "username") String username) {
        return userService.findByUsername(username)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .map(userDto -> new ResponseEntity<>(userDto,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(null, headerGenerator.getHeadersForError(),
                        HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Get user by ID", notes = "Retrieve user information based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User retrieved successfully", response = UserResponse.class),
            @ApiResponse(code = 404, message = "User not found", response = ResponseEntity.class)
    })
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and principal.id == #id")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable("id") Long id) {
        return userService.findById(id)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .map(userDto -> new ResponseEntity<>(userDto,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(null, headerGenerator.getHeadersForError(),
                        HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Get a secure user resource", authorizations = { @Authorization(value = "JWT") })
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<PageResponse<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "id") String sortBy,
                                                                        @RequestParam(defaultValue = "ASC") String sortOrder) {

        return userService.findAllUsers(page, size, sortBy, sortOrder)
                .map(usersPage -> new ResponseEntity<>(usersPage,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK));
    }

    @ApiOperation(value = "Get user information from token", notes = "Retrieve user information based on the provided JWT token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User information retrieved successfully", response = UserResponse.class),
            @ApiResponse(code = 404, message = "User not found", response = ResponseEntity.class)
    })
    @GetMapping("/info")
    public Mono<ResponseEntity<UserResponse>> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtProvider.getUserNameFromToken(token);
        return userService.findByUsername(username)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .map(userDto -> new ResponseEntity<>(userDto,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .switchIfEmpty(Mono
                        .error(new TokenErrorOrAccessTimeOut("Token error or access timeout")));
    }


}
