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
    //private final StudentRepository studentRepository;
    //private final TeacherRepository teacherRepository;
    //private final ViceDeanRepository viceDeanRepository;
    //private final DeanRepository deanRepository;
    //private final GuestUserRepository guestUserRepository;

    private final FieldControl fieldControl;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    //ResponseMessage yerine Object yazilmisti "?" yuzunden,Biz degistirdik
    public ResponseMessage save(AdminRequest request) {

        //girilen username - ssn -phoneNumber unique mi kontrolu(tum DB leri kontrol etmeliyiz)
        //ya existBy methodlarini kullanicaz yada cok fazla JOIN query ile (method daha performansli)

        fieldControl.checkDuplicate(request.getUsername(),
                                    request.getSsn(),
                                    request.getPhoneNumber());
        //checkDuplicate(request.getUsername(), request.getSsn(), request.getPhoneNumber());

        //Admin nesnesi olusturmaliyiz.Admin nesnesini builder ile olusturalim
        Admin admin = createAdminForSave(request);
        admin.setBuilt_in(false);

        if(Objects.equals(request.getUsername(),"Admin")) admin.setBuilt_in(true);
        //Su kullanici adi suysa ona built_in yap

        //admin rolu veriliyor
        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        //Neden direkt atamiyoruz? Cunku DB de olup olmadigini bilmiyoruz.O yuzden bunun kontrolunu
        //yapacak ilgili entity nin ilgili service i

        //password encode ediliyor.
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedData = adminRepository.save(admin);

        return ResponseMessage.<AdminResponse>builder().
                message("Admin saved").
                httpStatus(HttpStatus.CREATED).
                object(createResponse(savedData)). //pojo - dto bunun sebebi request te id gibi datalarin olmamasi
                build();
    }

   /* public void checkDuplicate(String username, String ssn,String phoneNumber){
        if (adminRepository.existsByUsername(username) ||
        deanRepository.existsByUsername(username) ||
        studentRepository.existsByUsername(username) ||
        teacherRepository.existsByUsername(username) ||
        viceDeanRepository.existsByUsername(username) ||
        guestUserRepository.existsByUsername(username))
        {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_USERNAME,username));
        } else if (adminRepository.existsBySsn(ssn) ||
                deanRepository.existsBySsn(ssn) ||
                studentRepository.existsBySsn(ssn) ||
                teacherRepository.existsBySsn(ssn) ||
                viceDeanRepository.existsBySsn(ssn) ||
                guestUserRepository.existsBySsn(ssn)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_SSN,ssn));
        } else if (adminRepository.existsByPhoneNumber(phoneNumber) ||
                deanRepository.existsByPhoneNumber(phoneNumber) ||
                studentRepository.existsByPhoneNumber(phoneNumber) ||
                teacherRepository.existsByPhoneNumber(phoneNumber) ||
                viceDeanRepository.existsByPhoneNumber(phoneNumber) ||
                guestUserRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER,phoneNumber));
        }
        //Bu kod calisir,ancak baska rol eklendiginde tekrar eklememiz gerekir.
        //Bunlarin hepsini bir interface e extend ederek,sadece interface ismini yazarak kurtulabiliriz.
        //FIELD CONTROL(UTILS)
    }*/
    //Ayrica baska parametre eklemek istersek(email)

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
        //orElseThrow suz nasil yapariz

        //isPresent Admin nesnesinin Optinal olarak dolu degeri varmi?(yani field lari dolu degeri varmi)
        //.get() Optinal<Admin> den Admini bize verir.Burdan built_in true mu diye kontrol ediyoruz.
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