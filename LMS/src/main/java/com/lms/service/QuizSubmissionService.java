package com.lms.service;

import java.util.List;
import java.util.Map;

import com.lms.persistence.entities.QuizSubmission;

public interface QuizSubmissionService {

  QuizSubmission submitQuiz(
      String quizId,
      String studentId,
      Map<String, String> studentAnswers);

  List<QuizSubmission> getSubmissionsByStudent(String studentId);

  List<QuizSubmission> getAllSubmissions();

  List<QuizSubmission> getSubmissionsByQuiz(String quizId);

  List<QuizSubmission> getSubmissionsByStudentAndCourse(
      String studentId,
      String courseId);

}