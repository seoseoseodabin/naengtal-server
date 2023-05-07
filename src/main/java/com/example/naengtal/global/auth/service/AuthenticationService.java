package com.example.naengtal.global.auth.service;

import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.auth.dto.TokenDto;
import com.example.naengtal.global.auth.jwt.JwtTokenProvider;
import com.example.naengtal.global.common.service.FcmService;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.example.naengtal.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.example.naengtal.global.auth.exception.AuthErrorCode.WRONG_ID_OR_PASSWORD;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final FcmService fcmService;

    public TokenDto signIn(String id, String password, String fcmToken) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);

        // 실제 검증(사용자 비밀번호 체크)
        // authenticate 메서드 실행 시 JwtUserDetailsService 에서 만든 loadUserByName 메서드 실행
        Authentication authentication = getAuthentication(authenticationToken);

        // fcm token 저장
        if (fcmToken != null){
            redisTemplate.opsForList().rightPush(id, fcmToken);

            // fcm 냉장고 구독
            Member member = memberRepository.findById(id).orElseThrow(() -> new RestApiException(MEMBER_NOT_FOUND));
            fcmService.subscribeFridge(member, Collections.singletonList(fcmToken));
        }

        return jwtTokenProvider.generateToken(authentication);
    }

    private Authentication getAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        try {
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception e) {
            throw new RestApiException(WRONG_ID_OR_PASSWORD);
        }
    }

    public void signOut(String accessToken, Member member, String fcmToken) {
        redisTemplate.opsForValue()
                .set(accessToken,
                        "signOut",
                        jwtTokenProvider.getExpiration(accessToken) - (new Date()).getTime(),
                        TimeUnit.MILLISECONDS);

        if (fcmToken != null){
            redisTemplate.opsForList().remove(member.getId(), 0, fcmToken);
            fcmService.unsubscribeFridge(member, Collections.singletonList(fcmToken));
        }
    }
}
