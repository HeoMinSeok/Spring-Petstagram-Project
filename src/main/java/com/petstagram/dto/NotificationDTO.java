package com.petstagram.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
public class NotificationDTO {
    private final Long id;
    private final Long likerId;
    private final Long postId;
    private final String eventType;
    private final String regTime;

//    public NotificationDTO(Long id, Long likerId, Long postId, String eventType) {
//        this.id = id;
//        this.likerId = likerId;
//        this.postId = postId;
//        this.eventType = eventType;
//    }

    public NotificationDTO(Long id, Long likerId, Long postId, String eventType, LocalDateTime regTime) {
        this.id = id;
        this.likerId = likerId;
        this.postId = postId;
        this.eventType = eventType;
        this.regTime = regTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
