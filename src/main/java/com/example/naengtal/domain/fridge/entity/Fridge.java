package com.example.naengtal.domain.fridge.entity;

import com.example.naengtal.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Fridge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fridge_id", nullable = false)
    int id;

    @OneToMany(mappedBy = "fridge")
    List<Member> sharedMembers;
}
