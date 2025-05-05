package com.lms.persistence.entities;

import java.util.ArrayList;
import java.util.List;

import com.lms.persistence.entities.questions.Question;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QuestionBank {

  private String courseId;
  private List<Question> questions = new ArrayList<>();

  public QuestionBank(String courseId) {
    this.courseId = courseId;
  }

  public void addQuestion(Question question) {
    questions.add(question);
  }

  public void deleteQuestion(String questionId) {
    questions.removeIf(q -> q.getId().equals(questionId));
  }
}
