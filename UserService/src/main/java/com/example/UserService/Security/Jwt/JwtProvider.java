package com.example.UserService.Security.Jwt;

import com.example.UserService.Security.UserPrinciple.UserPrinciple;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.UserService.Security.UserPrinciple.UserPrinciple.build;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.refreshExpiration}")
    private int jwtRefreshExpiration;

    public String createToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        List<String> authorities = userPrinciple.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userPrinciple.getUsername())
                .issuer("Web")
                .claim("authorities", authorities)
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + jwtExpiration * 1000L))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(jwtSecret));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String createRefreshToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userPrinciple.getUsername())
                .issuer("Web")
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + jwtExpiration * 1000L))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(jwtSecret));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    public String reduceTokenExpiration(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            JWTClaimsSet oldClaims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            JWTClaimsSet newClaims = new JWTClaimsSet.Builder(oldClaims)
                    .expirationTime(new Date(0))
                    .build();

            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

            JWSObject newJwsObject = new JWSObject(
                    jwsHeader,
                    new Payload(newClaims.toJSONObject())
            );

            newJwsObject.sign(new MACSigner(jwtSecret));

            return newJwsObject.serialize();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new MACVerifier(jwtSecret);

            if (!signedJWT.verify(verifier)) {
                logger.error("Invalid JWT signature");
                return false;
            }
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                logger.error("JWT token is expired");
                return false;
            }

            return true;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
