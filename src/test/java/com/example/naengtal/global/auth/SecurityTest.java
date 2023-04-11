package com.example.naengtal.global.auth;

import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.service.AccountService;
import com.example.naengtal.global.auth.jwt.JwtTokenProvider;
import com.example.naengtal.global.auth.service.AuthenticationService;
import com.example.naengtal.global.error.RestApiException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static com.example.naengtal.global.auth.jwt.JwtAuthenticationFilter.BEARER_PREFIX;
import static com.example.naengtal.global.auth.jwt.JwtTokenProvider.AUTHORITIES_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SecurityTest {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${jwt.secret}")
    private String secretKey;

    @BeforeEach
    void beforeEach() {
        // 회원 가입
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name("test")
                .id("test")
                .password("test1234")
                .confirmPassword("test1234")
                .build();


        accountService.saveMember(signUpRequestDto);
    }

    @AfterEach
    void afterEach() {
        memberRepository.findById("test").ifPresent(member -> memberRepository.delete(member));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        String accessToken = authenticationService.signIn("test", "test1234").getAccessToken();

        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() {
        assertThatThrownBy(() -> authenticationService.signIn("test", "test1233")).isInstanceOf(RestApiException.class);
    }

    @Test
    @DisplayName("accessToken 검증 성공")
    void access_token_validate_success() {
        String accessToken = authenticationService.signIn("test", "test1234").getAccessToken();

        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰으로 접근 시 실패")
    void access_token_expired_fail() {
        long now = new Date().getTime();

        String expiredToken = Jwts.builder()
                .setSubject("test")
                .claim(AUTHORITIES_KEY, "ROLE_USER")
                .setExpiration((new Date(now - 1)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("시크릿 키가 틀린 토큰 정보일 경우 실패")
    void access_token_wrong_secret_key_fail() {
        long now = new Date().getTime();

        String accessToken = Jwts.builder()
                .setSubject("test")
                .claim(AUTHORITIES_KEY, "ROLE_USER")
                .setExpiration((new Date(now + 1000 * 60 * 2)))
                .signWith(Keys.hmacShaKeyFor("WrongKey_잘못된키입니다_WrongKey".getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtTokenProvider.validateToken(accessToken)).isFalse();
    }

    @Test
    @DisplayName("Role이 다를 경우 실패")
    void access_token_wrong_role_fail() throws Exception {
        long now = new Date().getTime();
        HttpHeaders httpHeaders = new HttpHeaders();

        String accessToken = Jwts.builder()
                .setSubject("test")
                .claim(AUTHORITIES_KEY, "ROLE_WRONG")
                .setExpiration((new Date(now + 1000 * 60 * 2)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + accessToken);

        mockMvc.perform(post("/auth/signout")
                        .headers(httpHeaders))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("로그아웃_성공")
    void signOutTest() throws Exception {
        String accessToken = authenticationService.signIn("test", "test1234").getAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken);

        authenticationService.signOut(accessToken);


        mockMvc.perform(post("/auth/signout")
                        .headers(httpHeaders))
                .andExpect(status().isUnauthorized());
    }
}
