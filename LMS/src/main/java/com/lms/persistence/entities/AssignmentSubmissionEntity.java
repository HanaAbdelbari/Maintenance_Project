package com.lms.persistence.entities;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssignmentSubmissionEntity {
    private int id;
    private String studentId;
    private int assignmentId;
    private String courseId;
    private int score;
    private String fileUrl;
    private String feedback;
    private String status;
}
