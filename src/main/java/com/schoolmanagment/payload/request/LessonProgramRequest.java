package com.schoolmanagment.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagment.entity.concretes.EducationTerm;
import com.schoolmanagment.entity.concretes.Lesson;
import com.schoolmanagment.entity.enums.Day;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LessonProgramRequest {

    @NotNull(message = "Please enter a day")
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "HH:mm",timezone = "US")
    @NotNull(message = "Please enter a start time")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "HH:mm",timezone = "US")
    @NotNull(message = "Please enter a stop time")
    private LocalTime stopTime;

    @NotNull(message = "Please select lesson")
    @Size(min = 1,message = "Lesson must not be empty")
    private Set<Long> lessonIdList;

    @NotNull(message = "Please enter a education term")
    private Long educationTermId;

}