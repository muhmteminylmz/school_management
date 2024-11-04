package com.schoolmanagment.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)//Json icindeki null olanlar gozukmememesini sagliyoruz.
//@MappedSuperclass DTO tarzi class larda DB ile alakasi olmadigindan buna gerek yok
public class ResponseMessage<E> {
//Kendi ResponseEntity mizi olusturuyoruz(Custom)

    private E object;
    private String message;

    private HttpStatus httpStatus;

}
