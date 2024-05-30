package com.petstagram.repository;

import com.petstagram.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserIdOrderByRegTimeDesc(Long userId);

    // 중복 알림 확인
    Optional<NotificationEntity> findByUserIdAndLikerIdAndPostIdAndEventType(Long userId, Long likerId, Long postId, String eventType);
}
