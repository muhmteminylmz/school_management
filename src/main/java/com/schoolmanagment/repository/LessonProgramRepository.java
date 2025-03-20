package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.LessonProgram;
import com.schoolmanagment.payload.response.LessonProgramResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {


    List<LessonProgram> findByTeachers_IdNull();

    List<LessonProgram> findByTeachers_IdNotNull();

    @Query("SELECT l FROM LessonProgram l INNER JOIN l.teachers teachers where teachers.username = ?1")
    Set<LessonProgram> getLessonProgramByTeacherUsername(String username);

    @Query("SELECT l FROM LessonProgram l INNER JOIN l.students students WHERE students.username = ?1")
    Set<LessonProgram> getLessonProgramByStudentUsername(String username);

    @Query("SELECT l FROM LessonProgram l WHERE l.id IN :lessonIdList")
    Set<LessonProgram> getLessonProgramByLessonProgramIdList(Set<Long> lessonIdList);
}