package com.lms.service;

import com.lms.persistence.User;
import com.lms.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public List<String> getAllStudentIds() {
        return userRepository.getAllStudentIds();
    }

    public boolean existsById(String studentId) {
        return userRepository.existsById(studentId);
    }

    public User findById(String userId) {
        return userRepository.findById(userId);
    }
}


