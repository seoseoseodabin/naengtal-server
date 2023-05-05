package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.dao.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.MemberInfo;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.error.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("signup 성공")
    public void signUpSuccess() {
        // given (Mock 객체 동작 정의)
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(fridgeRepository.save(any(Fridge.class))).willReturn(fridge);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name("test")
                .id("test")
                .password("test1234")
                .confirmPassword("test1234")
                .build();
        MemberInfo memberInfo = accountService.saveMember(signUpRequestDto);

        // then
        assertThat(memberInfo.getId(), is(member.getId()));
        assertThat(memberInfo.getName(), is(member.getName()));
        then(passwordEncoder).should(times(1)).encode(any());
    }

    @Test
    @DisplayName("사용 불가능한 아이디인 경우")
    public void signUpFailWithUnavailableId() {
        // given
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        given(memberRepository.findById(any(String.class))).willReturn(Optional.of(member));

        // when
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name("test")
                .id("test")
                .password("test1234")
                .confirmPassword("test1234")
                .build();

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                accountService.saveMember(signUpRequestDto)
        );
        assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(exception.getErrorCode().name(), is("UNAVAILABLE_ID"));
    }

    @Test
    @DisplayName("비밀번호와 확인용 비밀번호 불일치하는 경우")
    public void signUpFailWithWrongConfirmPassword() {
        // given
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .name("test")
                .id("test")
                .password("test1234")
                .confirmPassword("1234test")
                .build();

        // when
        RestApiException exception = assertThrows(RestApiException.class, () ->
                accountService.saveMember(signUpRequestDto)
        );

        // then
        assertThat(exception.getErrorCode().getHttpStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(exception.getErrorCode().name(), is("WRONG_CONFIRM_PASSWORD"));
    }

    @Test
    @DisplayName("닉네임 변경")
    public void editName() {
        // given
        String newName = "newName";
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("encodedPassword")
                .fridge(fridge)
                .build();

        // when
        accountService.editName(member, newName);

        // then
        assertThat(member.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("본인 계정 정보 가져오기")
    public void getInfo() {
        Fridge fridge = new Fridge(1);
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("encodedPassword")
                .fridge(fridge)
                .build();
        MemberInfo originMemberInfo = MemberInfo.builder()
                .id("test")
                .name("test")
                .build();

        MemberInfo memberInfo = accountService.getInfo(member);

        assertThat(memberInfo).usingRecursiveComparison().isEqualTo(originMemberInfo);
    }
}