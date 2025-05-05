package com.lms.persistence.entities;

import java.util.List;

import com.lms.business.models.StudentAnswer;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class QuizSubmission {

  private String id;
  private String studentId;
  private String quizId;
  private String courseId;
  private Double score;
  private Double grade;
  private List<StudentAnswer> studentAnswers;

  public void setStudentAnswers(List<StudentAnswer> studentAnswers) {
    this.studentAnswers = studentAnswers;
    calcScore();
  }

  private void calcScore() {
    score = 0D;
    grade = 0D;
    for (StudentAnswer answer : studentAnswers) {
      if (answer.getStatus() == "correct") {
        score += answer.getQuestionGrade();
      }
      grade += answer.getQuestionGrade();
    }
  }
}
