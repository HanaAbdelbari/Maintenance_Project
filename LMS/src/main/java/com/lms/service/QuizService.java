package com.lms.service;

import java.util.List;

import com.lms.persistence.entities.Quiz;

public interface QuizService {

  Quiz createQuiz(
      String courseId,
      String name,
      String instructorId,
      int questionsNumber,
      int duration,
      String status);

  void markQuizAsDeleted(String quizId);

  void markQuizAsOpened(String quizId);

  List<Quiz> getAllQuizzes();

  Quiz getQuizById(String quizId);

}