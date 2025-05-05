package com.lms.business.models;

import com.lms.persistence.Lesson;
import com.lms.persistence.User;
import com.lms.persistence.entities.AssignmentEntity;
import com.lms.persistence.entities.AssignmentSubmissionEntity;
import com.lms.persistence.entities.Quiz;
import com.lms.persistence.entities.QuizSubmission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CourseProgress {

  private String courseId;
  private Map<String, List<QuizSubmission>> quizSubmissionByStudent = new HashMap<>(); // quizId -> list of quiz submissions
  private Map<Integer, List<AssignmentSubmissionEntity>> assignmentSubmissionByStudent = new HashMap<>(); // assignmentId -> list of assignment submissions
  private Map<String, List<User>> attendedLessonsByStudent = new HashMap<>(); // lessonId -> list of users
}
