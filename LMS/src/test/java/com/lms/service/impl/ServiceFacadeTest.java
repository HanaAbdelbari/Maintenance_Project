package com.lms.service.impl;

import com.lms.business.models.AssignmentSubmissionModel;
import com.lms.business.models.CourseProgress;
import com.lms.business.models.StudentProgress;
import com.lms.persistence.Course;
import com.lms.persistence.User;
import com.lms.persistence.entities.AssignmentEntity;
import com.lms.persistence.entities.AssignmentSubmissionEntity;
import com.lms.persistence.entities.Quiz;
import com.lms.persistence.entities.QuizSubmission;
import com.lms.persistence.entities.questions.Question;
import com.lms.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServiceFacadeTest {

    @Mock
    private AssignmentService assignmentService;
    @Mock
    private QuizService quizService;
    @Mock
    private AssignmentSubmissionService assignmentSubmissionService;
    @Mock
    private QuestionBankService questionBankService;
    @Mock
    private QuizSubmissionService quizSubmissionService;
    @Mock
    private UserService userService;
    @Mock
    private CourseService courseService;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private ProgressService progressService;
    @Mock
    private User mockUser;
    @Mock
    private Authentication authentication;

    private ServiceFacade serviceFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceFacade = new ServiceFacade(
                assignmentService,
                quizService,
                assignmentSubmissionService,
                questionBankService,
                quizSubmissionService,
                userService,
                courseService,
                enrollmentService,
                progressService
        );
    }

    @Test
    void testCreateQuiz() {
        String courseId = "course1";
        String name = "Quiz 1";
        String instructorId = "instructor1";
        int questionsNumber = 10;
        int duration = 30;
        String status = "active";

        Quiz mockQuiz = new Quiz();
        when(quizService.createQuiz(anyString(), anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(mockQuiz);

        Quiz result = serviceFacade.createQuiz(courseId, name, instructorId, questionsNumber, duration, status);

        assertNotNull(result);
        verify(quizService, times(1)).createQuiz(courseId, name, instructorId, questionsNumber, duration, status);
    }

    @Test
    void testSubmitAssignment() {
        int assignmentId = 1;
        String studentId = "student1";
        AssignmentSubmissionModel model = new AssignmentSubmissionModel(studentId, "", null, null, null);

        when(assignmentSubmissionService.submitAssignment(any(), anyInt(), anyString())).thenReturn(true);

        boolean result = serviceFacade.submitAssignment(model, assignmentId, studentId);

        assertTrue(result);
        verify(assignmentSubmissionService, times(1)).submitAssignment(model, assignmentId, studentId);
    }

    @Test
    void testGetAssignmentSubmissionsByStudent() {
        String studentId = "student1";
        List<AssignmentSubmissionEntity> mockSubmissions = new ArrayList<>();
        when(assignmentSubmissionService.getSubmissionsByStudent(anyString())).thenReturn(mockSubmissions);

        List<AssignmentSubmissionEntity> result = serviceFacade.getAssignmentSubmissionsByStudent(studentId);

        assertEquals(mockSubmissions, result);
        verify(assignmentSubmissionService, times(1)).getSubmissionsByStudent(studentId);
    }

    @Test
    void testGetCurrentUser() {
        String username = "user1";
        User mockUser = mock(User.class);  // Mock the User object
        Authentication authentication = mock(Authentication.class);  // Mock Authentication

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(mock(SecurityContext.class));
            when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(mockUser);
            when(mockUser.getUsername()).thenReturn(username);
            when(userService.findByEmail(username)).thenReturn(Optional.of(mockUser));

            Optional<User> result = serviceFacade.getCurrentUser();

            assertTrue(result.isPresent());
            assertEquals(mockUser, result.get());
        }
    }

    @Test
    void testAddQuestion() {
        String courseId = "course1";
        Question question = mock(Question.class);

        serviceFacade.addQuestion(courseId, question);

        verify(questionBankService, times(1)).addQuestion(courseId, question);
    }

    @Test
    void testFindCourseById() {
        String courseId = "course1";
        Course mockCourse = new Course(courseId, "JAVA", "Programming language", 3, "P01");
        when(courseService.findCourseById(anyString())).thenReturn(mockCourse);

        Course result = serviceFacade.findCourseById(courseId);

        assertNotNull(result);
        verify(courseService, times(1)).findCourseById(courseId);
    }

    @Test
    void testDeleteAssignment() {
        int assignmentId = 1;
        String courseId = "course1";
        when(assignmentService.deleteAssignment(anyInt(), anyString())).thenReturn(true);

        boolean result = serviceFacade.deleteAssignment(assignmentId, courseId);

        assertTrue(result);
        verify(assignmentService, times(1)).deleteAssignment(assignmentId, courseId);
    }

    @Test
    void testGetAssignmentsByCourse() {
        String courseId = "course1";
        List<AssignmentEntity> mockAssignments = new ArrayList<>();
        when(assignmentService.getAssignmentsByCourse(anyString())).thenReturn(mockAssignments);

        List<AssignmentEntity> result = serviceFacade.getAssignmentsByCourse(courseId);

        assertEquals(mockAssignments, result);
        verify(assignmentService, times(1)).getAssignmentsByCourse(courseId);
    }

    @Test
    void testMarkQuizAsDeleted() {
        String quizId = "quiz1";

        // No return value for this method, just verify interaction
        doNothing().when(quizService).markQuizAsDeleted(quizId);

        serviceFacade.markQuizAsDeleted(quizId);

        verify(quizService, times(1)).markQuizAsDeleted(quizId);
    }

    @Test
    void testGetAssignmentSubmissionsByCourse() {
        String courseId = "course1";
        List<AssignmentSubmissionEntity> mockSubmissions = mock(List.class);

        // Mocking AssignmentSubmissionService
        when(assignmentSubmissionService.getSubmissionsByCourse(courseId)).thenReturn(mockSubmissions);

        List<AssignmentSubmissionEntity> result = serviceFacade.getAssignmentSubmissionsByCourse(courseId);

        assertNotNull(result);
        assertEquals(mockSubmissions, result);
    }

    @Test
    void testGetQuizSubmissionsByStudent() {
        String studentId = "student1";
        List<QuizSubmission> mockSubmissions = mock(List.class);

        // Mocking QuizSubmissionService
        when(quizSubmissionService.getSubmissionsByStudent(studentId)).thenReturn(mockSubmissions);

        List<QuizSubmission> result = serviceFacade.getQuizSubmissionsByStudent(studentId);

        assertNotNull(result);
        assertEquals(mockSubmissions, result);
    }

    @Test
    void testGetAllCourses() {
        List<Course> mockCourses = mock(List.class);

        // Mocking CourseService
        when(courseService.getAllCourses()).thenReturn(mockCourses);

        List<Course> result = serviceFacade.getAllCourses();

        assertNotNull(result);
        assertEquals(mockCourses, result);
    }

    @Test
    void testEnrollStudent() {
        String studentId = "student1";
        String courseId = "course1";

        // Mocking EnrollmentService
        doNothing().when(enrollmentService).enrollStudent(studentId, courseId);

        serviceFacade.enrollStudent(studentId, courseId);

        verify(enrollmentService, times(1)).enrollStudent(studentId, courseId);
    }

    @Test
    void testGetStudentProgressByStudentIdAndCourseId() {
        String studentId = "student1";
        String courseId = "course1";
        StudentProgress mockProgress = mock(StudentProgress.class);

        // Mocking ProgressService
        when(progressService.getStudentProgressByStudentIdAndCourseId(studentId, courseId)).thenReturn(mockProgress);

        StudentProgress result = serviceFacade.getStudentProgressByStudentIdAndCourseId(studentId, courseId);

        assertNotNull(result);
        assertEquals(mockProgress, result);
    }

    @Test
    void testCreateCourse() {
        Course mockCourse = mock(Course.class);

        // Mocking CourseService
        when(courseService.createCourse(mockCourse)).thenReturn(mockCourse); // Mock the return value

        // Call the service facade method
        Course result = serviceFacade.createCourse(mockCourse);

        // Verify the interaction with the mock and the result
        verify(courseService, times(1)).createCourse(mockCourse);
        assertEquals(mockCourse, result); // Ensure the result matches the mock course
    }

    @Test
    void testUploadMedia() {
        String courseId = "course1";
        MultipartFile mockFile = mock(MultipartFile.class);

        // Mocking CourseService
        doNothing().when(courseService).uploadMedia(courseId, mockFile);

        serviceFacade.uploadMedia(courseId, mockFile);

        verify(courseService, times(1)).uploadMedia(courseId, mockFile);
    }

    @Test
    void testGetCourseProgress() {
        String courseId = "course1";
        CourseProgress mockProgress = mock(CourseProgress.class);

        // Mocking ProgressService
        when(progressService.getCourseProgress(courseId)).thenReturn(mockProgress);

        CourseProgress result = serviceFacade.getCourseProgress(courseId);

        assertNotNull(result);
        assertEquals(mockProgress, result);
    }

}
