package com.ecommerce.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.auth.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>{

    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByPhoneNumber(long phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(long phoneNumber);

}