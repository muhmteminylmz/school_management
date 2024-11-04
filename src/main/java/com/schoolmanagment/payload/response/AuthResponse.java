package com.schoolmanagment.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//Null olanlar gozukmesin
public class AuthResponse {

    //Hangilerini setlersek onlar gider.
    private String username;
    private String ssn;
    private String role;
    private String token;
    private String name;
    private String isAdvisor;

}
