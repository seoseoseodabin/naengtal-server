package com.example.naengtal.domain.member.controller;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.repository.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "name=tttesttt",
                "id=tttesttt",
                "password=passwd",
                "confirmPassword=passwd"}
)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AccountApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FridgeRepository fridgeRepository;

    @Value("${name}")
    private String name;

    @Value("${id}")
    private String id;

    @Value("${password}")
    private String password;

    @Value("${confirmPassword}")
    private String confirmPassword;

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
    @DisplayName("회원가입 성공 테스트")
    @Transactional
    public void success() throws Exception {
        // when
        String body = mapper.writeValueAsString(SignUpRequestDto.builder()
                .name(name)
                .id(id)
                .password(password)
                .confirmPassword(confirmPassword)
                .build());

        // then
        mvc.perform(post("/account/signup")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("id가 너무 긺")
    @Transactional
    public void failWithInvalidParameter() throws Exception {
        // given
        String invalidId = "tttttttesttttttt";

        // when
        String body = mapper.writeValueAsString(SignUpRequestDto.builder()
                .name(name)
                .id(invalidId)
                .password(password)
                .confirmPassword(confirmPassword)
                .build());

        // then
        mvc.perform(post("/account/signup")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_PARAMETER")));
    }

    @Test
    @DisplayName("비밀번호와 확인용 비밀번호 불일치")
    @Transactional
    public void failWithWrongConfirmPassword() throws Exception {
        // given
        String wrongConfirmPassword = "passwdwdwd";

        // when
        String body = mapper.writeValueAsString(SignUpRequestDto.builder()
                .name(name)
                .id(id)
                .password(password)
                .confirmPassword(wrongConfirmPassword)
                .build());

        // then
        mvc.perform(post("/account/signup")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("WRONG_CONFIRM_PASSWORD")));
    }

    @Test
    @DisplayName("사용 불가능한 id")
    @Transactional
    public void failWithUnavailableId() throws Exception {
        // given
        String unavailableId = "test";

        // when
        String body = mapper.writeValueAsString(SignUpRequestDto.builder()
                .name(name)
                .id(unavailableId)
                .password(password)
                .confirmPassword(confirmPassword)
                .build());

        // then
        mvc.perform(post("/account/signup")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("UNAVAILABLE_ID")));
    }
}