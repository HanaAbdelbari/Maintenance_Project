package com.lms.persistence.repositories;

import com.lms.persistence.entities.QuizSubmission;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
class QuizSubmissionRepository {

  private List<QuizSubmission> submissions = new ArrayList<>();

  public void save(QuizSubmission submission) {
    submissions.add(submission);
  }

  public List<QuizSubmission> findByStudentId(String studentId) {
    return submissions
      .stream()
      .filter(submission -> submission.getStudentId().equals(studentId))
      .collect(Collectors.toList());
  }

  public List<QuizSubmission> findAll() {
    return new ArrayList<>(submissions);
  }

  public List<QuizSubmission> findByQuizId(String quizId) {
    return submissions
      .stream()
      .filter(submission -> submission.getQuizId().equals(quizId))
      .collect(Collectors.toList());
  }

  public List<QuizSubmission> findByStudentAndCourse(
    String studentId,
    String courseId
  ) {
    return submissions
      .stream()
      .filter(submission ->
        submission.getStudentId().equals(studentId) &&
        submission.getCourseId().equals(courseId)
      )
      .collect(Collectors.toList());
  }

  public boolean existsByStudentIdAndQuizId(String studentId, String quizId) {
    return submissions
      .stream()
      .anyMatch(submission ->
        submission.getStudentId().equals(studentId) &&
        submission.getQuizId().equals(quizId)
      );
  }
}
