package com.example.UserService.model.dto.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Old password must be not blank")
    String oldPassword;

    @NotBlank(message = "New password must not be blank")
    @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters")
    String newPassword;

    @NotBlank(message = "Confirm password must not be blank")
    @Size(min = 6, max = 100, message = "Confirm password must be between 6 and 100 characters")
    String confirmPassword;
}
