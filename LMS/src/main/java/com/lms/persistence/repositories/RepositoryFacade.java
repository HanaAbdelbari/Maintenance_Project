package com.lms.persistence.repositories;

import com.lms.persistence.Course;
import com.lms.persistence.Enrollment;
import com.lms.persistence.UserRepository;
import com.lms.persistence.entities.AssignmentEntity;
import com.lms.persistence.entities.AssignmentSubmissionEntity;
import com.lms.persistence.entities.QuestionBank;
import com.lms.persistence.entities.Quiz;
import com.lms.persistence.entities.QuizSubmission;
import com.lms.service.CourseService;
import com.lms.service.EnrollmentService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
@AllArgsConstructor
public class RepositoryFacade {

  private final AssignmentRepository assignmentRepository;
  private final AssignmentSubmissionRepository assignmentSubmissionRepository;
  private final QuestionBankRepository questionBankRepository;
  private final QuizRepository quizRepository;
  private final QuizSubmissionRepository quizSubmissionRepository;
  private final UserRepository userRepository;
  private final EnrollmentService enrollmentService;
  private final CourseService courseService;

  // Assignment operations

  public AssignmentEntity addAssignment(AssignmentEntity entity) {
    return assignmentRepository.add(entity);
  }

  public List<AssignmentEntity> findAllAssignments() {
    return assignmentRepository.findAll();
  }

  public AssignmentEntity findAssignmentById(int id) {
    return assignmentRepository.findById(id);
  }

  // Assignment Submission operations

  public boolean addAssignmentSubmission(
    AssignmentSubmissionEntity submission
  ) {
    return assignmentSubmissionRepository.add(submission);
  }

  public List<AssignmentSubmissionEntity> findAllAssignmentSubmissions() {
    return assignmentSubmissionRepository.findAll();
  }

  public AssignmentSubmissionEntity findAssignmentSubmissionById(int id) {
    return assignmentSubmissionRepository.findById(id);
  }

  public List<AssignmentSubmissionEntity> findAssignmentSubmissionsByStudentAndCourse(
    String studentId,
    String courseId
  ) {
    return assignmentSubmissionRepository.findByStudentAndCourse(
      studentId,
      courseId
    );
  }

  public List<AssignmentSubmissionEntity> findByAssignmentSubmissionsByAssignmentId(
    int assignmentId
  ) {
    return assignmentSubmissionRepository.findByAssignmentSubmissionsByAssignmentId(
      assignmentId
    );
  }

  public boolean hasStudentSubmittedAssignment(
    String studentId,
    int assignmentId
  ) {
    return assignmentSubmissionRepository.hasStudentSubmittedAssignment(
      studentId,
      assignmentId
    );
  }

  public boolean existsByStudentId(String studentId) {
    return assignmentSubmissionRepository.existsByStudentId(studentId);
  }

  // Question Bank operations

  public void saveQuestionBank(QuestionBank questionBank) {
    questionBankRepository.save(questionBank);
  }

  public QuestionBank findQuestionBankByCourseId(String courseId) {
    return questionBankRepository.findByCourseId(courseId);
  }

  // Quiz operations

  public void saveQuiz(Quiz quiz) {
    quizRepository.save(quiz);
  }

  public Optional<Quiz> findQuizById(String quizId) {
    return quizRepository.findById(quizId);
  }

  public List<Quiz> findAllQuizzes() {
    return quizRepository.findAll();
  }

  // Quiz Submission operations

  public void saveQuizSubmission(QuizSubmission submission) {
    quizSubmissionRepository.save(submission);
  }

  public List<QuizSubmission> findAllQuizSubmissions() {
    return quizSubmissionRepository.findAll();
  }

  public List<QuizSubmission> findQuizSubmissionsByStudentId(String studentId) {
    return quizSubmissionRepository.findByStudentId(studentId);
  }

  public List<QuizSubmission> findQuizSubmissionsByQuizId(String quizId) {
    return quizSubmissionRepository.findByQuizId(quizId);
  }

  public List<QuizSubmission> findQuizSubmissionsByStudentAndCourse(
    String studentId,
    String courseId
  ) {
    return quizSubmissionRepository.findByStudentAndCourse(studentId, courseId);
  }

  public boolean isAssignmentExist(int assignmentId) {
    return assignmentRepository.isExist(assignmentId);
  }

  public boolean updateAssignmentSubmission(AssignmentSubmissionEntity entity) {
    return assignmentSubmissionRepository.update(entity);
  }

  public void updateQuiz(Quiz quiz) {
    quizRepository.update(quiz);
  }

  public boolean studentExistsById(String studentId) {
    return userRepository.existsById(studentId);
  }

  public List<String> getAllStudentIds() {
    return userRepository.getAllStudentIds();
  }

  public boolean findQuizSubmissionByStudentIdAndQuizId(
    String studentId,
    String quizId
  ) {
    return quizSubmissionRepository.existsByStudentIdAndQuizId(
      studentId,
      quizId
    );
  }

  public List<String> getAllRegestedStudentIds(String courseId) {
    return enrollmentService
      .getEnrollmentsByCourse(courseId)
      .stream()
      .map(Enrollment::getsId)
      .collect(Collectors.toList());
  }

  public List<String> getAllRegestedCoursesIds(String studentId) {
    return enrollmentService
      .getEnrollmentsByCourse(studentId)
      .stream()
      .map(Enrollment::getcId)
      .collect(Collectors.toList());
  }

  public List<Course> getAllRegisteredCourses(String studentId) {
    List<String> courseIds = enrollmentService
      .getEnrollmentsByStudent(studentId)
      .stream()
      .map(Enrollment::getcId)
      .collect(Collectors.toList());

    return courseIds
      .stream()
      .map(courseId -> courseService.findCourseById(courseId))
      .collect(Collectors.toList());
  }
}
