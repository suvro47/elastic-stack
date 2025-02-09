package com.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
