package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.ViceDean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViceDeanRepository extends JpaRepository<ViceDean,Long> {


    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phoneNumber);

    ViceDean findByUsernameEquals(String username);
}