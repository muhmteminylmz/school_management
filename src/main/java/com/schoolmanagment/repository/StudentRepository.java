package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.Student;
import com.schoolmanagment.payload.response.StudentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {


    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phoneNumber);

    Student findByUsernameEquals(String username);

    boolean existsByEmail(String email);

    @Query(value = "SELECT (count(s)>0) FROM Student s")
    boolean findStudent();

    @Query("SELECT MAX(s.studentNumber) FROM Student s")
    int getMaxStudentNumber();

    List<Student> getByNameContaining(String studentName);

    Optional<Student> findByUsername(String username);

    @Query("SELECT s FROM Student s WHERE s.advisorTeacher.teacher.username =:username")
    //@Query(value = "SELECT s FROM Student s JOIN s.advisorTeacher at JOIN at.teacher t WHERE t.username =:username)
    List<Student> getStudentByAdvisorTeacher_Username(String username);

    @Query("SELECT s FROM Student s WHERE s.id IN :id")
    List<Student> findByIdsEquals(Long[] id);

    @Query("SELECT s FROM Student s WHERE s.username =:username")
    Optional<Student> findByUsernameEqualsForOptional(String username);

    @Modifying
    @Query("DELETE FROM Student s WHERE s.id =:id")
    void deleteById(@Param("id") Long id);

    @Query(value = "SELECT s FROM Student s WHERE s.id IN :id")
    Set<Student> findByIdsEquals(List<Long> id);
}