package com.lms.service;

import java.util.List;

import com.lms.persistence.entities.questions.Question;

public interface QuestionBankService {

  void addQuestion(String courseId, Question question);

  void deleteQuestion(String courseId, String questionId);

  List<Question> getQuestions(String courseId);

}