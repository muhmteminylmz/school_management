package com.schoolmanagment.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagment.entity.enums.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EducationTermRequest implements Serializable {

    @NotNull(message = "Education Term must not be empty")
    private Term term;

    @NotNull(message = "Start Date must not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End Date must not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Last registration Date must not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate lastRegistrationDate;
}