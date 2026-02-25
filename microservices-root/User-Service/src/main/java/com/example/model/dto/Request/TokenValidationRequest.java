package com.example.model.dto.Request;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class TokenValidationRequest {
    private String accessToken;
}
