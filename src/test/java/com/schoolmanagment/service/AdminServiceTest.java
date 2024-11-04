package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.entity.concretes.UserRole;
import com.schoolmanagment.entity.enums.Gender;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.payload.request.AdminRequest;
import com.schoolmanagment.payload.response.AdminResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.*;
import com.schoolmanagment.utils.FieldControl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//Mockito yu kullanacagimizi belirtiyoruz
//Mocklama icin kullaniliyor
class AdminServiceTest {

    @Mock//mockluyoruz
    private AdminRepository adminRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private DeanRepository deanRepository;
    @Mock
    private ViceDeanRepository viceDeanRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private GuestUserRepository guestUserRepository;
    @Mock
    private FieldControl fieldControl;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    //Yukarda mocklanan nesneleri adminService ornegine otomatik olarak injekte edilmesi saglanir
    private AdminService adminService;

    @Test
    void testSave_AdminSavedSuccessfully() {

        AdminRequest request = createAdminRequest();
        Admin admin = createAdmin();
        Admin savedAdmin = createAdmin();
        savedAdmin.setId(1L);

        UserRole adminRole = new UserRole(1, RoleType.ADMIN);
        doNothing().when(fieldControl).checkDuplicate(anyString(),anyString(),anyString());
        //return u void yani hicbisey yapma diyoruz

        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        //Biz AdminService in save methodunu test ediyoruz.adminRepository yi degil
        when(userRoleService.getUserRole(RoleType.ADMIN)).thenReturn(adminRole);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        //davranis seklini belirliyoruz
        ResponseMessage<AdminResponse> response = adminService.save(request);

        assertNotNull(response);//response objesi null degil degil mi?
        assertEquals("Admin saved",response.getMessage());
        assertEquals(HttpStatus.CREATED,response.getHttpStatus());
        assertNotNull(response.getObject());
        assertEquals(savedAdmin.getId(),response.getObject().getUserId());
        assertEquals(savedAdmin.getName(),response.getObject().getName());

        //Bu ozellik kac defa calisti kontrolu
        Mockito.verify(fieldControl, Mockito.times(1)).checkDuplicate(anyString(),anyString(),anyString());
        Mockito.verify(adminRepository,Mockito.times(1)).save(any(Admin.class));
        Mockito.verify(userRoleService,Mockito.times(1)).getUserRole(RoleType.ADMIN);
        Mockito.verify(passwordEncoder,Mockito.times(1)).encode(anyString());

    }

    private AdminRequest createAdminRequest(){
        AdminRequest request = new AdminRequest();
        request.setUsername("admin1");
        request.setName("John");
        request.setSurname("soyad");
        request.setPassword("12345678");
        request.setSsn("1231412121");
        request.setBirthDate(LocalDate.of(2000,1,1));
        request.setBirthPlace ("City");
        request.setPhoneNumber ("523725625672");
        request.setGender (Gender.MALE);
        return request;
    }

    private Admin createAdmin(){
        Admin admin = new Admin();
        admin.setUsername("admin1");
        admin.setName("John");
        admin.setSurname("soyad");
        admin.setPassword("12345678");
        admin.setSsn("1231412121");
        admin.setBirthDate(LocalDate.of(2000,1,1));
        admin.setBirthPlace("City");
        admin.setPhoneNumber("523725625672");
        admin.setGender (Gender.MALE);
        return admin;
    }

    @Test
    void getAllAdmin() {

        Pageable pageable = Pageable.unpaged();
        Page<Admin> expectedPage = mock(Page.class);
        when(adminRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Admin> result = adminService.getAllAdmin(pageable);

        assertSame(expectedPage,result);
        verify(adminRepository,times(1)).findAll(pageable);

    }

    //Bazi methodlarin exception firlattigi durumlarda kontrol edilebilir
    @Test
    void deleteAdmin_Successful() {
        Long id = 1L;
        Admin admin = new Admin();
        admin.setId(id);
        admin.setBuilt_in(false);

        //mocklada yapilabilir
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        String result = adminService.deleteAdmin(id);
        assertEquals("Admin is deleted Successfully",result);
        verify(adminRepository,times(1)).deleteById(id);
    }
}