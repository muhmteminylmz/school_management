package com.schoolmanagment.controller;

import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.payload.request.LoginRequest;
import com.schoolmanagment.payload.response.AuthResponse;
import com.schoolmanagment.security.jwt.JwtUtils;
import com.schoolmanagment.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    public final JwtUtils jwtUtils;
    public final AuthenticationManager authenticationManager;

    @PostMapping("/login") // http://localhost:8080/auth/login
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        //bunlarin service katmani olmaz.
        //Gelen requestin icinden kullanici ve parola bilgisi alinir
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        //authenticationManager uzerinden kullanici valide ediyoruz.
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(username,password));
        //Valide edilen kullanici contexte atilir
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //JWT token olusturluyor
        String token = jwtUtils.generateJwtToken(authentication);
        //Validasyonlarla Bearer in onemi yok ancak on taraf gorsun diye ekledik

        //Normalde kod bitti ancak on tarafa(response) rolun de gitmesini istiyoruz.
        //security den role almak istiyoruz.UserDetails de rol yok grantedAuth var o yuzden biraz ugrastiracak
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<String> roles = userDetails.
                getAuthorities().
                stream().
                map(GrantedAuthority::getAuthority).//Bu bize GrantedAuth rollerini String olarak bize getirir.
                collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();//Zaten 1 tane rolu var(rolu yoksa diye Optinal kullandik)
        //Sistemde suan herkesin tek rolu var

        //AuthResponse Bu da yapilabilir(cok sevmiyoruz)
        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.username(username);
        authResponse.token(token);
        authResponse.name(userDetails.getName());

        //Rol mevcutsa ve TEACHER ise advisor durumu getiriliyor.
        if(role.isPresent()){
            authResponse.role(role.get());
            if (role.get().equalsIgnoreCase(RoleType.TEACHER.name())){
                authResponse.isAdvisor(userDetails.getIsAdviser().toString());
            }
        }

        //AuthResponse nesnesi ResponseEntity ile donduruyoruz
        return ResponseEntity.ok(authResponse.build());
    }

}