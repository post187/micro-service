package com.example.Service;

import com.example.model.Entity.User;
import com.example.model.dto.Request.ChangePasswordRequest;
import com.example.model.dto.Request.Login;
import com.example.model.dto.Request.SignUp;
import com.example.model.dto.Response.JwtResponseMessage;
import com.example.model.dto.Response.PageResponse;
import com.example.model.dto.Response.UserResponse;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> register(SignUp signUp);

    Mono<JwtResponseMessage> login(Login signInForm);

    Mono<Void> logout();

    Mono<User> update(Long userId, SignUp update);

    Mono<String> changePassword(ChangePasswordRequest request);

    // Mono<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
    Mono<String> delete(Long id);

    Mono<User> findById(Long userId);

    Mono<User> findByUsername(String userName);

    Mono<PageResponse<UserResponse>> findAllUsers(int page, int size, String sortBy, String sortOrder);
}
