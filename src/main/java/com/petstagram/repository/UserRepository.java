package com.petstagram.repository;

import com.petstagram.dto.UserProfileDTO;
import com.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByNickName(String name);

    UserEntity findByEmail(String email);

    UserEntity findByName(String username);

    @Query("SELECT new com.petstagram.dto.UserProfileDTO(u.id, u.name, u.email) FROM UserEntity u")
    List<UserProfileDTO> findAllUserProfiles();
}