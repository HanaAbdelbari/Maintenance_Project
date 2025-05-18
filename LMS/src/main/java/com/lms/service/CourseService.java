package com.lms.service;

import com.lms.persistence.Course;
import com.lms.persistence.Lesson;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    void uploadMedia(String courseId, MultipartFile file) throws IOException;
    List<String> getMediaForCourse(String courseId);
    void addLessonToCourse(String courseId, Lesson lesson);
    List<Lesson> getLessonsForCourse(String courseId);
    Course findCourseById(String courseId);

    public List<Course> getAllCourses();
}