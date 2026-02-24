package com.example.UserService.model.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Inputs must be email format")
    private String email;

    @NotBlank(message = "New password must not be blank")
    @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters")
    private String newPassword;
}
