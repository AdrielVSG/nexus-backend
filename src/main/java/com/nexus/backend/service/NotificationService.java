package com.nexus.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.nexus.backend.entity.Notification;
import com.nexus.backend.entity.User;
import com.nexus.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(User user, String message, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();
        notificationRepository.save(notification);
        
        System.out.println("Salvando Notificação no Banco para " + user.getEmail() + ": " + message);

        // Enviar Push Real via Firebase se o usuário tiver um token e o Firebase estiver configurado
        if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                Message firebaseMessage = Message.builder()
                        .setToken(user.getFcmToken())
                        .putData("title", "Nexus")
                        .putData("body", message)
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle("Nexus")
                                .setBody(message)
                                .build())
                        .build();
                
                FirebaseMessaging.getInstance().send(firebaseMessage);
                System.out.println("Push enviado via Firebase com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao enviar push via Firebase: " + e.getMessage());
            }
        }
    }

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findAllByUserOrderByTimestampDesc(user);
    }
}
