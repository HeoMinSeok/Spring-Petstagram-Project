package com.petstagram.entity;

import com.petstagram.domain.UserDTO;
import jakarta.persistence.*;
import lombok.*;

// 사용자
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long userNum; // 사용자의 고유 식별자. 직접 입력된 값으로 설정합니다.
    private String userEmail; // 사용자의 이메일 주소.
    private String userName; // 성명
    private String userId; // 성명
    private String userPassword; // 사용자의 비밀번호.

    public static User toEntity(UserDTO dto) {
        return User.builder()
                .userNum(dto.getUserNum())
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName())
                .userId(dto.getUserId())
                .userPassword(dto.getUserPassword())
                .build();
    }
}
