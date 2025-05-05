package com.lms.persistence.entities.questions;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MCQQuestion extends Question {

  private List<String> options;
  private String correctAnswer;

  public MCQQuestion(
    String id,
    String questionText,
    int grade,
    List<String> options,
    String answer
  ) {
    super(id, "MCQ", questionText, grade);
    this.options = options;
    this.correctAnswer = answer;
  }

  @Override
  public boolean validate() {
    return (
      options != null && !options.isEmpty() && options.contains(correctAnswer)
    );
  }

  @Override
  public String getCorrectAnswer() {
    return correctAnswer;
  }
}

