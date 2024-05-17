package com.petstagram.controller;

import com.petstagram.dto.UserDTO;
import com.petstagram.entity.UserEntity;
import com.petstagram.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/users/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO){
        try {
            UserDTO registeredUser = userManagementService.signup(userDTO);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userManagementService.login(userDTO));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userManagementService.refreshToken(userDTO));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<UserDTO> getAllUsers(){
        return ResponseEntity.ok(userManagementService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<UserDTO> getUSerByID(@PathVariable Long userId){
        return ResponseEntity.ok(userManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserEntity userEntity){
        return ResponseEntity.ok(userManagementService.updateUser(userId, userEntity));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<UserDTO> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDTO response = userManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<UserDTO> deleteUSer(@PathVariable Long userId){
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }
}
