package com.example.naengtal.global.auth.service;

import com.example.naengtal.global.auth.dto.TokenDto;
import com.example.naengtal.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public TokenDto signIn(String id, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);

        // 실제 검증(사용자 비밀번호 체크)
        // authenticate 메서드 실행 시 JwtUserDetailsService 에서 만든 loadUserByName 메서드 실행
        Authentication authentication = getAuthentication(authenticationToken);

        return jwtTokenProvider.generateToken(authentication);
    }

    private Authentication getAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        try {
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}