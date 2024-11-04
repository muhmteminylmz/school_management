package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.ContactMessage;
import com.schoolmanagment.payload.response.ContactMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository//kod okunurlugu
public interface ContactMessageRepository extends JpaRepository<ContactMessage,Long> {


    boolean existsByEmailEqualsAndDateEquals(String email, LocalDate now);
    //Bunun turkcesi Date ile Email i esit olan bir veri varmi(keyword ler schoolmanagement classinda var)
    //Hicbir sey yazmamiza gerek kalmadan exist turetilebilen method sayesinde.

    Page<ContactMessage> findByEmailEquals(String email, Pageable pageable);

    Page<ContactMessage> findBySubjectEquals(String subject, Pageable pageable);
    //findAll gibi calisiyor.findAll kullanmadik cunku pageable yapida calismiyor.Bizde keyword le hallettik


}