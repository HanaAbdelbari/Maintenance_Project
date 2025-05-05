package com.lms.service.impl;

import com.lms.persistence.Course;
import com.lms.persistence.entities.QuestionBank;
import com.lms.persistence.entities.Quiz;
import com.lms.persistence.entities.questions.Question;
import com.lms.persistence.repositories.RepositoryFacade;
import com.lms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuizServiceTests {

    @Mock
    private RepositoryFacade repository;

    @Mock
    private CourseService courseService;

    private QuizServiceImpl service;

    private final String courseId = "C01";
    private final String instructorId = "I01";
    private final String quizName = "Sample Quiz";
    private final int questionsNumber = 2;
    private final int duration = 30;
    private final String status = "created";
    private final String quizId = "Q01";

    private Question question1;
    private Question question2;

    @BeforeEach
    public void setup() {
        repository = mock(RepositoryFacade.class);
        courseService = mock(CourseService.class);
        service = new QuizServiceImpl(repository, courseService);
        setupQuestions();
    }

    private void setupQuestions() {
        question1 = mock(Question.class);
        question2 = mock(Question.class);
    }

    @Test
    public void testCreateQuiz_Succeeds() {
        QuestionBank questionBank = new QuestionBank();
        questionBank.setQuestions(new ArrayList<>(List.of(question1, question2)));

        when(courseService.findCourseById(courseId)).thenReturn(mock(Course.class));
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(questionBank);

        Quiz quiz = service.createQuiz(courseId, quizName, instructorId, questionsNumber, duration, status);

        assertNotNull(quiz);
        assertEquals(courseId, quiz.getCourseId());
        assertEquals(quizName, quiz.getName());
        assertEquals(questionsNumber, quiz.getNumberOfQuestions());
        assertEquals(duration, quiz.getDuration());
        assertEquals(status, quiz.getStatus());
        assertEquals(questionsNumber, quiz.getSelectedQuestions().size());

        verify(repository).saveQuiz(any(Quiz.class));
        verify(repository, times(1)).saveQuiz(any(Quiz.class));
    }

    @Test
    public void testCreateQuiz_CourseNotFound_ThrowsIllegalArgumentException() {
        when(courseService.findCourseById(courseId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.createQuiz(courseId, quizName, instructorId, questionsNumber, duration, status));

        assertEquals("Course with id " + courseId + " does not exist", exception.getMessage());
    }

    @Test
    public void testCreateQuiz_NotEnoughQuestions_ThrowsIllegalArgumentException() {
        QuestionBank questionBank = new QuestionBank();
        questionBank.setQuestions(List.of(question1));

        when(courseService.findCourseById(courseId)).thenReturn(mock(Course.class));
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(questionBank);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.createQuiz(courseId, quizName, instructorId, questionsNumber, duration, status));

        assertEquals("Not enough questions in the question bank to create the quiz.", exception.getMessage());
    }

    @Test
    public void testMarkQuizAsDeleted_Succeeds() {
        Quiz quiz = new Quiz(quizId, courseId, quizName, instructorId, questionsNumber, duration, status, new ArrayList<>());

        when(repository.findQuizById(quizId)).thenReturn(Optional.of(quiz));

        service.markQuizAsDeleted(quizId);

        assertEquals("deleted", quiz.getStatus());

        verify(repository, times(1)).findQuizById(quizId);
        verify(repository, times(1)).updateQuiz(quiz);
    }

    @Test
    public void testMarkQuizAsOpened_Succeeds() {
        Quiz quiz = new Quiz(quizId, courseId, quizName, instructorId, questionsNumber, duration, "created", new ArrayList<>());

        when(repository.findQuizById(quizId)).thenReturn(Optional.of(quiz));

        service.markQuizAsOpened(quizId);

        assertEquals("opened", quiz.getStatus());
        verify(repository).updateQuiz(quiz);
    }

    @Test
    public void testGetAllQuizzes_Succeeds() {
        List<Quiz> quizzes = List.of(
                new Quiz(quizId, courseId, quizName, instructorId, questionsNumber, duration, status, new ArrayList<>())
        );

        when(repository.findAllQuizzes()).thenReturn(quizzes);

        List<Quiz> result = service.getAllQuizzes();

        assertEquals(quizzes, result);
    }

    @Test
    public void testGetQuizById_Succeeds() {
        Quiz quiz = new Quiz(quizId, courseId, quizName, instructorId, questionsNumber, duration, status, new ArrayList<>());

        when(repository.findQuizById(quizId)).thenReturn(Optional.of(quiz));

        Quiz result = service.getQuizById(quizId);

        assertEquals(quiz, result);
    }

    @Test
    public void testGetQuizById_NotFound_ReturnsNull() {
        when(repository.findQuizById(quizId)).thenReturn(Optional.empty());

        Quiz result = service.getQuizById(quizId);

        assertNull(result);
        verify(repository, times(1)).findQuizById(quizId);
        System.out.println("Quiz not found for ID: " + quizId);
    }
}
