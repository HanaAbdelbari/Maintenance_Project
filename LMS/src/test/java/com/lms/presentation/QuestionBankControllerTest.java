package com.lms.presentation;

import com.lms.business.models.QuestionRequest;
import com.lms.persistence.Course;
import com.lms.persistence.User;
import com.lms.persistence.entities.questions.Question;
import com.lms.service.impl.ServiceFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.*;

class QuestionBankControllerTest {

    @InjectMocks
    private QuestionBankController questionBankController;

    @Mock
    private ServiceFacade service;

    @Mock
    private User currentUser;

    @Mock
    private Question question;

    @Mock
    private QuestionRequest questionRequest;

    private final String courseId = "courseId";
    private final String questionId = "questionId";
    private final Course course = new Course(courseId, "JAVA", "Programming language", 3, "P01");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddQuestion_unauthenticated() {
        when(service.getCurrentUser()).thenReturn(Optional.empty());

        ResponseEntity<String> response = questionBankController.addQuestion(courseId, questionRequest);

        verify(service, never()).addQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
        assert Objects.equals(response.getBody(), "Not authenticated");
    }

    @Test
    void testAddQuestion_forbidden() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Student");

        ResponseEntity<String> response = questionBankController.addQuestion(courseId, questionRequest);

        verify(service, never()).addQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.FORBIDDEN;
        assert Objects.equals(response.getBody(), "Access Denied: You are unauthorized");
    }

    @Test
    void testAddQuestion_courseNotFound() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Instructor");
        when(service.findCourseById(courseId)).thenReturn(null);

        ResponseEntity<String> response = questionBankController.addQuestion(courseId, questionRequest);

        verify(service, never()).addQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert Objects.equals(response.getBody(), "Course not found");
    }

    @Test
    void testAddQuestion_success() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Instructor");
        when(service.findCourseById(courseId)).thenReturn(course);

//        // Mock the type and other fields for the questionRequest
        when(questionRequest.getType()).thenReturn("MCQ");
        when(questionRequest.getQuestionText()).thenReturn("Sample question");
        when(questionRequest.getGrade()).thenReturn(5);
        when(questionRequest.getOptions()).thenReturn(List.of("Option 1", "Option 2"));
        when(questionRequest.getCorrectAnswer()).thenReturn("Option 1");

        ResponseEntity<String> response = questionBankController.addQuestion(courseId, questionRequest);

        verify(service).addQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.OK;
        assert Objects.equals(response.getBody(), "Question added successfully.");
    }

    @Test
    void testAddQuestions_unauthenticated() {
        when(service.getCurrentUser()).thenReturn(Optional.empty());

        ResponseEntity<String> response = questionBankController.addQuestions(courseId, List.of(questionRequest));

        verify(service, never()).addQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
        assert Objects.equals(response.getBody(), "Not authenticated");
    }

    @Test
    void testDeleteQuestion_unauthenticated() {
        when(service.getCurrentUser()).thenReturn(Optional.empty());

        ResponseEntity<String> response = questionBankController.deleteQuestion(courseId, questionId);

        verify(service, never()).deleteQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
        assert Objects.equals(response.getBody(), "Not authenticated");
    }

    @Test
    void testDeleteQuestion_forbidden() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Student");

        ResponseEntity<String> response = questionBankController.deleteQuestion(courseId, questionId);

        verify(service, never()).deleteQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.FORBIDDEN;
        assert Objects.equals(response.getBody(), "Access Denied: You are unauthorized");
    }

    @Test
    void testDeleteQuestion_courseNotFound() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Instructor");
        when(service.findCourseById(courseId)).thenReturn(null);

        ResponseEntity<String> response = questionBankController.deleteQuestion(courseId, questionId);

        verify(service, never()).deleteQuestion(any(), any());
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert Objects.equals(response.getBody(), "Course not found");
    }

    @Test
    void testDeleteQuestion_success() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Instructor");
        when(service.findCourseById(courseId)).thenReturn(course);

        ResponseEntity<String> response = questionBankController.deleteQuestion(courseId, questionId);

        verify(service).deleteQuestion(courseId, questionId);
        assert response.getStatusCode() == HttpStatus.OK;
        assert Objects.equals(response.getBody(), "Question deleted successfully.");
    }

    @Test
    void testGetQuestions_unauthenticated() {
        when(service.getCurrentUser()).thenReturn(Optional.empty());

        ResponseEntity<Object> response = questionBankController.getQuestions(courseId);

        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
        assert Objects.equals(response.getBody(), List.of());
    }

    @Test
    void testGetQuestions_forbidden() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Guest");

        ResponseEntity<Object> response = questionBankController.getQuestions(courseId);

        assert response.getStatusCode() == HttpStatus.FORBIDDEN;
        assert Objects.equals(response.getBody(), "Access Denied: You are unauthorized");
    }

    @Test
    void testGetQuestions_courseNotFound() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Instructor");
        when(service.findCourseById(courseId)).thenReturn(null);

        ResponseEntity<Object> response = questionBankController.getQuestions(courseId);

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert Objects.equals(response.getBody(), "Course not found");
    }

    @Test
    void testGetQuestions_success() {
        when(service.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(currentUser.getRole()).thenReturn("Instructor");
        when(service.findCourseById(courseId)).thenReturn(course);
        when(service.getQuestions(courseId)).thenReturn(List.of(question));

        ResponseEntity<Object> response = questionBankController.getQuestions(courseId);

        assert response.getStatusCode() == HttpStatus.OK;
        assert Objects.equals(response.getBody(), List.of(question));
    }
}
