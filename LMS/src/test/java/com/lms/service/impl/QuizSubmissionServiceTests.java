package com.lms.service.impl;

import com.lms.persistence.entities.Quiz;
import com.lms.persistence.entities.QuizSubmission;
import com.lms.persistence.entities.questions.Question;
import com.lms.persistence.repositories.RepositoryFacade;
import com.lms.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuizSubmissionServiceTests {

    @Mock
    private RepositoryFacade repository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private QuizSubmissionServiceImpl service;

    private final String quizId = "Q01";
    private final String studentId = "S01";
    private final Map<String, String> studentAnswers = Map.of("Q1", "A1", "Q2", "A2");

    private Quiz quiz;

    @BeforeEach
    public void setup() {
        repository = mock(RepositoryFacade.class);
        courseService = mock(CourseService.class);
        service = new QuizSubmissionServiceImpl(repository, courseService);
        setupQuizAndQuestions();
    }

    private void setupQuizAndQuestions() {
        Question question1 = mock(Question.class);
        Question question2 = mock(Question.class);
        when(question1.getId()).thenReturn("Q1");
        when(question1.getQuestionText()).thenReturn("What is Java?");
        when(question1.getCorrectAnswer()).thenReturn("A programming language");
        when(question1.getGrade()).thenReturn(5);

        when(question2.getId()).thenReturn("Q2");
        when(question2.getQuestionText()).thenReturn("What is JDK?");
        when(question2.getCorrectAnswer()).thenReturn("Java Development Kit");
        when(question2.getGrade()).thenReturn(5);

        quiz = mock(Quiz.class);
        when(quiz.getSelectedQuestions()).thenReturn(List.of(question1, question2));
        when(quiz.getCourseId()).thenReturn("C01");
    }

    @Test
    public void testSubmitQuiz_Succeeds() {
        when(repository.studentExistsById(studentId)).thenReturn(true);
        when(repository.findQuizById(quizId)).thenReturn(Optional.of(quiz));
        when(repository.findQuizSubmissionByStudentIdAndQuizId(studentId, quizId)).thenReturn(false);

        QuizSubmission submission = service.submitQuiz(quizId, studentId, studentAnswers);

        assertNotNull(submission);
        assertEquals(quizId, submission.getQuizId());
        assertEquals(studentId, submission.getStudentId());
        assertEquals("C01", submission.getCourseId());
        assertEquals(2, submission.getStudentAnswers().size());

        verify(repository).saveQuizSubmission(submission);
    }

    @Test
    public void testSubmitQuiz_StudentNotFound_ThrowsRuntimeException() {
        when(repository.studentExistsById(studentId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.submitQuiz(quizId, studentId, studentAnswers));

        assertEquals("Student does not exist", exception.getMessage());
    }

    @Test
    public void testSubmitQuiz_QuizNotFound_ThrowsRuntimeException() {
        when(repository.studentExistsById(studentId)).thenReturn(true);
        when(repository.findQuizById(quizId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.submitQuiz(quizId, studentId, studentAnswers));

        assertEquals("Quiz does not exist", exception.getMessage());
    }

    @Test
    public void testSubmitQuiz_AlreadySubmitted_ThrowsRuntimeException() {
        when(repository.studentExistsById(studentId)).thenReturn(true);
        when(repository.findQuizById(quizId)).thenReturn(Optional.of(quiz));
        when(repository.findQuizSubmissionByStudentIdAndQuizId(studentId, quizId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.submitQuiz(quizId, studentId, studentAnswers));

        assertEquals("Student has already submitted the quiz", exception.getMessage());
    }

    @Test
    public void testSubmitQuiz_SkipsInvalidQuestions() {
        when(repository.studentExistsById(studentId)).thenReturn(true);
        when(repository.findQuizById(quizId)).thenReturn(Optional.of(quiz));
        when(repository.findQuizSubmissionByStudentIdAndQuizId(studentId, quizId)).thenReturn(false);

        Map<String, String> answersWithInvalidQuestion = Map.of("Q1", "A1", "Invalid", "Answer");
        QuizSubmission submission = service.submitQuiz(quizId, studentId, answersWithInvalidQuestion);

        assertNotNull(submission);
        assertEquals(1, submission.getStudentAnswers().size());
        assertEquals("Q1", submission.getStudentAnswers().get(0).getQuestionId());

        verify(repository).saveQuizSubmission(submission);
    }
}
