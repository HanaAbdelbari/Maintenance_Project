package com.lms.business.models;
import java.sql.Date;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssignmentModel {
    private String title;
    private String description;
    private int grade;
    private Date dueDate;
    private String status;
}
