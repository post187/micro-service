package com.example.UserService.model.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private String gender;
    private String phone;
    private String avatar;
    private Collection<? extends GrantedAuthority> roles;
}
