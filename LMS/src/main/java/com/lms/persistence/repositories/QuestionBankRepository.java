package com.lms.persistence.repositories;

import com.lms.persistence.entities.QuestionBank;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
class QuestionBankRepository {

  private final Map<String, QuestionBank> questionBanks = new HashMap<>();

  public void save(QuestionBank questionBank) {
    questionBanks.put(questionBank.getCourseId(), questionBank);
  }

  public QuestionBank findByCourseId(String courseId) {
    return questionBanks
      .values()
      .stream()
      .filter(qb -> qb.getCourseId().equals(courseId))
      .findFirst()
      .orElse(null);
  }
}
