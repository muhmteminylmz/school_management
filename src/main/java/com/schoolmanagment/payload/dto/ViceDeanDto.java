package com.schoolmanagment.payload.dto;

import com.schoolmanagment.entity.concretes.ViceDean;
import com.schoolmanagment.payload.request.ViceDeanRequest;
import lombok.Data;

@Data
public class ViceDeanDto {

    public ViceDean dtoViceDean(ViceDeanRequest viceDeanRequest){
        return ViceDean.builder()
                .username(viceDeanRequest.getUsername())
                .ssn(viceDeanRequest.getSsn())
                .name(viceDeanRequest.getName())
                .birthDate(viceDeanRequest.getBirthDate())
                .birthPlace(viceDeanRequest.getBirthPlace())
                .gender(viceDeanRequest.getGender())
                .surname(viceDeanRequest.getSurname())
                .phoneNumber(viceDeanRequest.getPhoneNumber())
                .password(viceDeanRequest.getPassword())
                .build();
    }
}