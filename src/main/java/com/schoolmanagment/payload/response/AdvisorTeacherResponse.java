package com.schoolmanagment.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdvisorTeacherResponse implements Serializable {

    private Long advisorTeacherId;
    private String teacherName;
    private String teacherSSN;
    private String teacherSurname;

}