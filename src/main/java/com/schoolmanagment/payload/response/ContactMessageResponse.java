package com.schoolmanagment.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

public class ContactMessageResponse implements Serializable {
//Buda on tarafa gidecekigden Serializable kullaandik

//Zaten DB den gelen veri icin Validation a gerek yoktur.

    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDate date;
}