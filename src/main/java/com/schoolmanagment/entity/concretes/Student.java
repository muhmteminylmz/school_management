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
@SuperBuilder//Farkli parametrelerle cons uretir. Tureyen ve tureten class da kullanilir.Single da Build kullanilir.
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
//callsuper : parent i varsa karsilastirma yapiliyorsa parent taki field larida karsilastirmaya ekle
//onlyExplicitlyIncluded : ilgili field lara annotation ekleyerek karsilastirmada istedigimiz field i gosterebilriz.
//HASH tum field lari hash methoduna sokar ve unique bir data elde edip(ayni class ayni deger) islemi hizlandirir.
@ToString(callSuper = true)
public class Student extends User {

    private String motherName;

    private String fatherName;

    private int studentNumber;

    private boolean isActive;

    @Column(unique = true)
    private String email;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private AdvisorTeacher advisorTeacher;

    @OneToMany(mappedBy = "student",cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<StudentInfo> studentInfos;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "student_lessonprogram",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonsProgramList;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "meet_id")
    )
    private List<Meet> meetList;

}