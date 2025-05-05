package com.lms.presentation;

import com.lms.persistence.RegisterUserDto;
import com.lms.persistence.User;
import com.lms.service.AuthenticationService;
import com.lms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/admin")
@RestController
public class AdminController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AdminController(UserService userService, AuthenticationService auth) {
        this.userService = userService;
        this.authenticationService=auth;
    }


    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody RegisterUserDto registerUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).body("Current user not found.");
        }
        if (!"Admin".equals(currentUser.get().getRole())) {
            return ResponseEntity.status(403).body("Access Denied: Access Denied: you are unauthorized");
        }
        User createdUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(createdUser);
    }


}