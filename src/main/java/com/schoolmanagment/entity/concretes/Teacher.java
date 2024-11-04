package com.schoolmanagment.entity.concretes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.schoolmanagment.entity.abstracts.User;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Teacher extends User {

    @JsonIgnore
    @OneToOne(mappedBy = "teacher", cascade = CascadeType.PERSIST,orphanRemoval = true)
    private AdvisorTeacher advisorTeacher;

    @Column(name = "isAdvisor")
    private Boolean isAdvisor;

    @Column(unique = true)
    private String email;

    //StudentInfo(yazacagimiz query leri kolaylastiriyoruz.) ,lessonProgram
    @OneToMany(mappedBy = "teacher",cascade = CascadeType.REMOVE)
    private List<StudentInfo> studentInfos;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "teacher_lesson_program",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonsProgramList;


}