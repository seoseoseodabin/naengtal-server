package com.example.naengtal.global.auth.jwt;

import com.example.naengtal.global.error.ErrorResponseDto;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.naengtal.global.auth.exception.AuthErrorCode.INVALID_TOKEN;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(INVALID_TOKEN.getHttpStatus().value());

        response.getWriter().write(ErrorResponseDto.builder()
                .code(INVALID_TOKEN.getHttpStatus().toString())
                .message(INVALID_TOKEN.getMessage())
                .build().toString());
    }
}
