package com.petstagram.service;

import com.petstagram.domain.UserDTO;
import com.petstagram.entity.User;
import com.petstagram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO signUp(UserDTO userDTO) {
        // 중복된 userId 확인
        if (existsByUserId(userDTO.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // UserDTO를 User Entity로 변환
        User user = User.toEntity(userDTO);

        // 사용자 등록
        User savedUser = userRepository.save(user);

        // 저장된 사용자 정보를 다시 DTO로 변환하여 반환
        return UserDTO.toDTO(savedUser);
    }

    public boolean existsByUserId(String userId) {
        // UserRepository에서 userId로 사용자를 조회하여 존재 여부를 확인
        return userRepository.existsByUserId(userId);
    }
}


