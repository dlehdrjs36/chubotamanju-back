package com.side.chubotamanju.global.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 프론트 화면으로 리다이렉트
        String targetUrl = UriComponentsBuilder
                .fromUriString("https://chubotamanju.com")
                .build().toUriString();

        System.out.println("########################");
        System.out.println("########################");
        System.out.println("OAUTH2 FAILURE: uri=" + request.getRequestURI() + ", exception=" + exception.getClass().getName() + " " + exception);
        System.out.println("########################");
        System.out.println("########################");
        System.out.println("########################");
        System.out.println("########################");

        response.addHeader("X-OAuth-Result", "failure");
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
