package com.lms.service.impl;

import com.lms.persistence.entities.QuestionBank;
import com.lms.persistence.entities.questions.Question;
import com.lms.persistence.repositories.RepositoryFacade;
import com.lms.service.CourseService;
import com.lms.service.QuestionBankService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class QuestionBankServiceImpl implements QuestionBankService {

  private final RepositoryFacade repository;
  private final CourseService courseService;

  @Override
  public void addQuestion(String courseId, Question question) {
    if (courseService.findCourseById(courseId) == null) {
      throw new IllegalArgumentException(
        "Course with id " + courseId + " does not exist"
      );
    }

    if (!question.validate()) {
      throw new IllegalArgumentException("Invalid question data.");
    }

    QuestionBank questionBank = repository.findQuestionBankByCourseId(courseId);
    if (questionBank == null) {
      questionBank = new QuestionBank();
      questionBank.setCourseId(courseId);
      questionBank.setQuestions(new ArrayList<>());
      questionBank.addQuestion(question);
      repository.saveQuestionBank(questionBank);
      return;
    }

    questionBank.addQuestion(question);
    repository.saveQuestionBank(questionBank);
  }

  @Override
  public void deleteQuestion(String courseId, String questionId) {

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
    questionBank.deleteQuestion(questionId);
    repository.saveQuestionBank(questionBank);
  }

  @Override
  public List<Question> getQuestions(String courseId) {
    if (courseService.findCourseById(courseId) == null) {
      throw new IllegalArgumentException(
        "Course with id " + courseId + " does not exist"
      );
    }
    QuestionBank questionBank = repository.findQuestionBankByCourseId(courseId);

    if (questionBank == null) {
      questionBank = new QuestionBank();
      questionBank.setCourseId(courseId);
      questionBank.setQuestions(new ArrayList<>());
      repository.saveQuestionBank(questionBank);
    }

    return questionBank.getQuestions();
  }
}
