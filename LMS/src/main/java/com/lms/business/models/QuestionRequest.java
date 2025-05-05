package com.lms.business.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionRequest {

  private String type;
  private String questionText;
  private int grade;
  private List<String> options;
  private String correctAnswer;
  private Boolean correctAnswerBoolean;
}
