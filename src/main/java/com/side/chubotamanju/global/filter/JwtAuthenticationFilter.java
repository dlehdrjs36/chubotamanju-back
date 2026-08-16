package com.side.chubotamanju.global.filter;


import com.side.chubotamanju.global.common.JwtProperties;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

        String token = null;

        //헤더에서 액세스토큰 추출
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//        }

        //쿠키에서 액세스토큰 추출
        for (Cookie cookie : request.getCookies()) {
            if("accessToken".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
        
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //마이크로서비스 자체적으로 토큰 검증
            try {
                byte[] secretKeyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
                SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

                //내부적으로 토큰 만료, 서명 검증 문제시 예외발생
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

//                String role = (String) claims.get("role");

//                List<GrantedAuthority> authorities = Stream.of(BeanDefinitionDsl.Role.valueOf(role))
//                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
//                        .collect(Collectors.toList());

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

            } catch (JwtException e) {
                SecurityContextHolder.clearContext(); // 토큰 검증 실패 시 초기화
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}