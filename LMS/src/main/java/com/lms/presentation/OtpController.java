package com.lms.presentation;

import com.lms.persistence.OtpRequest;
import com.lms.persistence.OtpResponseDto;
import com.lms.persistence.OtpValidationRequest;
import com.lms.persistence.User;
import com.lms.service.AuthenticationService;
import com.lms.service.CourseService;
import com.lms.service.SmsService;
import com.lms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/otp")
@Slf4j
public class OtpController {
    private final UserService userService;
    private final CourseService courseService;
    private final AuthenticationService authenticationService;

    public OtpController(UserService userService, CourseService courseService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.courseService = courseService;
        this.authenticationService = authenticationService;
    }

    @Autowired
    private SmsService smsService;

    @GetMapping("/process")
    public String processSMS() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        return "SMS sent";
    }

    @PostMapping("/send-otp")
    public OtpResponseDto sendOtp(@RequestBody OtpRequest otpRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        log.info("Inside sendOtp :: " + otpRequest.getUsername());
        return smsService.sendSMS(otpRequest,currentUser);
    }

    @PostMapping("/validate-otp")
    public String validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        log.info("Inside validateOtp :: " + otpValidationRequest.getUsername() + " " + otpValidationRequest.getOtp());
        return smsService.validateOtp(otpValidationRequest, currentUser);
    }

    @GetMapping("/viewAttendance")
    public ResponseEntity<ArrayList<Pair<String, Optional<User>>>> getMediaForCourse() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(SmsService.viewAttendance());
    }
}
