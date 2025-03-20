package com.schoolmanagment.payload.dto;

import com.schoolmanagment.entity.concretes.Teacher;
import com.schoolmanagment.payload.request.TeacherRequest;
import lombok.Data;

@Data
public class TeacherRequestDto {

    public Teacher dtoTeacher(TeacherRequest teacherRequest){

        return Teacher.builder()
                .name(teacherRequest.getName())
                .surname(teacherRequest.getSurname())
                .ssn(teacherRequest.getSsn())
                .username(teacherRequest.getUsername())
                .birthDate(teacherRequest.getBirthDate())
                .birthPlace(teacherRequest.getBirthPlace())
                .password(teacherRequest.getPassword())
                .phoneNumber(teacherRequest.getPhoneNumber())
                .email(teacherRequest.getEmail())
                .isAdvisor(teacherRequest.isAdvisorTeacher())
                .build();
    }

}