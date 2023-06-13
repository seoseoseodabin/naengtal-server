package com.example.naengtal.domain.member.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.dto.MemberResponseDto;
import com.example.naengtal.domain.member.entity.Member;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberSearchServiceTest {

    @InjectMocks
    private MemberSearchService memberSearchService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 검색 성공")
    void search_success() {
        // given
        Fridge fridge1 = new Fridge(1);
        Fridge fridge2 = new Fridge(2);
        Member inviter = Member.builder()
                .name("inviter")
                .id("inviter")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member member1 = Member.builder()
                .name("test1")
                .id("test1")
                .password("encodedPassword")
                .fridge(fridge1)
                .build();
        Member member2 = Member.builder()
                .name("test2")
                .id("test2")
                .password("encodedPassword")
                .fridge(fridge2)
                .build();
        List<Member> memberList = new ArrayList<>(List.of(inviter, member1, member2));
        given(memberRepository.findByNameContainsOrIdContains(any(String.class), any(String.class))).willReturn(memberList);

        // when
        String nameOrId = "t";
        List<MemberResponseDto> searchedList = memberSearchService.search(inviter, nameOrId);

        // then
        assertEquals(searchedList.size(), 2);
        assertEquals(searchedList.get(0).getId(), member1.getId());
        assertTrue(searchedList.get(0).isSharing());
        assertEquals(searchedList.get(1).getId(), member2.getId());
        assertFalse(searchedList.get(1).isSharing());
    }
}