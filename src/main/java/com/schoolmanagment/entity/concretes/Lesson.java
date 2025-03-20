package com.schoolmanagment.entity.concretes;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    private String lessonName;

    private Integer creditScore;

    private Boolean isCompulsory;//zorunlu mu

    @ManyToMany(mappedBy = "lesson")
    private Set<LessonProgram> lessonPrograms;


}