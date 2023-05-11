package com.example.naengtal.domain.ingredientalarm.controller;

import com.example.naengtal.domain.ingredientalarm.dto.IngredientAlarmResponseDto;
import com.example.naengtal.domain.ingredientalarm.service.IngredientAlarmService;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("ingredient/alarm/")
public class IngredientAlarmApiController {

    private final IngredientAlarmService ingredientAlarmService;

    @GetMapping("get")
    public ResponseEntity<List<IngredientAlarmResponseDto>> getIngredientAlarm(@Parameter(hidden = true) @LoggedInUser Member member) {
        List<IngredientAlarmResponseDto> alarms = ingredientAlarmService.getAlarm(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body(alarms);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteIngredientAlarm(@Parameter(hidden = true) @LoggedInUser Member member,
                                                        @RequestParam(name = "id") int id) {
        ingredientAlarmService.deleteAlarm(member, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }
}
