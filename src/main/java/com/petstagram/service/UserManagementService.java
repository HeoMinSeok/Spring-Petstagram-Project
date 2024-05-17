package com.petstagram.service;

import com.petstagram.dto.UserDTO;
import com.petstagram.entity.UserEntity;
import com.petstagram.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO signup(UserDTO userDTO){
        UserDTO resp = new UserDTO();

        try {
            UserEntity userEntity = new UserEntity();
            userEntity.setName(userDTO.getName());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userEntity.setRole("USER");
            UserEntity userEntityResult = userRepository.save(userEntity);
            if (userEntityResult.getId()>0) {
                resp.setUserEntity((userEntityResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    public UserDTO login(UserDTO userDTO){
        UserDTO response = new UserDTO();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(),
                            userDTO.getPassword()));
            var user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    public UserDTO refreshToken(UserDTO userDTO){
        log.info("POST /auth/refresh called with userDTO: {}", userDTO);
        UserDTO response = new UserDTO();
        try{
            String ourEmail = jwtUtils.extractUsername(userDTO.getToken());
            UserEntity users = userRepository.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(userDTO.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(userDTO.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            log.info("Token refreshed for user: {}", userDTO.getEmail());
            return response;
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public UserDTO getAllUsers() {
        UserDTO userDTO = new UserDTO();

        try {
            List<UserEntity> result = userRepository.findAll();
            if (!result.isEmpty()) {
                userDTO.setUserEntityList(result);
                userDTO.setStatusCode(200);
                userDTO.setMessage("Successful");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("No users found");
            }
            return userDTO;
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred: " + e.getMessage());
            return userDTO;
        }
    }


    public UserDTO getUsersById(Long userId) {
        UserDTO userDTO = new UserDTO();
        try {
            UserEntity usersById = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not found"));
            userDTO.setUserEntity(usersById);
            userDTO.setStatusCode(200);
            userDTO.setMessage("Users with id '" + userId + "' found successfully");
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred: " + e.getMessage());
        }
        return userDTO;
    }


    public UserDTO deleteUser(Long userId) {
        UserDTO userDTO = new UserDTO();
        try {
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
                userDTO.setStatusCode(200);
                userDTO.setMessage("User deleted successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return userDTO;
    }

    public UserDTO updateUser(Long userId, UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        try {
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                UserEntity existingUser = userOptional.get();
                existingUser.setEmail(userEntity.getEmail());
                existingUser.setName(userEntity.getName());
                existingUser.setRole(userEntity.getRole());

                // Check if password is present in the request
                if (userEntity.getPassword() != null && !userEntity.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(userEntity.getPassword()));
                }

                UserEntity savedUser = userRepository.save(existingUser);
                userDTO.setUserEntity(savedUser);
                userDTO.setStatusCode(200);
                userDTO.setMessage("User updated successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found for update");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return userDTO;
    }


    public UserDTO getMyInfo(String email){
        UserDTO userDTO = new UserDTO();
        try {
            Optional<UserEntity> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                userDTO.setUserEntity(userOptional.get());
                userDTO.setStatusCode(200);
                userDTO.setMessage("successful");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found for update");
            }

        }catch (Exception e){
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return userDTO;

    }
}