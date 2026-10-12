package com.side.chubotamanju.global.filter;


import com.side.chubotamanju.global.common.JwtProperties;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import com.side.chubotamanju.util.JwtUtil;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenProvider;
    private final JwtProperties jwtProperties;

    //CORS 사전 요청(CORS preflight)은 필터 동작안하도록 처리
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {

        // 봇 인증 필터에서 인증이 성공한 경우 바로 통과
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 액세스 토큰 쿠키에서 액세스토큰 값 추출
        String accessToken = findCookieBy(request, "accessToken");
        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //마이크로서비스 자체적으로 토큰 검증
            try {
                Claims accessTokenClaims = jwtTokenProvider.extractAllClaims(accessToken);

                // 리프레시 토큰 쿠키 이름을 변경하여 요청한 것인지 확인한다. 액세스 토큰이 아니라면 거부처리
                String tokenType = accessTokenClaims.get("type", String.class);
                if(!"access".equals(tokenType)) {
                    throw new BadCredentialsException("유효한 토큰이 아닙니다.");
                }

//                String role = (String) claims.get("role");

//                List<GrantedAuthority> authorities = Stream.of(BeanDefinitionDsl.Role.valueOf(role))
//                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
//                        .collect(Collectors.toList());

                registerAuthentication(request, accessTokenClaims);

            // 액세스 토큰 유효 기간 만료 시, 리프레시 토큰을 확인하고 액세스토큰 재발급
            } catch (ExpiredJwtException e) {
                try {
                    //유효 기간이 지난 토큰이면 리프레시 토큰이 있는지 검증한다. 리프레시 토큰이 있으면 새로운 액세스 토큰으로 설정

                    //만료된 토큰에 대한 리프레시 토큰인지 검증
                    Claims expiredClaims = e.getClaims();
                    String expiredTokenProvider = expiredClaims.getSubject();
                    String expiredTokenUserId = expiredClaims.get("id", String.class);

                    String refreshToken = findCookieBy(request, "refreshToken");
                    if(refreshToken != null) {
                        Claims refreshTokenClaims = jwtTokenProvider.extractAllClaims(refreshToken);

                        // 리프레시 토큰 쿠키 이름을 변경하여 요청한 것인지 확인한다. 리프레시 토큰이 아니라면 거부처리
                        String tokenType = refreshTokenClaims.get("type", String.class);
                        if (!"refresh".equals(tokenType)) {
                            throw new BadCredentialsException("유효한 토큰이 아닙니다.");
                        }

                        // 만료된 액세스 토큰과 리프레시 토큰이 동일한 Id를 갖는지 검증
                        String refreshTokenUserId = refreshTokenClaims.get("id", String.class);
                        if (!expiredTokenUserId.equals(refreshTokenUserId)) {
                            throw new BadCredentialsException("유효한 토큰이 아닙니다.");
                        }

                        // 새로운 액세스, 리프레시 토큰을 만들어 응답 헤더에 쿠키 세팅
                        String newAccessToken = jwtTokenProvider.generateAccessToken(expiredTokenProvider, expiredTokenUserId);
                        String newRefreshToken = jwtTokenProvider.generateRefreshToken(expiredTokenProvider, expiredTokenUserId);

                        ResponseCookie cookie = ResponseCookie.from("accessToken", newAccessToken)
                                .domain("chubotamanju.com")
                                .path("/")
                                .httpOnly(true) // 자바스크립트로 쿠키 탈취 방지 (보안)
                                .secure(true) // localhost 테스트 시 false, 운영 환경(https)은 true
                                .sameSite("Lax") // 크로스 사이트 요청 보안 설정
                                .maxAge(jwtProperties.getAccessExpiration()) // 쿠키 만료 시간 (초)
                                .build();

                        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                                .domain("chubotamanju.com")
                                .path("/")
                                .httpOnly(true) // 자바스크립트로 쿠키 탈취 방지 (보안)
                                .secure(true) // localhost 테스트 시 false, 운영 환경(https)은 true
                                .sameSite("Lax") // 크로스 사이트 요청 보안 설정
                                .maxAge(jwtProperties.getRefreshExpiration()) // 쿠키 만료 시간 (초)
                                .build();

                        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

                        Claims newAccessTokenClaims = jwtTokenProvider.extractAllClaims(newAccessToken);
                        registerAuthentication(request, newAccessTokenClaims);

                    } else {
                        SecurityContextHolder.clearContext(); // 토큰 검증 실패 시 초기화
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                } catch (JwtException ex) {
                    SecurityContextHolder.clearContext(); // 토큰 검증 실패 시 초기화
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (JwtException e) {
                SecurityContextHolder.clearContext(); // 토큰 검증 실패 시 초기화
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 토큰 정보를 추출하여 스프링 시큐리티 컨텍스트에 인증정보 등록
     * @param request
     * @param claims
     */
    private static void registerAuthentication(HttpServletRequest request, Claims claims) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        CustomOAuth2User customOAuth2User = CustomOAuth2User.builder()
                .provider(claims.getSubject())
                .providerId(claims.get("id").toString())
                .attributes(claims)
                .authorities(authorities)
                .build();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, authorities);
        //인증객체에 요청 정보 추가 (IP, 세션 등) - 로그
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //컨텍스트를 새로 만들어 깔끔한 상태를 만든 뒤에 인증처리
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);
    }

    /**
     * 쿠키 이름에 해당하는 값 추출
     * @param request
     * @param cookieName
     * @return
     */
    private static String findCookieBy(HttpServletRequest request, String cookieName) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if(cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}