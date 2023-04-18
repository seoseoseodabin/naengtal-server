package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.MemberResponseDto;
import com.example.naengtal.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSearchService {

    private final MemberRepository memberRepository;

    public List<MemberResponseDto> search(Member inviter, String nameOrId) {
        List<Member> memberList = memberRepository.findByNameContainsOrIdContains(nameOrId, nameOrId);

        memberList.remove(inviter); // 본인은 검색 대상에서 제외

        return memberList.stream()
                .map(member -> new MemberResponseDto(member.getName(), member.getId(), inviter.getFridge().equals(member.getFridge())))
                .collect(Collectors.toList());
    }

    public List<MemberResponseDto> searchSharedMembers(Member member) {
        List<Member> memberList = member.getFridge().getSharedMembers();

        memberList.remove(member);

        return memberList.stream()
                .map(sharedMember -> new MemberResponseDto(sharedMember.getName(), sharedMember.getId(), true))
                .collect(Collectors.toList());
    }
}
