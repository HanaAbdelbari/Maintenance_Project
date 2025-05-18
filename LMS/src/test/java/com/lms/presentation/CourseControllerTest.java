package com.lms.presentation;

import com.lms.persistence.Course;
import com.lms.persistence.Lesson;
import com.lms.persistence.User;
import com.lms.service.UserService;
import com.lms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        User instructor = new User();
        instructor.setId("user1");
        instructor.setEmail("instructor@example.com");
        instructor.setRole("Instructor");
        userService.save(instructor);

        User student = new User();
        student.setId("user2");
        student.setEmail("student@example.com");
        student.setRole("Student");
        userService.save(student);
    }

    @Test
    @WithMockUser(username = "email@email.com", authorities = "ROLE_Instructor")
    void createCourse_Success() throws Exception {
        String courseJson = "{\"id\":\"course1_test\",\"title\":\"Test Course\",\"description\":\"A test course\",\"duration\":30}";

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(username = "email@email.com", authorities = "ROLE_Instructor")
    void getAllCourses_Success() throws Exception {
        // Pre-create course
        Course course = new Course();
        course.setId("course1_test");
        course.setTitle("Test Course");
        course.setProfid("user1");
        course.setDescription("A test course");
        course.setDuration(30);
        courseService.createCourse(course);

        MvcResult result = mockMvc.perform(get("/courses/availableCourses"))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println("Actual Response: " + result.getResponse().getContentAsString());
    }



    @Test
    @WithMockUser(username = "email@email.com", authorities = "ROLE_Instructor")
    void getMediaForCourse_Success() throws Exception {
        // Pre-create course with media
        Course course = new Course();
        course.setId("course1_test");
        course.setTitle("Test Course");
        course.setProfid("user1");
        course.setDescription("A test course");
        course.setDuration(30);
        course.getMediaPaths().add("media/path1");
        courseService.createCourse(course);

        mockMvc.perform(get("/courses/course1_test/media"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@email.com", authorities = "ROLE_Instructor")
    void getMediaForCourse_UserNotFound() throws Exception {
        // Pre-create course
        Course course = new Course();
        course.setId("course1_test");
        course.setTitle("Test Course");
        course.setProfid("user1");
        course.setDescription("A test course");
        course.setDuration(30);
        courseService.createCourse(course);

        mockMvc.perform(get("/courses/course1_test/media"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Course could not be created no User Exists\"]"));
    }

    @Test
    @WithMockUser(username = "email@email.com", authorities = "ROLE_Instructor")
    void addLessonToCourse_Success() throws Exception {
        Course course = new Course();
        course.setId("course1_test");
        course.setTitle("Test Course");
        course.setProfid("user1");
        course.setDescription("A test course");
        course.setDuration(30);
        courseService.createCourse(course);

        String lessonJson = "{\"id\":\"lesson2\",\"title\":\"Test Lesson 2\"}";

        mockMvc.perform(post("/courses/course1_test/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(lessonJson))
                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(username = "email@email.com", authorities = "ROLE_Instructor")
    void getLessonsForCourse_Success() throws Exception {
        Course course = new Course();
        course.setId("course1_test");
        course.setTitle("Test Course");
        course.setProfid("user1");
        course.setDescription("A test course");
        course.setDuration(30);
        courseService.createCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId("lesson1");
        lesson.setTitle("Test Lesson");
        courseService.addLessonToCourse("course1_test", lesson);

        mockMvc.perform(get("/courses/course1_test/lessons"))
                .andExpect(status().isOk());
    }

    @Test
    void getLessonsForCourse_UserNotFound() throws Exception {
        Course course = new Course();
        course.setId("course1_test");
        course.setTitle("Test Course");
        course.setProfid("user1");
        course.setDescription("A test course");
        course.setDuration(30);
        courseService.createCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId("lesson1");
        lesson.setTitle("Test Lesson");
        courseService.addLessonToCourse("course1_test", lesson);

        mockMvc.perform(get("/courses/course1_test/lessons"))
                .andExpect(status().isOk()) ;
    }
}