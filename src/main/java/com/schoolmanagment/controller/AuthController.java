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
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(username,password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //JWT token olusturluyor
        String token = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<String> roles = userDetails.
                getAuthorities().
                stream().
                map(GrantedAuthority::getAuthority).
                collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.username(username);
        authResponse.token(token);
        authResponse.name(userDetails.getName());

        if(role.isPresent()){
            authResponse.role(role.get());
            if (role.get().equalsIgnoreCase(RoleType.TEACHER.name())){
                authResponse.isAdvisor(userDetails.getIsAdviser().toString());
            }
        }

        return ResponseEntity.ok(authResponse.build());
    }

}