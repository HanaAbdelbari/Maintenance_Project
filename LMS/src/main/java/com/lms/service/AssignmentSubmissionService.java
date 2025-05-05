package com.lms.service;

import com.lms.business.models.AssignmentSubmissionModel;
import com.lms.persistence.entities.AssignmentSubmissionEntity;
import java.util.List;

public interface AssignmentSubmissionService {
  boolean submitAssignment(
    AssignmentSubmissionModel model,
    int assignmentId,
    String studentId
  );

  List<AssignmentSubmissionEntity> getSubmissionsByAssignment(int assignmentId);

  List<AssignmentSubmissionEntity> getSubmissionsByCourse(String courseId);

  List<AssignmentSubmissionEntity> getSubmissionsByStudent(String studentId);

  boolean updateSubmission(
    AssignmentSubmissionEntity updatedEntity
  );

  List<AssignmentSubmissionEntity> getSubmissionsByStudentAndCourse(
    String studentId,
    String courseId
  );

 boolean existsByStudentId(String studentId);
 public boolean hasStudentSubmittedAssignment(String studentId, int assignmentId);

AssignmentSubmissionEntity getAssignmentSubmission(int submissionId);
}
