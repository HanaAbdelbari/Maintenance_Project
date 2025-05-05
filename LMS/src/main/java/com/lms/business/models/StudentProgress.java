package com.lms.business.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lms.persistence.Course;
import com.lms.persistence.Lesson;
import com.lms.persistence.entities.AssignmentSubmissionEntity;
import com.lms.persistence.entities.QuizSubmission;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class StudentProgress {

  private String studentId;
  private Map<String, List<QuizSubmission>> quizSubmissionByCourse = new HashMap<>();
  private Map<String, List<AssignmentSubmissionEntity>> assignmentSubmissionByCourse = new HashMap<>();
  private Map<String, List<Lesson>> attendedLessonsByCourse = new HashMap<>();
}
