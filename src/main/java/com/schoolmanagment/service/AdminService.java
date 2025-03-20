package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.ConflictException;
import com.schoolmanagment.payload.request.AdminRequest;
import com.schoolmanagment.payload.response.AdminResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.*;
import com.schoolmanagment.utils.FieldControl;
import com.schoolmanagment.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final FieldControl fieldControl;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public ResponseMessage save(AdminRequest request) {

        //girilen username - ssn -phoneNumber unique mi kontrolu
        fieldControl.checkDuplicate(request.getUsername(),
                                    request.getSsn(),
                                    request.getPhoneNumber());

        Admin admin = createAdminForSave(request);
        admin.setBuilt_in(false);

        if(Objects.equals(request.getUsername(),"Admin")) admin.setBuilt_in(true);

        //admin rolu veriliyor
        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));

        //password encode ediliyor.
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedData = adminRepository.save(admin);

        return ResponseMessage.<AdminResponse>builder().
                message("Admin saved").
                httpStatus(HttpStatus.CREATED).
                object(createResponse(savedData)). //pojo - dto
                build();
    }

    //Bu yap yerine injection yapi daha temiz ama buda yapilabilir.
    protected Admin createAdminForSave(AdminRequest request){
        return Admin.builder().
                username(request.getUsername()).
                name(request.getName()).
                surname(request.getSurname()).
                password(request.getPassword()).
                ssn(request.getSsn()).
                birthDate(request.getBirthDate()).
                birthPlace(request.getBirthPlace()).
                phoneNumber(request.getPhoneNumber()).
                gender(request.getGender()).
                build();
    }

    private AdminResponse createResponse(Admin admin){
        return AdminResponse.builder().
                userId(admin.getId()).
                userName(admin.getUsername()).
                name(admin.getName()).
                surname(admin.getSurname()).
                birthDate(admin.getBirthDate()).
                birthPlace(admin.getBirthPlace()).
                ssn(admin.getSsn()).
                phoneNumber(admin.getPhoneNumber()).
                gender(admin.getGender()).
                build();
    }

    //Not: getAll() ****
    public Page<Admin> getAllAdmin(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }


    //Not: delete() ****
    public String deleteAdmin(Long id) {

        Optional<Admin> admin = adminRepository.findById(id);

        if(admin.isPresent() && admin.get().isBuilt_in()){
            throw new ConflictException(Messages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        if(admin.isPresent()){
            adminRepository.deleteById(id);

            return "Admin is deleted Successfully";
        }

        return Messages.NOT_FOUND_USER_MESSAGE;
    }

    //runner tarafi icin yazildi
    public long countAllAdmin() {
        return adminRepository.count();
    }
}