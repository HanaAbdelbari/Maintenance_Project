package com.lms.presentation;

import com.lms.events.CourseNotificationEvent;

import com.lms.events.NotificationEvent;
import com.lms.persistence.Course;
import com.lms.persistence.Lesson;
import com.lms.persistence.User;
import com.lms.service.AuthenticationService;
import com.lms.service.CourseService;
import com.lms.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final UserService userService;
    private final CourseService courseService;

    // A REmoving the unUsed injection AuthenticationService

    private ApplicationEventPublisher eventPublisher;

    public CourseController(CourseService courseService,/* AuthenticationService auth,*/ UserService user, ApplicationEventPublisher eventPublisher) {
        this.courseService = courseService;
        // A REmoving the unUsed Constructor  injection AuthenticationService

        this.userService=user;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody Course course) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());


        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        if (!"Instructor".equals(currentUser.get().getRole())) {
            return ResponseEntity.status(403).body("Access Denied: Access Denied: you are unauthorized");
        }
        if(courseService.findCourseById(course.getId()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Course Already Exists");
        }

        course.setProfid(currentUser.get().getId());
        Course newCourse = courseService.createCourse(course);
        String message = "Course " + course.getId() + " \"" + course.getTitle() + "\"" + " created successfully" ;
        eventPublisher.publishEvent(new NotificationEvent(this, currentUser.get().getId(), message, "EMAIL"));
        return ResponseEntity.ok("Course " + newCourse.getId() + " created successfully!");
    }

    @PostMapping("/{courseId}/media")
    public ResponseEntity<String> uploadMedia(@PathVariable String courseId, @RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        if (!"Instructor".equals(currentUser.get().getRole())) {
            return ResponseEntity.status(403).body("Access Denied: Access Denied: you are unauthorized");
        }
        Course course = courseService.findCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with ID: " + courseId);
        }
        String uploadDirectory = System.getProperty("user.dir") + "/Uploads";
        String filePath = uploadDirectory + File.separator + file.getOriginalFilename();

        try {
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            FileOutputStream fout = new FileOutputStream(filePath);
            fout.write(file.getBytes());
            fout.close();
            course.addMedia(filePath);

            String studentMessage = "course " + courseId + " \"" + course.getTitle() + "\"" + " media updated successfully";
            eventPublisher.publishEvent(new CourseNotificationEvent(this, courseId, studentMessage));
            String instructorMessage = "You updated course  " + courseId + " \"" + course.getTitle() + "\"" + " media successfully";
            eventPublisher.publishEvent(new NotificationEvent(this, currentUser.get().getId(), instructorMessage, "EMAIL"));

            return ResponseEntity.ok("File uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in uploading file: " + e.getMessage());
        }
    }


    @GetMapping("/{courseId}/media")
    public ResponseEntity<List<String>> getMediaForCourse(@PathVariable String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(courseService.getMediaForCourse(courseId));
    }

    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<String> addLessonToCourse(@PathVariable String courseId, @RequestBody Lesson lesson) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        if (!"Instructor".equals(currentUser.get().getRole())) {
            return ResponseEntity.status(403).body("Access Denied: Access Denied: you are unauthorized");
        }
        courseService.addLessonToCourse(courseId, lesson);
        return ResponseEntity.ok("Lesson added successfully!");
    }

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<List<Lesson>> getLessonsForCourse(@PathVariable String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(courseService.getLessonsForCourse(courseId));
    }

    @GetMapping("/availableCourses")
    public ResponseEntity<?> getAllCourses() {
        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> currentUser = userService.findByEmail(currentUserDetails.getUsername());
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        List<Course> courses = courseService.getAllCourses();

        return ResponseEntity.ok(courses);
    }

}

