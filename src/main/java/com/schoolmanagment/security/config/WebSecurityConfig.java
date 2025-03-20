package com.schoolmanagment.security.config;

import com.schoolmanagment.security.jwt.AuthEntryPointJwt;
import com.schoolmanagment.security.jwt.AuthTokenFilter;
import com.schoolmanagment.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().//frontend ve backend i farkli serverdaysa calistirmayan guvenlik onlemi
        and().csrf().disable().
                exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().
                authorizeRequests().antMatchers(AUTH_WHITE_LIST).permitAll().
                anyRequest().authenticated();

        http.headers().frameOptions().sameOrigin();
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static final String[] AUTH_WHITE_LIST = {
            "/",
            "/index*",
            "/static/**",
            "/*.js",
            "/*.json",
            "/contactMessages/save",
            "/auth/login",
            "/v3/api-docs/**", //swagger
            "/swagger-ui/**", //swagger
            "/swagger*/**", //swagger
    };

    @Bean
    public WebMvcConfigurer corsCConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //corse uzerinde degislik yapmamizi sagliyor

                registry.addMapping("/**").//Tum URL leri kapsayacak
                    allowedOrigins("*").//tum kaynaklara izin ver(Farkli protokol,port ...)
                    allowedHeaders("*").//hangi headlara(request,response taki headlerlar) izin verilecek
                    allowedMethods("*");//tum HTTP methodlara izin verildi.
            }
        };
    }
}