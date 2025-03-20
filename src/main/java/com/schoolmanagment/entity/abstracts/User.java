package com.schoolmanagment.entity.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagment.entity.concretes.UserRole;
import com.schoolmanagment.entity.enums.Gender;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class User implements Serializable {

    @Id//Burdan tablo olusmayacak ancak SuperBuilder la child lar kullanacak
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String ssn;

    private String name;

    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String birthPlace;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //Client dan DB ye yazma islemi var.Yazabilsin ancak hassas veri oldugundan okuma yapilamasin.
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    //SuperBuilder bizim burdaki field lari ozellikleri ile birlikte kalitiyor.
    @OneToOne//Turtettigimiz class larda userRole yazmadan burdan alicaz
    //hibernate 3.0 sikinti cikartir,cunku derki Admin Rolu 1 kisiye atanabilir(unique),gibi bir yapi kazanir
    //Bunun icin ManyToOne gibi kullanilmasi gerekir
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//DB deki Role bilgileri gizli kalsin.
    private UserRole userRole;

    private Gender gender;

}