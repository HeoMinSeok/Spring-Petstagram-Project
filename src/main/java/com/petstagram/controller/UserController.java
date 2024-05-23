package com.petstagram.controller;

import com.petstagram.dto.UserDTO;
import com.petstagram.dto.UserProfileDTO;
import com.petstagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {
        try {
            UserDTO registeredUser = userService.signup(userDTO);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.login(userDTO));
    }

    // 새로고침 토큰
    @PostMapping("/refresh")
    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.refreshToken(userDTO));
    }

    // 회원수정
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    // 회원 프로필 편집
    @PutMapping(value = "/edit/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> editUser(@PathVariable Long userId,
                                           @RequestPart("user") UserDTO userDTO,
                                           @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            System.out.println("수신된 userDTO 데이터: " + userDTO);

            userService.editUser(userId, userDTO, file);
            return ResponseEntity.ok("수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }
    }

    // 회원탈퇴
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("회원을 탈퇴하셨습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴에 실패했습니다.");
        }
    }

    // 회원 마이페이지
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDTO response = userService.getMyInfo(email);
        return ResponseEntity.ok(response);
    }

    // 회원 모두 조회
    @GetMapping("/getAllUsers")
    public List<UserProfileDTO> getAllUserProfiles() {
        return userService.getAllUserProfiles();
    }

    // 회원 한명 조회
    @GetMapping("/get/{userId}")
    public ResponseEntity<UserDTO> getUSerByID(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUsersById(userId));
    }

    // 팔로워 갯수
    @GetMapping("/followersCount/{userId}")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable Long userId) {
        int count = userService.getFollowersCount(userId);
        return ResponseEntity.ok(count);
    }

    // 팔로잉 갯수
    @GetMapping("/followingsCount/{userId}")
    public ResponseEntity<Integer> getFollowingsCount(@PathVariable Long userId) {
        int count = userService.getFollowingsCount(userId);
        return ResponseEntity.ok(count);
    }
}
