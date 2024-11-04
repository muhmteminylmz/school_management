package com.schoolmanagment.payload.dto;

import com.schoolmanagment.entity.concretes.Dean;
import com.schoolmanagment.payload.request.DeanRequest;
import lombok.Data;

@Data
//@Component Bunu her dto icin eklememiz gerekecek
//Bunun yerine CreateObjectBean config i ile tum beanleri 1 class da topluyacagiz
//Boylece hepsini duzenli sekilde gorebilecegiz
public class DeanDto {

    //DTO - POJO
    public Dean dtoDean(DeanRequest deanRequest){
        return Dean.builder().
                username(deanRequest.getUsername()).
                name(deanRequest.getName()).
                surname(deanRequest.getSurname()).
                password(deanRequest.getPassword()).
                ssn(deanRequest.getSsn()).
                birthDate(deanRequest.getBirthDate()).
                birthPlace(deanRequest.getBirthPlace()).
                phoneNumber(deanRequest.getPhoneNumber()).
                gender(deanRequest.getGender()).
                build();
    }
}