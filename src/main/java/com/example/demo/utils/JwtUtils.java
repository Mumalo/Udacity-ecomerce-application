package com.example.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.security.SecurityConstants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

import static com.example.demo.security.SecurityConstants.*;

@Component
public class JwtUtils {

    static public String createToken(UserDetails  userDetails){
       return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SecurityConstants.SECRET.getBytes()));
    }

    static public void editResponseHeader(String token, HttpServletResponse response){
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }

    static public boolean isBasicAuth(HttpServletRequest request)  {
        String requestHeader = request.getHeader(HEADER_STRING);
        return (requestHeader == null || !requestHeader.startsWith(TOKEN_PREFIX));
    }

    static public boolean isvalidToken(String token){
        Optional<String> subject = getSubject(token);
        return (token != null && subject.isPresent());
    }

    static public boolean isvalidHeader(String header){
        return header != null && header.startsWith(TOKEN_PREFIX);
    }

    /**
     * verify the token and return the subject (username)
     * @param token
     * @return
     */
    static public Optional<String> getSubject(String token){
        String username = JWT
                .require(Algorithm.HMAC256(SECRET.getBytes()))
                .build()
                .verify(token)
                .getSubject();
        return Optional.ofNullable(username);
    }



}
