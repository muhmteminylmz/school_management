package com.schoolmanagment.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;

    private String username;

    private String name;

    private Boolean isAdviser;//User rolleri gelicek(teacher) o yuzden eklendi(cunku Adviser Teacher yok)

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    //Bu constructor POJO-UserDetail donusumu icin
    public UserDetailsImpl(Long id, String username, String name, Boolean isAdviser, String password,String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.isAdviser = isAdviser;
        this.password = password;
        //Userimizda GrantedAuthory diye bisey yok bunun amaci user larimizi gondermesi
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        this.authorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean equals(Object o) {
        if (this == o)//kendisiyle karsilastiriliyor
        {
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id,user.id);
        //Hangisinden yakalarsak.(2 farkli user ayni kisi mi??)
    }
}
