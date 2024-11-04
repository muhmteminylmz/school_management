package com.schoolmanagment.payload.response.abstracts;

import com.schoolmanagment.entity.enums.Gender;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BaseUserResponse {

    private Long userId;
    //Burda id gonderdik bunu clint gormiyecek FrontEnd kullanmak isterse diye
    private String userName;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String ssn;
    private String birthPlace;
    private String phoneNumber;
    private Gender gender;
}
