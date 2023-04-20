package com.example.naengtal.global.common.service;

import com.example.naengtal.domain.alarm.dto.FcmInvitationDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FcmService {

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;


    @PostConstruct
    public void init() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials
                                .fromStream(new FileInputStream(FCM_PRIVATE_KEY_PATH))
                                )
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public void sendByTokenList(List<String> tokenList, FcmInvitationDto dto) {
        MulticastMessage multicastMessage = makeMessages(tokenList, dto);

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(multicastMessage);

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.error("List of tokens are not valid FCM token : " + failedTokens);
            }
        } catch (FirebaseMessagingException e) {
                log.error("cannot send to memberList push message. error info : {}", e.getMessage());
        }

    }

    public MulticastMessage makeMessages(List<String> tokenList, FcmInvitationDto dto) {
        return MulticastMessage.builder()
                .addAllTokens(tokenList)
                .setNotification(
                        Notification.builder()
                                .setTitle(dto.getTitle())
                                .setBody(dto.getBody())
                                .build()
                )
                .putData("type", dto.getType().name())
                .build();
    }
}
