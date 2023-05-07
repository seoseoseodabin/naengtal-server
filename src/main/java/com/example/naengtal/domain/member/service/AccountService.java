package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.fridge.dao.FridgeRepository;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.MemberInfo;
import com.example.naengtal.domain.member.dto.SignUpRequestDto;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.service.FcmService;
import com.example.naengtal.global.common.service.S3Uploader;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.naengtal.domain.member.exception.MemberErrorCode.UNAVAILABLE_ID;
import static com.example.naengtal.domain.member.exception.MemberErrorCode.WRONG_CONFIRM_PASSWORD;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;

    private final FridgeRepository fridgeRepository;

    private final PasswordEncoder passwordEncoder;

    private final IngredientRepository ingredientRepository;

    private final S3Uploader s3Uploader;

    private final RedisTemplate<String, String> redisTemplate;

    private final FcmService fcmService;

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
        // fridge 에 속한 재료들의 이미지 삭제
        Fridge fridge = member.getFridge();
        ingredientRepository.findByFridge(fridge)
                .forEach(ingredient ->
                        s3Uploader.deleteFile(ingredient.getImage()));

        if (fridge.getSharedMembers().size() == 1) {
            fridgeRepository.delete(fridge);
        }

        memberRepository.delete(member);
        fcmService.unsubscribeFridge(member, getTokenList(member));
    }

    public void editName(Member member, String name) {
        member.setName(name);
    }

    public MemberInfo getInfo(Member member) {
        return MemberInfo.builder()
                .name(member.getName())
                .id(member.getId())
                .build();
    }

    private List<String> getTokenList(Member member) {
        return redisTemplate.opsForList().range(member.getId(), 0, -1);
    }
}
