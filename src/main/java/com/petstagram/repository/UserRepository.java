package com.petstagram.repository;

import com.petstagram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByNickName(String name);

    UserEntity findByEmail(String email);

    UserEntity findByName(String username);
}