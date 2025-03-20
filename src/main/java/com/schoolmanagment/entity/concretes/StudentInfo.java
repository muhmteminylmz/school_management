package com.schoolmanagment.entity.concretes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.schoolmanagment.entity.enums.Note;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfo {
//Student yardimci entity, su student in su verisini cek demek yerine tabloyu aliyoruz.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer absentee;//yoklama

    private Double midtermExam;

    private Double finalExam;

    private Double examAverage;

    private String infoNote;

    @ManyToOne
    @JsonIgnoreProperties("teacher")
    private Teacher teacher;

    @ManyToOne
    private Student student;

    @Enumerated(EnumType.STRING)
    private Note letterGrade;

    @ManyToOne
    @JsonIgnoreProperties("lesson")
    private Lesson lesson;

    @OneToOne
    private EducationTerm educationTerm;


}