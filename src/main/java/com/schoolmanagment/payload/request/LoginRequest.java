package com.schoolmanagment.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
//Sisteme giris yapacagimiz DTO

    //Bunun onemi biz bunu kitledik,sadece 2 parametreli endpoint alir.
    //gelirse 500 lu hatalar aliriz.(yoksa kotu niyetli biri uygulamayi cokertebilir)

    @NotNull(message = "Username must not be empty")
    private String username;

    @NotNull(message = "Password must not be empty")
    private String password;
}