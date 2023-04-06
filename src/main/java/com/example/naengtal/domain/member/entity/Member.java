package com.example.naengtal.domain.member.entity;

import com.example.naengtal.domain.fridge.entity.Fridge;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "fridge_id", nullable = false)
    private Fridge fridge;

    @Builder
    public Member(String id, String name, String password, Fridge fridge) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.fridge = fridge;
    }
}
