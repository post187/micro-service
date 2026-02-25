package com.example.Security.Validate;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Component
public class AuthorityTokenUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public List<String> checkPermission(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return (List<String>) signedJWT.getJWTClaimsSet().getClaim("authorities");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}