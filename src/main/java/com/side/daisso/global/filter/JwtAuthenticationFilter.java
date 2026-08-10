package com.side.daisso.global.filter;


import com.side.daisso.global.common.JwtProperties;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProperties jwtProperties;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
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

                String role = (String) claims.get("role");

                List<GrantedAuthority> authorities = Stream.of(BeanDefinitionDsl.Role.valueOf(role))
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

                //스프링 시큐리티 컨텍스트에 인증처리
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (JwtException e) {
                SecurityContextHolder.clearContext(); // 토큰 검증 실패 시 초기화
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}