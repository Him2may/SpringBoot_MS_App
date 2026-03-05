package com.security_service.app.service;

import com.security_service.app.dto.RegisterRequest;
import com.security_service.app.entity.Role;
import com.security_service.app.entity.User;
import com.security_service.app.entity.UserPrincipal;
import com.security_service.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        System.out.println("Customer found with id: " + user.getEmail()+ " " + user.getPassword()) ;
        return UserPrincipal.create(user);
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(user == null){
            user = User.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role(Role.EMPLOYEE)
                    .employeeId(request.getEmployeeId()) // to change this logic later with emp service call
                    .build();
            user = userRepository.save(user);
        }
        return user;
    }


}
