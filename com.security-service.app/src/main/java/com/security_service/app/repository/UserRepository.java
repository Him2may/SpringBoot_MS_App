package com.security_service.app.repository;

import com.security_service.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmployeeId(long id);
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
}
