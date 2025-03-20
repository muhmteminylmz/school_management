package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.GuestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestUserRepository extends JpaRepository<GuestUser, Long> {

    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phoneNumber);
}
