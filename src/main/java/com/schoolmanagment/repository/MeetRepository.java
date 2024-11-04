package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.Meet;
import com.schoolmanagment.payload.response.MeetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetRepository extends JpaRepository<Meet, Long> {


    List<Meet> findByStudentList_IdEquals(Long studentId);

    Page<Meet> findByAdvisorTeacher_IdEquals(Long id, Pageable pageable);

    List<Meet> getByAdvisorTeacher_IdEquals(Long id);
}