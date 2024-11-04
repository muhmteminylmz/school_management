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
//Eski versiyonda 2 tane methodu override etmemiz gerekiyordu
//Artik Bean olarak olusturarak hallediyoruz.Yeni versiyonda daha performansli

    //Genelde Security kodlari boilerplate dir(Yani sabittir kopyala yapistir ile yazilir)

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

    //Bunu ekliyoruz ki olusacak olan encoderi burda kullanabilelim
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        //pom a baska encoder ekleyip burda kullanabiliriz.
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().//frontend ve backend imiz farkli serverdaysa calistirmayan guvenlik onlemi
        //(eskiden ayni server da ayni portla oluyordu,suan farkli serverlarda kullanilabiliyor.
        and().csrf().disable().
                exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and().//unauthorized hata formatini degistirdik.
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
            "/swagger*/**" //bu yukardakini kapsiyor gibi
    };

    @Bean
    public WebMvcConfigurer corsCConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //corse uzerinde degislik yapmamizi sagliyor

                registry.addMapping("/**").//Tum URL leri kapsayacagini soyledik
                    allowedOrigins("*").//tum kaynaklara izin verecegimizi soyluyoruz.(Farkli protokol,port ...)
                    allowedHeaders("*").//hangi headlara(request,response taki headlerlar) izin verilecek
                    allowedMethods("*");//tum HTTP methodlara izin verildi.
            }
        };
    }
}