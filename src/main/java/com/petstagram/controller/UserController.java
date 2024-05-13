package com.petstagram.controller;

import com.petstagram.domain.UserDTO;
import com.petstagram.entity.User;
import com.petstagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody UserDTO userDTO) {
        // 회원가입 서비스 호출
        UserDTO savedUser = userService.signUp(userDTO);
        // 저장된 사용자 정보를 응답으로 반환
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
