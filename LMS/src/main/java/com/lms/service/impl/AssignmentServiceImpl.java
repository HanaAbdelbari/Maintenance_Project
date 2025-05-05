package com.lms.service.impl;

import com.lms.business.models.AssignmentModel;
import com.lms.persistence.entities.AssignmentEntity;
import com.lms.persistence.repositories.RepositoryFacade;
import com.lms.service.AssignmentService;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
class AssignmentServiceImpl implements AssignmentService {

  private final RepositoryFacade repository;

  public AssignmentServiceImpl(RepositoryFacade repository) {
    this.repository = repository;
  }

  @Override
  public AssignmentEntity createAssignment(AssignmentModel model, String courseId, String instructorId) {
    AssignmentEntity entity = new AssignmentEntity(
      0,
      model.getTitle(),
      courseId,
      model.getDescription(),
      model.getGrade(),
      model.getDueDate(),
      model.getStatus(),
            instructorId
    );
    return repository.addAssignment(entity);
  }

  @Override
  public boolean deleteAssignment(int id, String courseId) {
    AssignmentEntity entity = repository.findAssignmentById(id);
    if (entity != null && entity.getCourseId().equals(courseId)) {
      entity.setStatus("Deleted");
      return true;
    }
    return false;
  }

  @Override
  public boolean editAssignment(
    int id,
    String courseId,
    AssignmentModel model
  ) {
    AssignmentEntity entity = repository.findAssignmentById(id);

    if (entity != null && entity.getCourseId().equals(courseId)) {
      if (model.getTitle() != null) {
        entity.setTitle(model.getTitle());
      }
      if (model.getDescription() != null) {
        entity.setDescription(model.getDescription());
      }
      if (model.getGrade() != 0) {
        entity.setGrade(model.getGrade());
      }
      if (model.getDueDate() != null) {
        entity.setDueDate(model.getDueDate());
      }
      if (model.getStatus() != null) {
        entity.setStatus(model.getStatus());
      }

      return true;
    }
    return false;
  }

  @Override
  public List<AssignmentEntity> getAssignmentsByCourse(String courseId) {
    return repository
      .findAllAssignments()
      .stream()
      .filter(a -> a.getCourseId().equals(courseId))
      .collect(Collectors.toList());
  }

  @Override
  public AssignmentEntity findAssignmentById(int assignmentId) {
    return repository.findAssignmentById(assignmentId);
  }
}
