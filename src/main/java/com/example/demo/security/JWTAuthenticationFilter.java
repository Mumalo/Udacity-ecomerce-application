package com.example.demo.security;

import com.example.demo.model.requests.AuthenticationRequest;
import com.example.demo.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
/**
 * responsible for the authentication process
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    Logger logger = LoggerFactory.getLogger(UsernamePasswordAuthenticationFilter.class);

    JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException {
        try {
            AuthenticationRequest credentials = new ObjectMapper()
                    .readValue(httpServletRequest.getInputStream(), AuthenticationRequest.class);
            logger.info("Authenticating user" + credentials);
            String username = credentials.getUsername();
            String password = credentials.getPassword().trim();
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (AuthenticationException e){
            logger.error("Error authenticating user "  + e.getLocalizedMessage());
            throw new BadCredentialsException("invalid login info");
        } catch(IOException e){
            logger.error("Error authenticating user "  + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        logger.info("Successfully authenticate user");
        String jwtToken = JwtUtils.createToken(user);
        JwtUtils.editResponseHeader(jwtToken, response);
    }
}
