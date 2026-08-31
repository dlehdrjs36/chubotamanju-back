package com.side.chubotamanju.util;

import com.side.chubotamanju.global.common.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final JwtProperties jwtProperties;

    /**
     * AccessToken 발급
     * @param username
     * @return
     */
    public String generateAccessToken(String username) {
        return createToken(username, jwtProperties.getAccessExpiration());
    }

    /**
     * AccessToken 발급
     * @param customUserDetails
     * @return
     */
    public String generateAccessToken(UserDetails customUserDetails) {
        return createToken(customUserDetails, jwtProperties.getAccessExpiration(), true);
    }

    /**
     * AccessToken 발급 - 소셜로그인
     * @param provider
     * @param oAuth2User
     * @return
     */
    public String generateAccessToken(String provider, OAuth2User oAuth2User) {
        return createToken(provider, oAuth2User, jwtProperties.getAccessExpiration(), "access");
    }

    /**
     * AccessToken 발급 - 소셜로그인(리프레시 토큰)
     * @param provider
     * @param providerId
     * @return
     */
    public String generateAccessToken(String provider, String providerId) {
        return createToken(provider, providerId, jwtProperties.getAccessExpiration(), "access");
    }

    /**
     * RefreshToken 발급
     * @param username
     * @return
     */
    public String generateRefreshToken(String username) {
        return createToken(username, jwtProperties.getRefreshExpiration());
    }

    /**
     * RefreshToken 발급
     * @param customUserDetails
     * @return
     */
    public String generateRefreshToken(UserDetails customUserDetails) {
        return createToken(customUserDetails, jwtProperties.getRefreshExpiration(), false);
    }

    /**
     * RefreshToken 발급 - 소셜로그인
     * @param provider
     * @param oAuth2User
     * @return
     */
    public String generateRefreshToken(String provider, OAuth2User oAuth2User) {
        return createToken(provider, oAuth2User, jwtProperties.getRefreshExpiration(), "refresh");
    }

    /**
     * RefreshToken 발급 - 소셜로그인(리프레시 토큰)
     * @param provider
     * @param providerId
     * @return
     */
    public String generateRefreshToken(String provider, String providerId) {
        return createToken(provider, providerId, jwtProperties.getRefreshExpiration(), "refresh");
    }

    /**
     * 토큰 생성
     * @param username
     * @param expiration
     * @return
     */
    private String createToken(String username, long expiration) {
        return Jwts.builder()
                .setClaims(createClaims(username))
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 토큰 생성
     * @param customUserDetails
     * @param expiration
     * @param includeRole
     * @return
     */
    private String createToken(UserDetails customUserDetails, long expiration, boolean includeRole) {
        return Jwts.builder()
                .setClaims(createClaims(customUserDetails, includeRole))
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 토큰 생성 - 소셜로그인
     * @param provider
     * @param oAuth2User
     * @param expiration
     * @return
     */
    private String createToken(String provider, OAuth2User oAuth2User, long expiration, String tokenType) {
        return Jwts.builder()
                .setClaims(createClaims(oAuth2User, tokenType))
                .setSubject(provider)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 토큰 생성 - 소셜로그인(리프레시 토큰)
     * @param provider
     * @param providerId
     * @param expiration
     * @return
     */
    private String createToken(String provider, String providerId, long expiration, String tokenType) {
        return Jwts.builder()
                .setClaims(createClaims(providerId, tokenType))
                .setSubject(provider)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * SecretKey 조회
     * @return
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Claims 생성
     * - 리프레시 토큰에는 권한정보 미포함
     * @param customUserDetails
     * @return
     */
    private static Map<String, Object> createClaims(UserDetails customUserDetails, boolean includeRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", customUserDetails.getUsername());

        if(includeRole) {
//            Role memberRole = customUserDetails.getAuthorities().stream()
//                    .map(role -> role.getAuthority().replace("ROLE_", ""))
//                    .map(role -> Role.valueOf(role))
//                    .findFirst()
//                    .orElse(Role.GENERAL);
//            claims.put("role", memberRole.name());
        } else {
            claims.put("jti", UUID.randomUUID().toString()); // Refresh Token 식별자(redis 키로 활용)
        }

        return claims;
    }

    /**
     * Claims 생성 - 소셜로그인
     * - 리프레시 토큰에는 권한정보 미포함
     * @param oAuth2User
     * @return
     */
    private static Map<String, Object> createClaims(OAuth2User oAuth2User, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", oAuth2User.getAttribute("id"));
        claims.put("type", tokenType);

        return claims;
    }

    /**
     * Claims 생성 - 소셜로그인(리프레시 토큰)
     * - 리프레시 토큰에는 권한정보 미포함
     * @param providerId
     * @return
     */
    private static Map<String, Object> createClaims(String providerId, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", providerId);
        claims.put("type", tokenType);

        return claims;
    }

    /**
     * Claims 생성
     * - 리프레시 토큰에는 권한정보 미포함
     *
     * @return
     */
    private static Map<String, Object> createClaims(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        return claims;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 전체 Claims 추출
     * @param token
     * @return
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}