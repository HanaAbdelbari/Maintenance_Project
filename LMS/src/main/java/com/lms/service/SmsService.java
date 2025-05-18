package com.lms.service;

import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import com.lms.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.lms.presentation.TwilioConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsService {

    @Autowired
    private TwilioConfig twilioConfig;
    Map<String, String> otpMap = new HashMap<>();
    private static ArrayList<Pair<String, Optional<User>>> lessonRequest = new ArrayList<>();


    public OtpResponseDto sendSMS(OtpRequest otpRequest, Optional<User> curr) {
        OtpResponseDto otpResponseDto = null;
        try {
              //removing unused variables(phone num form and to and the unused message)
            String otp = generateOTP();
            String otpMessage = "Dear Customer , Your OTP is  " + otp + " for sending sms through Spring boot application. Thank You.";
            //remove unused messege object
            otpMap.put(otpRequest.getUsername(), otp);
            lessonRequest.add(Pair.of(otpRequest.getLessonName(), curr));
            otpResponseDto = new OtpResponseDto(OtpStatus.DELIVERED, otpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            otpResponseDto = new OtpResponseDto(OtpStatus.FAILED, e.getMessage());
        }
        return otpResponseDto;
    }

    public String validateOtp(OtpValidationRequest otpValidationRequest, Optional<User> currentUser) {
        Set<String> keys = otpMap.keySet();
        String username = null;
        for(String key : keys)
            username = key;
        if (otpValidationRequest.getUsername().equals(username)) {
            otpMap.remove(username,otpValidationRequest.getOtpNumber());
            return "OTP is valid!";
        } else {
            currentUser.ifPresent(user ->
                    lessonRequest.removeIf(pair ->
                            pair.getRight().isPresent() && pair.getRight().get().equals(user)
                    )
            );
            return "OTP is invalid!";
        }
    }

    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    public static ArrayList<Pair<String, Optional<User>>> viewAttendance() {
        return lessonRequest;
    }


}