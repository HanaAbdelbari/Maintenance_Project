package com.lms.service;

import com.lms.persistence.Enrollment;
import com.lms.persistence.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final List<Enrollment> enrollmentList = new ArrayList<>();
    private final UserService userService; // Inject UserService

    // Constructor injection for UserService
    public EnrollmentServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void enrollStudent(String studentId, String courseId) {
        String enrollmentId = "E" + (enrollmentList.size() + 1); // Simple ID generation
        Enrollment enrollment = new Enrollment(enrollmentId, studentId, courseId);
        enrollmentList.add(enrollment);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(String courseId) {
        return enrollmentList.stream()
                .filter(e -> e.getcId().equals(courseId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(String studentId) {
        return enrollmentList.stream()
                .filter(e -> e.getsId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<User>> getCourseStudentMap() {
        return enrollmentList.stream()
                .collect(Collectors.groupingBy(
                        Enrollment::getcId,
                        Collectors.mapping(
                                enrollment -> {
                                    // Use UserService to find user by student ID
                                    Optional<User> userOptional = userService.findByEmail(enrollment.getsId());
                                    return userOptional.orElse(null); // Return null if user not found
                                },
                                Collectors.filtering(user -> user != null, Collectors.toList()) // Filter out null users
                        )
                ));
    }
}
