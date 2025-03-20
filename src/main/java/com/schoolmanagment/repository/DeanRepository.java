package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.Dean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeanRepository extends JpaRepository<Dean,Long> {


    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phoneNumber);

    Dean findByUsernameEquals(String username);
}