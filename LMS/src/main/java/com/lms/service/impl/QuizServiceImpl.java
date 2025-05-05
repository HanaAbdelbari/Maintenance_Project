package com.lms.service.impl;

import com.lms.persistence.entities.QuestionBank;
import com.lms.persistence.entities.Quiz;
import com.lms.persistence.entities.questions.Question;
import com.lms.persistence.repositories.RepositoryFacade;
import com.lms.service.CourseService;
import com.lms.service.QuizService;

import lombok.AllArgsConstructor;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class QuizServiceImpl implements QuizService {

  private final RepositoryFacade repository;
  private final CourseService courseService;


  @Override
  public Quiz createQuiz(
    String courseId,
    String name,
    String instructorId,
    int questionsNumber,
    int duration,
    String status
  ) {
    if (courseService.findCourseById(courseId) == null) {
      throw new IllegalArgumentException(
        "Course with id " + courseId + " does not exist"
      );
    }
    QuestionBank questionBank = repository.findQuestionBankByCourseId(courseId);

    if (questionBank == null) {
      throw new IllegalArgumentException(
        "Question bank not found for the course."
      );
    }

    List<Question> availableQuestions = questionBank.getQuestions();
    if (availableQuestions.size() < questionsNumber) {
      throw new IllegalArgumentException(
        "Not enough questions in the question bank to create the quiz."
      );
    }

    Collections.shuffle(availableQuestions);
    List<Question> selectedQuestions = availableQuestions.subList(
      0,
      questionsNumber
    );

    Quiz quiz = new Quiz(
      UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6),
      courseId,
      name,
            instructorId,
      questionsNumber,
      duration,
      status,
      selectedQuestions
    );

    repository.saveQuiz(quiz);
    return quiz;
  }

  @Override
  public void markQuizAsDeleted(String quizId) {
    repository
      .findQuizById(quizId)
      .ifPresent(quiz -> {
        quiz.setStatus("deleted");
        repository.updateQuiz(quiz);
      });
  }

  @Override
  public void markQuizAsOpened(String quizId) {
    repository
      .findQuizById(quizId)
      .ifPresent(quiz -> {
        quiz.setStatus("opened");
        repository.updateQuiz(quiz);
      });
  }

  @Override
  public List<Quiz> getAllQuizzes() {
    return repository.findAllQuizzes();
  }

  @Override
  public Quiz getQuizById(String quizId) {
    return repository.findQuizById(quizId).orElse(null);
  }
}
