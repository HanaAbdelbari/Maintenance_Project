package com.lms.persistence.entities;
import java.sql.Date;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class AssignmentEntity {
    private int id;
    private String title;
    private String courseId;
    private String description;
    private int grade;
    private Date dueDate;
    private String status;
    private String instructorId;
}
