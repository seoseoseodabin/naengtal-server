package com.example.naengtal.domain.ingredientalarm.service;

import com.example.naengtal.domain.alarm.dto.FcmNotificationDto;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.ingredientalarm.dao.IngredientAlarmRepository;
import com.example.naengtal.domain.ingredientalarm.entity.IngredientAlarm;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.service.FcmService;
import com.example.naengtal.global.common.service.FcmType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpirationNotificationScheduler {

    private final IngredientRepository ingredientRepository;
    private final IngredientAlarmRepository ingredientAlarmRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final FcmService fcmService;
    private LocalDate now;

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void schedule() {
        System.out.println("schedule");
        now = LocalDate.now();

        // 유통기한 임박 (D-5~D-day)
        List<Ingredient> ingredients = ingredientRepository.findByExpirationDateLessThanEqualOrderByExpirationDate(now.plusDays(5));
        sendAlarms(ingredients);
    }

    private void sendAlarms(List<Ingredient> ingredients) {
        // db에 저장, fcm 알람 보냄
        ingredients.forEach(ingredient -> {
            saveAlarm(ingredient);
            sendAlarm(ingredient);
        });
    }

    private void saveAlarm(Ingredient ingredient) {
        System.out.println(ingredient.getName());
        String message = ingredient.getName() + " " + getMessage(getTimeDifference(ingredient.getExpirationDate()));

        Fridge fridge = ingredient.getFridge();
        List<Member> sharedMembers = fridge.getSharedMembers();

        sharedMembers.stream()
                .map(member -> IngredientAlarm.builder()
                        .createdAt(LocalDateTime.now())
                        .text(message)
                        .member(member)
                        .build())
                .forEach(ingredientAlarmRepository::save);
    }

    private void sendAlarm(Ingredient ingredient) {
        int timeDifference = getTimeDifference(ingredient.getExpirationDate());

        fcmService.sendByTopic(String.valueOf(ingredient.getFridge().getId()), FcmNotificationDto.builder()
                .title(getTitle(timeDifference))
                .body(ingredient.getName() + getMessage(timeDifference))
                .type(FcmType.EXPIRATION_DATE)
                .build());
    }

    private String getMessage(int date) {
        if (date < 0) {
            return "의 유통기한이 지났어요.";
        }
        return "의 유통기한이 " + date + "일 남았어요.";
    }

    private String getTitle(int date) {
        if (date < 0) {
            return "유통기한 만료";
        }
        return "유통기한 임박";
    }

    private int getTimeDifference(LocalDate expirationDate) {
        Period period = Period.between(now, expirationDate);
        return period.getDays();
    }
}
