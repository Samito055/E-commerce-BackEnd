package com.project.ecommerce_backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.project.ecommerce_backend.Models.LocalUser;
import com.project.ecommerce_backend.Models.dao.LocalUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDAO localUserDAO;

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Test
    public void testVerificationTokenNotUsableForLogin(){
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should not contain username.");

    }

    @Test
    public void testAuthTokenReturnUsername(){
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for auth should contain ");

    }

    @Test
    public void testJWTNotGenerateByUs() throws UnsupportedEncodingException {
        String token =
        JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256("NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.getUsername(token));
    }

    @Test
    public void testJWTCorrectlySignedNoIssuer() throws UnsupportedEncodingException {
        String token = JWT.create().withClaim("USERNAME", "UserA")
                .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(InvalidClaimException.class, () -> jwtService.getUsername(token));
    }


}