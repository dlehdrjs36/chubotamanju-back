package com.side.chubotamanju.global.config;


import com.side.chubotamanju.domain.user.service.CustomOAuth2UserService;
import com.side.chubotamanju.global.common.JwtProperties;
import com.side.chubotamanju.global.config.security.OAuth2AuthenticationSuccessHandler;
import com.side.chubotamanju.global.filter.ApiKeyAuthenticationFilter;
import com.side.chubotamanju.global.filter.JwtAuthenticationFilter;
import com.side.chubotamanju.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProperties jwtProperties;
    private final JwtUtil jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
        return new ApiKeyAuthenticationFilter();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, jwtProperties);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(basicConfigurer -> basicConfigurer.disable()) //jwt 사용으로 비활성
            .formLogin(formLoginConfigurer -> formLoginConfigurer.disable()) //form 로그인 비활성화
            .logout((logout) -> logout.disable())
            // 디스코드 OAuth2 로그인 기능 활성화
            .oauth2Login(oauth2 -> oauth2
                    // 사용자가 /oauth2/authorization/discord 로 요청하면 로그인이 시작됩니다.
                    .authorizationEndpoint(authorization -> authorization
                            .baseUri("/oauth2/authorization")
                    )
                    // 디스코드에서 인증 완료 후 백엔드로 리다이렉트되는 경로를 처리합니다.
                    .redirectionEndpoint(redirection -> redirection
                            .baseUri("/login/oauth2/code/*")
                    )
                    // 디스코드로부터 유저 정보를 받아왔을 때 실행할 서비스를 등록합니다.
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                            .userService(customOAuth2UserService)
                    )
                    // 로그인 성공 시 JWT를 생성하고 프론트엔드로 전달할 커스텀 핸들러 설정
                    .successHandler(oAuth2AuthenticationSuccessHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/actuator/**", "/api/public/**", "/api-docs/**", "/login/**", "/oauth2/**").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authException) -> {
                        // 시큐리티 예외를 스프링 예외 핸들러(ControllerAdvice) 쪽으로 위임한다
                        handlerExceptionResolver.resolveException(request, response, null, authException);
                    })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(apiKeyAuthenticationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}