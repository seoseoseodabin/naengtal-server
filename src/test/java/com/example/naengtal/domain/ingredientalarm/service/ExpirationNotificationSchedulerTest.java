package com.example.naengtal.domain.ingredientalarm.service;

import com.example.naengtal.domain.fridge.dao.FridgeRepository;
import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.ingredientalarm.dao.IngredientAlarmRepository;
import com.example.naengtal.domain.ingredientalarm.entity.IngredientAlarm;
import com.example.naengtal.domain.member.dao.MemberRepository;
import com.example.naengtal.domain.member.entity.Member;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ExpirationNotificationSchedulerTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private FridgeRepository fridgeRepository;
    @Autowired
    private ExpirationNotificationScheduler expirationNotificationScheduler;
    @Autowired
    private IngredientAlarmRepository ingredientAlarmRepository;

    @BeforeEach
    void beforeEach() {
        // 냉장고 생성
        Fridge fridge = new Fridge(1);
        fridgeRepository.save(fridge);

        // 회원 가입
        Member member = Member.builder()
                .name("test")
                .id("test")
                .password("test1234")
                .fridge(fridge)
                .build();

        memberRepository.save(member);

        Ingredient ingredient1 = Ingredient.builder()
                .name("재료1")
                .category("분류1")
                .expirationDate(LocalDate.now().plusDays(1))
                .image("image")
                .fridge(fridge)
                .build();
        Ingredient ingredient2 = Ingredient.builder()
                .name("재료2")
                .category("분류2")
                .expirationDate(LocalDate.now())
                .image("image")
                .fridge(fridge)
                .build();
        Ingredient ingredient3 = Ingredient.builder()
                .name("재료3")
                .category("분류3")
                .expirationDate(LocalDate.now().minusDays(1))
                .image("image")
                .fridge(fridge)
                .build();

        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
    }

    @AfterEach
    void afterEach(){
        ingredientRepository.deleteAll();
        memberRepository.deleteAll();
        fridgeRepository.deleteAll();
    }

    @Test
    void scheduler_send_alarm_success() {
        expirationNotificationScheduler.schedule();

        IngredientAlarm ingredientAlarm1 = ingredientAlarmRepository.findById(1).orElse(null);
        assertThat(ingredientAlarm1).isNotNull();
        assertThat(ingredientAlarm1.getText()).isEqualTo("재료3의 유통기한이 지났어요.");
        assertThat(ingredientAlarm1.getMember().getId()).isEqualTo("test");

        IngredientAlarm ingredientAlarm2 = ingredientAlarmRepository.findById(2).orElse(null);
        assertThat(ingredientAlarm2).isNotNull();
        assertThat(ingredientAlarm2.getText()).isEqualTo("재료2의 유통기한이 0일 남았어요.");

        IngredientAlarm ingredientAlarm3 = ingredientAlarmRepository.findById(3).orElse(null);
        assertThat(ingredientAlarm3).isNotNull();
        assertThat(ingredientAlarm3.getText()).isEqualTo("재료1의 유통기한이 1일 남았어요.");
    }
}