package com.schoolmanagment;

import com.schoolmanagment.entity.enums.Gender;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.payload.request.AdminRequest;
import com.schoolmanagment.service.AdminService;
import com.schoolmanagment.service.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class SchoolManagmentApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final AdminService adminService;

	public SchoolManagmentApplication(UserRoleService userRoleService, AdminService adminService) {
		this.userRoleService = userRoleService;
		this.adminService = adminService;
	}

	public static void main(String[] args) {
		SpringApplication.run(SchoolManagmentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if (userRoleService.getAllUserRole().size() == 0){
			userRoleService.save(RoleType.ADMIN);
			userRoleService.save(RoleType.MANAGER);
			userRoleService.save(RoleType.ASSISTANTMANAGER);
			userRoleService.save(RoleType.TEACHER);
			userRoleService.save(RoleType.STUDENT);
			userRoleService.save(RoleType.ADVISORTEACHER);
			userRoleService.save(RoleType.GUESTUSER);
		}


		//Admin olusturulacak built_in
		if (adminService.countAllAdmin() == 0){
			AdminRequest admin = new AdminRequest();
			admin.setUsername("Admin");
			admin.setSsn("987-99-9999");
			admin.setPassword("12345678");
			admin.setName("Admin");
			admin.setSurname("Admin");
			admin.setPhoneNumber("555-444-4331");
			admin.setGender(Gender.FEMALE);
			admin.setBirthDate(LocalDate.of(2002,5,2));
			admin.setBirthPlace("US");
			adminService.save(admin);
		}
	}
}
