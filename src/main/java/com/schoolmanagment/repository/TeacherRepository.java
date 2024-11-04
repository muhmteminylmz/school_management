package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.Teacher;
import com.schoolmanagment.payload.response.TeacherResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long> {


    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phoneNumber);

    Teacher findByUsernameEquals(String username);

    boolean existsByEmail(String email);

    //@Query("SELECT t FROM Teacher t WHERE t.name LIKE concat('%',?!,'%')")
    List<Teacher> getTeacherByNameContaining(String teacherName);

    Teacher getTeacherByUsername(String username);

    @Query("SELECT t FROM Teacher t WHERE t.id IN :id")
    Set<Teacher> findByIdsEquals(List<Long> id);
}