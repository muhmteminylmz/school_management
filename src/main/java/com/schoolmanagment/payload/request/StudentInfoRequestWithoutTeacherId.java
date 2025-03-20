package com.schoolmanagment.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfoRequestWithoutTeacherId {

    @NotNull(message = "please select education term")
    private Long educationTermId;

    @NotNull(message = "Please enter midterm exam")
    @DecimalMax("100.0")
    @DecimalMin("0.0")
    private Double midtermExam;

    @NotNull(message = "Please enter final exam")
    @DecimalMax("100.0")
    @DecimalMin("0.0")
    private Double finalExam;

    @NotNull(message = "Please enter absentee")
    private Integer absentee;

    @NotNull(message = "Please enter info")
    @Size(min = 10, max =200,message = "Info should be at least 10 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message="Info must consist of the characters .")
    private String infoNote;

    @NotNull(message = "Please select lesson")
    private Long lessonId;

    @NotNull(message = "Please select student")
    private Long studentId;


}