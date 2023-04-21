package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.repository.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.MemberInfo;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
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

    public MemberInfo saveMember(SignUpRequestDto signUpRequestDto) {
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

        return getInfo(member);
    }

    public void deleteMember(Member member) {
        // 외래키 제약 조건 때문에 member 먼저 삭제 후 fridge 삭제해야 함
        Fridge fridge = member.getFridge();

        memberRepository.delete(member);

        // jpa 영속성 컨텍스트 때문에 0이 아닌 1로 검사를 해줘야 함
        if (fridge.getSharedMembers().size() == 1)
            fridgeRepository.delete(fridge);
    }

    public void editName(Member member, String name) {
        member.setName(name);
    }

    public MemberInfo getInfo(Member member) {
        return MemberInfo.builder()
                .name(member.getName())
                .id(member.getId())
                .fridgeId(member.getFridge().getId())
                .build();
    }
}
