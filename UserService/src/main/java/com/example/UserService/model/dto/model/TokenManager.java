package com.example.UserService.model.dto.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class TokenManager {
    String accessToken;
    String refreshToken;
    Map<String, String> storeToken = new HashMap<>();
    Map<String, String> storeRefreshToken = new HashMap<>();

    public void storeToken(String userName, String token) {
        storeToken.put(userName, accessToken);
        accessToken = token;
    }

    public void storeRefreshToken(String userName, String refreshToken) {
        this.storeRefreshToken.put(userName, refreshToken);
        this.refreshToken = refreshToken;
    }

    public String getToken(String userName) {
        return storeToken.get(userName);
    }

    public void removeToken(String username) {
        storeToken.remove(username);
    }

    public String getRefreshToken(String userName) {
        return storeRefreshToken.get(userName);
    }
}
