package com.lms.presentation;
import com.lms.persistence.UpdateUserDto;
import com.lms.persistence.UserInfoDto;
import com.lms.persistence.User;
import com.lms.service.AuthenticationService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.lms.service.UserService;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService auth) {
        this.userService = userService;
        this.authenticationService=auth;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(currentUser.get());
    }

    @GetMapping("/viewInfo")
    public ResponseEntity<?> getAccountInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(401).body("Unauthorized: Please log in.");
        }
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = currentUser.get();
        UserInfoDto accountInfo = new UserInfoDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword()
        );
        return ResponseEntity.ok(accountInfo);
    }

    @PatchMapping("/updateInfo")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(401).body("Unauthorized: Please log in.");
        }
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUserOptional = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User currentUser = currentUserOptional.get();
        if (updateUserDto.getFirstName() != null) {
            currentUser.setFirstName(updateUserDto.getFirstName());
        }
        if (updateUserDto.getLastName() != null) {
            currentUser.setLastName(updateUserDto.getLastName());
        }
        if (updateUserDto.getPassword() != null) {
            currentUser.setPassword(updateUserDto.getPassword());
        }
        userService.save(currentUser);
        return ResponseEntity.ok("Your info is updated");
    }



//    @PostMapping("/createUser")
//    public ResponseEntity<?> createUser(@RequestBody RegisterUserDto registerUserDto) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
//        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
//        if (currentUser.isEmpty()) {
//            return ResponseEntity.status(404).body("Current user not found.");
//        }
//        if (!"Admin".equals(currentUser.get().getRole())) {
//            return ResponseEntity.status(403).body("Access Denied: Only Admins can create new users.");
//        }
//        User createdUser = authenticationService.signup(registerUserDto);
//
//        return ResponseEntity.ok(createdUser);
//    }



    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}



