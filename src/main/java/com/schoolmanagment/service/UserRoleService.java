package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.UserRole;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.ConflictException;
import com.schoolmanagment.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getUserRole(RoleType roleType) {

        Optional<UserRole> userRole = userRoleRepository.findByERoleEquals(roleType);//Query ile yazmak istedik
        return userRole.orElse(null);

    }

    //Runner tarafi icin gerekli method
    public List<UserRole> getAllUserRole() {
        return userRoleRepository.findAll();
    }

    //Runner tarafi icin gerekli method
    public UserRole save(RoleType roleType) {

        if(userRoleRepository.existsByERoleEquals(roleType)){
            throw new ConflictException("This role is already registered");
        }

        UserRole userRole = UserRole.builder().roleType(roleType).build();
        return userRoleRepository.save(userRole);
    }
}