package com.lms.service.impl;

import com.lms.business.models.AssignmentModel;
import com.lms.persistence.entities.AssignmentEntity;
import com.lms.persistence.repositories.RepositoryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AssignmentServiceImplTests {

    @Mock
    private RepositoryFacade repository;

    private AssignmentServiceImpl service;

    private final String courseId = "C01";
    private final String instructorId = "I01";
    private AssignmentModel model;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new AssignmentServiceImpl(repository);
        model = new AssignmentModel("Assignment 1", "Description", 100, java.sql.Date.valueOf("2024-12-31"), "Active");
    }

    @Test
    public void testCreateAssignment_Succeeds() {
        AssignmentEntity entity = new AssignmentEntity(0, model.getTitle(), courseId, model.getDescription(),
                model.getGrade(), model.getDueDate(), model.getStatus(), instructorId);
        when(repository.addAssignment(any(AssignmentEntity.class))).thenReturn(entity);

        AssignmentEntity createdAssignment = service.createAssignment(model, courseId, instructorId);

        assertNotNull(createdAssignment);
        assertEquals(model.getTitle(), createdAssignment.getTitle());
        assertEquals(courseId, createdAssignment.getCourseId());
        verify(repository).addAssignment(any(AssignmentEntity.class));
    }

    @Test
    public void testDeleteAssignment_Succeeds() {
        AssignmentEntity entity = new AssignmentEntity(1, "Assignment 1", courseId, "Description", 100, java.sql.Date.valueOf("2024-12-31"), "Active", instructorId);
        when(repository.findAssignmentById(1)).thenReturn(entity);

        boolean result = service.deleteAssignment(1, courseId);

        assertTrue(result);
        assertEquals("Deleted", entity.getStatus());
    }

    @Test
    public void testDeleteAssignment_Fails() {
        when(repository.findAssignmentById(1)).thenReturn(null);

        boolean result = service.deleteAssignment(1, courseId);

        assertFalse(result);
    }

    @Test
    public void testEditAssignment_Succeeds() {
        AssignmentEntity entity = new AssignmentEntity(1, "Assignment 1", courseId, "Description", 100, java.sql.Date.valueOf("2024-12-31"), "Active", instructorId);
        when(repository.findAssignmentById(1)).thenReturn(entity);

        AssignmentModel newModel = new AssignmentModel("Updated Assignment", "Updated Description", 90, java.sql.Date.valueOf("2024-12-30"), "Active");
        boolean result = service.editAssignment(1, courseId, newModel);

        assertTrue(result);
        assertEquals("Updated Assignment", entity.getTitle());
        assertEquals("Updated Description", entity.getDescription());
    }

    @Test
    public void testEditAssignment_Fails() {
        when(repository.findAssignmentById(1)).thenReturn(null);

        AssignmentModel newModel = new AssignmentModel("Updated Assignment", "Updated Description", 90, java.sql.Date.valueOf("2024-12-30"), "Active");
        boolean result = service.editAssignment(1, courseId, newModel);

        assertFalse(result);
    }
}
