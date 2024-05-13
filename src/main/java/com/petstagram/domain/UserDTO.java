package com.petstagram.domain;

import com.petstagram.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {
    private Long userNum; // 사용자의 고유 식별자. 직접 입력된 값으로 설정합니다.
    private String userName; // 성명
    private String userId; // 성명
    private String userEmail; // 사용자의 이메일 주소.
    private String userPassword; // 사용자의 비밀번호.

    public UserDTO(Long userNum, String userEmail, String userName, String userId, String userPassword) {
        this.userNum = userNum;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .userNum(user.getUserNum())
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .build();
    }
}
