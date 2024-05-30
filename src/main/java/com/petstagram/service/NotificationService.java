package com.petstagram.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstagram.dto.NotificationDTO;
import com.petstagram.entity.NotificationEntity;
import com.petstagram.entity.PostEntity;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.EmitterRepository;
import com.petstagram.repository.NotificationRepository;
import com.petstagram.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::sendKeepAliveMessage, 0, 15, TimeUnit.SECONDS);
    }

    private void sendKeepAliveMessage() {
        emitterRepository.getAllEmitters().forEach((userId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("KEEP_ALIVE").data("keep-alive"));
                } catch (IOException e) {
                    emitterRepository.removeEmitter(userId, emitter);
                }
            }
        });
    }

    public SseEmitter subscribe(Long userId) {
        /* sout 다 지우기 */
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterRepository.addEmitter(userId, emitter);

        emitter.onCompletion(() -> {
            System.out.println("Emitter completed for userId: " + userId);
            emitterRepository.removeEmitter(userId, emitter);
        });

        emitter.onTimeout(() -> {
            System.out.println("Emitter timeout for userId: " + userId);
            emitterRepository.removeEmitter(userId, emitter);
        });

        emitter.onError((e) -> {
            System.out.println("Emitter error for userId: " + userId + ", error: " + e.getMessage());
            emitterRepository.removeEmitter(userId, emitter);
        });

        try {
            emitter.send(SseEmitter.event().name("INIT").data("SSE connection established for userId: " + userId));
        } catch (IOException e) {
            System.err.println("Failed to send INIT event: " + e.getMessage());
        }

        return emitter;
    }

    public void sendNotification(Long userId, String eventType, Long likerId, Long postId) {
        PostEntity post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return; // 게시글이 존재하지 않으면 알림을 보내지 않음
        }

        Long postAuthorId = post.getAuthorId(); // 게시글 작성자의 userId

        // likerId와 게시글 작성자의 userId가 동일한 경우 알림 막음
        if (likerId.equals(postAuthorId)) {
            return;
        }

        // 알림이 이미 존재하는지 확인
        Optional<NotificationEntity> existingNotification = notificationRepository.findByUserIdAndLikerIdAndPostIdAndEventType(userId, likerId, postId, eventType);
        if (existingNotification.isPresent()) {
            return; // 중복 알림이므로 알림을 보내지 않음
        }

        saveNotification(userId, likerId, postId, eventType);

        var emitters = emitterRepository.getEmitters(userId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    NotificationDTO notificationData = new NotificationDTO(null, likerId, postId, eventType, LocalDateTime.now());
                    String jsonData = objectMapper.writeValueAsString(notificationData);
                    emitter.send(SseEmitter.event().name(eventType).data(jsonData));
                    System.out.println("Notification sent to emitter: " + jsonData);
                } catch (IOException e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                    emitterRepository.removeEmitter(userId, emitter);
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                    e.printStackTrace();
                    emitterRepository.removeEmitter(userId, emitter);
                }
            }
        }
    }

    public void saveNotification(Long userId, Long likerId, Long postId, String eventType) {
        NotificationEntity notification = NotificationEntity.builder()
                .user(UserEntity.builder().id(userId).build())
                .liker(UserEntity.builder().id(likerId).build())
                .post(PostEntity.builder().id(postId).build())
                .eventType(eventType)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationDTO> findByUserId(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUserIdOrderByRegTimeDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDto(NotificationEntity notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getLiker().getId(),
                notification.getPost().getId(),
                notification.getEventType(),
                notification.getRegTime()
        );
    }
}
