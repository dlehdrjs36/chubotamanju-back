package com.side.chubotamanju.global.filter;


import com.side.chubotamanju.global.common.ChubotaHeader;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.api-key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //헤더에 서버 검증용 헤더가 있는지 확인
        String requestApiKey = request.getHeader(ChubotaHeader.API_KEY.getName());

        // 1. 요청에 API Key 헤더가 있고, 값이 일치하는 경우 (봇 서버의 요청)
        if (requestApiKey != null && requestApiKey.equals(expectedApiKey)) {

            String botUserId = request.getHeader(ChubotaHeader.BOT_USER_ID.getName()) == null || request.getHeader(ChubotaHeader.BOT_USER_ID.getName()).isEmpty() ? "botUser" : request.getHeader(ChubotaHeader.BOT_USER_ID.getName());

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", botUserId);
            claims.put("type", "bot");

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            CustomOAuth2User customOAuth2User = CustomOAuth2User.builder()
                    .provider("discord")
                    .providerId(botUserId)
                    .attributes(claims)
                    .authorities(authorities)
                    .build();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, authorities);

            //인증객체에 요청 정보 추가 (IP, 세션 등) - 로그
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //컨텍스트를 새로 만들어 깔끔한 상태를 만든 뒤에 인증처리 - SecurityContext에 등록하면 다음 필터(JWT 등)는 이미 인증된 것으로 인식하고 통과함
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);
        }

        // 2. 헤더에 API Key가 없거나 틀렸다면? 그냥 다음 필터(예: JWT 필터)로 넘김 (일반 웹 사용자 처리)
        filterChain.doFilter(request, response);
    }
}
