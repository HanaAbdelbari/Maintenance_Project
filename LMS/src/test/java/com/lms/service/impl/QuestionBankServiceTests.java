package com.lms.service.impl;

import com.lms.persistence.Course;
import com.lms.persistence.entities.QuestionBank;
import com.lms.persistence.entities.questions.MCQQuestion;
import com.lms.persistence.entities.questions.Question;
import com.lms.persistence.entities.questions.ShortAnswerQuestion;
import com.lms.persistence.entities.questions.TrueFalseQuestion;
import com.lms.persistence.repositories.RepositoryFacade;
import com.lms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionBankServiceTests {

    @Mock
    private RepositoryFacade repository;

    @Mock
    private CourseService courseService;

    private QuestionBankServiceImpl service;

    Course course;
    Question mcqQuestion;
    Question trueFalseQuestion;
    Question shortAnswerQuestion;
    String question1Id = "101";
    String question2Id = "102";
    String question3Id = "103";
    String courseId = "C01";

    @BeforeEach
    public void setup() {
        repository = mock(RepositoryFacade.class);
        courseService = mock(CourseService.class);
        service = new QuestionBankServiceImpl(repository, courseService);
        setupCourse();
        setupQuestions();

    }

    private void setupCourse() {
        course = new Course(courseId, "JAVA", "Programming language", 3, "P01");
    }

    private void setupQuestions() {
        String questionText = "What is the capital of France?";
        int grade = 5;
        List<String> options = List.of("Paris", "London", "Berlin", "Rome");
        String correctAnswer = "Paris";
        mcqQuestion = new MCQQuestion(question1Id, questionText, grade, options, correctAnswer);


        questionText = "Paris is the capital of France?";
        grade = 1;
        boolean correctAnswerBoolean = true;
        trueFalseQuestion = new TrueFalseQuestion(question2Id, questionText, grade, correctAnswerBoolean);

        questionText = "What is the capital of France?";
        grade = 2;
        correctAnswer = "Paris";
        shortAnswerQuestion = new ShortAnswerQuestion(question3Id, questionText, grade, correctAnswer);

    }

    @Test
    public void testAddMCQQuestionSucceeds() {
        Question question = mcqQuestion;
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(null);

        service.addQuestion(courseId, question);

        verify(repository).saveQuestionBank(any(QuestionBank.class));
    }

    @Test
    public void testAddTrueFalseQuestionSucceeds() {
        Question question = trueFalseQuestion;
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(null);

        service.addQuestion(courseId, question);

        verify(repository).saveQuestionBank(any(QuestionBank.class));
    }

    @Test
    public void testAddShortAnswerQuestionSucceeds() {
        Question question = shortAnswerQuestion;
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(null);

        service.addQuestion(courseId, question);

        verify(repository).saveQuestionBank(any(QuestionBank.class));
    }

    @Test
    public void testAddQuestion_CourseNotFound_ThrowsIllegalArgumentException() {
        Question question = mcqQuestion;
        when(courseService.findCourseById(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.addQuestion(courseId, question));
    }

    @Test
    public void testAddQuestion_InvalidQuestionData_ThrowsIllegalArgumentException() {

        String questionText = "What is the capital of France?";
        int grade = 5;
        List<String> options = List.of("Paris", "London", "Berlin", "Rome");
        String correctAnswer = "Paris";

        MCQQuestion question = new MCQQuestion("1", questionText, grade, options, correctAnswer);
        question.setCorrectAnswer("dummyNoneExistAnswer");
        when(courseService.findCourseById(courseId)).thenReturn(course);

        assertThrows(IllegalArgumentException.class, () -> service.addQuestion(courseId, question));
    }

    @Test
    public void testDeleteQuestion_Succeeds() {
        QuestionBank questionBank = new QuestionBank();
        List<Question> questions = new ArrayList<>(List.of(mcqQuestion, trueFalseQuestion, shortAnswerQuestion));
        questionBank.setQuestions(questions);
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(questionBank);

        service.deleteQuestion(courseId, question1Id);

        verify(repository).saveQuestionBank(any(QuestionBank.class));
    }

    @Test
    public void testDeleteQuestion_CourseNotFound_ThrowsIllegalArgumentException() {
        when(courseService.findCourseById(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.deleteQuestion(courseId, question1Id));
    }

    @Test
    public void testDeleteQuestion_QuestionBankNotFound_ThrowsIllegalArgumentException() {
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.deleteQuestion(courseId, question1Id));
    }

    @Test
    public void testGetQuestions_Succeeds() {
        List<Question> questions = List.of(mcqQuestion, trueFalseQuestion, shortAnswerQuestion);
        QuestionBank questionBank = new QuestionBank();
        questionBank.setQuestions(questions);
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(questionBank);

        List<Question> result = service.getQuestions(courseId);

        assertEquals(questions, result);
    }

    @Test
    public void testGetQuestions_CourseNotFound_ThrowsIllegalArgumentException() {
        when(courseService.findCourseById(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.getQuestions(courseId));
    }

    @Test
    public void testGetQuestions_QuestionBankNotFound_ReturnsEmptyList() {
        when(courseService.findCourseById(courseId)).thenReturn(course);
        when(repository.findQuestionBankByCourseId(courseId)).thenReturn(null);

        List<Question> result = service.getQuestions(courseId);

        assertEquals(Collections.emptyList(), result);
    }

}
