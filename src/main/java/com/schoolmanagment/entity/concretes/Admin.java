package com.schoolmanagment.entity.concretes;

import com.schoolmanagment.entity.abstracts.User;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@Getter
@Setter//ToString e gerek yok
@NoArgsConstructor
@SuperBuilder
public class Admin extends User {

    private boolean built_in;//ne olursa olsun silinmemeli



}