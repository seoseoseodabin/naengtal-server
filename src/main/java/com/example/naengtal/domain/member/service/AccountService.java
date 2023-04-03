package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.fridge.repository.FridgeRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;

    private final FridgeRepository fridgeRepository;

    private final PasswordEncoder passwordEncoder;

    public void saveMember(SignUpRequestDto signUpRequestDto) {
        // id 중복 검사
        memberRepository.findById(signUpRequestDto.getId()).ifPresent(a ->
            System.out.println("id already exist")
        );

        // 비밀번호와 비밀번호 확인 일치하는지 검사
        if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getConfirmPassword()))
            System.out.println("wrong confirm password");

        // 냉장고 생성
        Fridge fridge = new Fridge();
        fridge = fridgeRepository.save(fridge);

        // 디비에 저장장
        memberRepository.save(Member.builder()
                .id(signUpRequestDto.getId())
                .name(signUpRequestDto.getName())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .fridge(fridge)
                .build());
    }
}
