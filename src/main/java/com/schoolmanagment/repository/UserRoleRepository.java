package com.schoolmanagment.repository;

import com.schoolmanagment.entity.concretes.UserRole;
import com.schoolmanagment.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {


    @Query("SELECT r FROM UserRole r WHERE r.roleType=?1")
    Optional<UserRole> findByERoleEquals(RoleType roleType);

    @Query("SELECT (count(r)>0) FROM UserRole r where r.roleType = ?1")
    boolean existsByERoleEquals(RoleType roleType);
}