package com.lms.persistence.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lms.persistence.entities.questions.Question;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Quiz {

  private String id;
  private String CourseId;
  private String name;
  private String instructorId;
  private int numberOfQuestions;
  private int duration;
  private String status;
  private List<Question> selectedQuestions;

  public Map<String, String> getCorrectAnswers() {
    Map<String, String> correctAnswers = new HashMap<>();
    for (Question question : selectedQuestions) {
        correctAnswers.put(question.getId(), question.getCorrectAnswer());
    }
    return correctAnswers;
}
}
