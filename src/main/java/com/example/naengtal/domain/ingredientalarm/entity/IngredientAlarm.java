package com.example.naengtal.domain.ingredientalarm.entity;

import com.example.naengtal.domain.member.entity.Member;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "ingredient_alarm")
public class IngredientAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id", nullable = false)
    private int alarm_id;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
