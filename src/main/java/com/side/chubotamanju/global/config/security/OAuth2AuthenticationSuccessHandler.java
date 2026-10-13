package com.side.chubotamanju.global.config.security;

import com.side.chubotamanju.global.common.JwtProperties;
import com.side.chubotamanju.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. 인증 유저 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. 인증 성공한 Provider 추출
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        // 3. 기존 JWT 생성 로직을 사용하여 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(provider, oAuth2User);
        String refreshToken = jwtTokenProvider.generateRefreshToken(provider, oAuth2User);

        // 4. 응답 헤더에 쿠키 세팅(액세스 토큰, 리프레시 토큰)
        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .domain("chubotamanju.com")
                .path("/")
                .httpOnly(true) // 자바스크립트로 쿠키 탈취 방지 (보안)
                .secure(true) // localhost 테스트 시 false, 운영 환경(https)은 true
                .sameSite("Lax") // 크로스 사이트 요청 보안 설정
                .maxAge(Duration.ofMillis(jwtProperties.getAccessExpiration())) // 쿠키 만료 시간 (초)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .domain("chubotamanju.com")
                .path("/")
                .httpOnly(true) // 자바스크립트로 쿠키 탈취 방지 (보안)
                .secure(true) // localhost 테스트 시 false, 운영 환경(https)은 true
                .sameSite("Lax") // 크로스 사이트 요청 보안 설정
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshExpiration())) // 쿠키 만료 시간 (초)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 5. JWT를 쿠키에 담아 프론트엔드 화면으로 리다이렉트
        String targetUrl = UriComponentsBuilder
                .fromUriString("https://chubotamanju.com")
                .build().toUriString();

        for (int i = 0; i < 500; i++) {
            System.out.println("jwtProperties.getAccessExpiration() : " + jwtProperties.getAccessExpiration() + " Duration.ofMillis(jwtProperties.getRefreshExpiration()) : " + Duration.ofMillis(jwtProperties.getRefreshExpiration()) + " Set-Cookie count: " +
                    response.getHeaders(HttpHeaders.SET_COOKIE).size());

            System.out.println("Response committed: " +
                    response.isCommitted());
        }


        response.addHeader(
                HttpHeaders.SET_COOKIE,
                "oauthTest=success; Path=/; Secure; HttpOnly; SameSite=Lax"
        );

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
