package com.schoolmanagment.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
//Biz bu class i yazmak zorunda degiliz ancak 401 hatasini sekillendirmek istedigimizden yazdik
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
//Security exception lari bu classdan firlatiyoruz(unauthorized(401) gibi)

    //Bu sinif, yetkilendirme hatasi durumunda islem yapilmasini sagliyor.

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //Bir unauthorized hatasinda bu method calisir.
        //logger kullanilarak yetkilendirme hatasi kaydediliyor.
        logger.error("Unauthorized error : {}", authException.getMessage());

        //response icerigi JSON olacak ve HTTP Status Code da 401, UnAuthorized olacagini setliyoruz
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status",HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path",request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(),body);
    }


}