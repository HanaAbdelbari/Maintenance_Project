package com.lms.business.models;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class AssignmentSubmissionModel {
    private String studentId;
    private String fileUrl;
    private Integer score;
    private String feedback;
    private String status;
}
