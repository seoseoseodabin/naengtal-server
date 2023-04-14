package com.example.naengtal.global.auth.jwt;

import com.example.naengtal.global.error.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
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
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code(INVALID_TOKEN.name())
                .message(INVALID_TOKEN.getMessage())
                .build();

        String errorCode = new ObjectMapper().writeValueAsString(errorResponseDto);

        response.getWriter().write(errorCode);
    }
}
