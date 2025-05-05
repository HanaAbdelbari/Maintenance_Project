package com.lms.service;

import com.lms.persistence.Enrollment;
import com.lms.persistence.User;

import java.util.List;
import java.util.Map;

public interface EnrollmentService {

    void enrollStudent(String studentId, String courseId);
    List<Enrollment> getEnrollmentsByCourse(String courseId);
    List<Enrollment> getEnrollmentsByStudent(String studentId);
    Map<String, List<User>> getCourseStudentMap();
}
