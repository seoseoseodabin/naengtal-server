package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.repository.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.dto.SignUpResponseDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.naengtal.domain.member.exception.MemberErrorCode.UNAVAILABLE_ID;
import static com.example.naengtal.domain.member.exception.MemberErrorCode.WRONG_CONFIRM_PASSWORD;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;

    private final FridgeRepository fridgeRepository;

    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDto saveMember(SignUpRequestDto signUpRequestDto) {
        // id 중복 검사
        memberRepository.findById(signUpRequestDto.getId()).ifPresent(a -> {
            throw new RestApiException(UNAVAILABLE_ID);
        });

        // 비밀번호와 비밀번호 확인 일치하는지 검사
        if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getConfirmPassword()))
            throw new RestApiException(WRONG_CONFIRM_PASSWORD);

        // 냉장고 생성
        Fridge fridge = new Fridge();
        fridge = fridgeRepository.save(fridge);

        // 디비에 저장
        Member member = memberRepository.save(Member.builder()
                .id(signUpRequestDto.getId())
                .name(signUpRequestDto.getName())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .fridge(fridge)
                .build());

        return SignUpResponseDto.builder()
                .name(member.getName())
                .id(member.getId())
                .fridgeId(fridge.getId())
                .build();
    }

    public void deleteMember(Member member) {
        // 냉장고, 냉장고 공유 구현 후 냉장고 삭제 로직 추가해야 함
        // 외래키 제약 조건 때문에 member 먼저 삭제 후 fridge 삭제해야 함
        int fridgeId = member.getFridge().getId();
        memberRepository.delete(member);
        fridgeRepository.deleteById(fridgeId);
    }
}
