package com.schoolmanagment.utils;

import com.schoolmanagment.entity.concretes.Student;
import com.schoolmanagment.payload.response.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateResponseObjectForService {

    public StudentResponse createStudentResponse(Student student){
        return StudentResponse.builder()
                .userId(student.getId())
                .userName(student.getUsername())
                .name(student.getName())
                .surname(student.getSurname())
                .birthDate(student.getBirthDate())
                .birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber())
                .gender(student.getGender())
                .email(student.getEmail())
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .studentNumber(student.getStudentNumber())
                .isActive(student.isActive())
                .build();
    }
}
