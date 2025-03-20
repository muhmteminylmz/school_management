package com.schoolmanagment.entity.concretes;

import com.schoolmanagment.entity.abstracts.User;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Dean extends User {


}