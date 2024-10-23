package com.project.ecommerce_backend.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import com.project.ecommerce_backend.Exceptions.EmailFailureException;
import com.project.ecommerce_backend.Exceptions.EmailNotFoundException;
import com.project.ecommerce_backend.Exceptions.UserAlreadyExistsException;
import com.project.ecommerce_backend.Exceptions.UserNoVerifiedException;
import com.project.ecommerce_backend.Models.LocalUser;
import com.project.ecommerce_backend.Models.VerificationToken;
import com.project.ecommerce_backend.Models.dao.LocalUserDAO;
import com.project.ecommerce_backend.Models.dao.VerificationTokenDAO;
import com.project.ecommerce_backend.api.model.LoginBody;
import com.project.ecommerce_backend.api.model.PasswordResetBody;
import com.project.ecommerce_backend.api.model.RegistrationBody;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetup.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenDAO verificationTokenDAO;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDAO localUserDAO;

    @Autowired
    private  EncryptionService encryptionService;

        @Test
        @Transactional
        public void testRegisterUser() throws MessagingException {
            RegistrationBody body = new RegistrationBody();
            body.setUsername("UserA");
            body.setEmail("UserServiceTest$testRegisterUser@junit.com");
            body.setFirstName("FirstName");
            body.setLastName("LastName");
            body.setPassword("MySecretPassword123");

            Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(body), "Username should already be in use.");

            body.setUsername("UserServiceTest$testRegisterUser");
            body.setEmail("UserA@junit.com");
            Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(body), "Email should already be in use.");

            body.setEmail("UserServiceTest$testRegisterUser@junit.com");
            Assertions.assertDoesNotThrow(() -> userService.registerUser(body), "User should register successfully");
            Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());

        }

        @Test
        @Transactional
        public void testLoginUser() throws UserNoVerifiedException, EmailFailureException {
            LoginBody body = new LoginBody();
            body.setUsername("UserA-NotExists");
            body.setPassword("Password123-BadPassword");
            Assertions.assertNull(userService.loginUser(body), "The user should not exist.");
            body.setUsername("UserA");
            Assertions.assertNull(userService.loginUser(body));
            body.setPassword("PasswordA123");
            Assertions.assertNotNull(userService.loginUser(body), "The user should login successfully.");
            body.setUsername("UserB");
            body.setPassword("PasswordB123");
            try {
                userService.loginUser(body);
                Assertions.assertTrue(false, "User should not have email verified.");
            } catch (UserNoVerifiedException ex){
                Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent");
                Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
            }
            try {
                userService.loginUser(body);
                Assertions.assertTrue(false, "User should not have email verified.");
            } catch (UserNoVerifiedException ex){
                Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent");
                Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
            }


        }

        @Test
        @Transactional
        public void testVerifyUser() throws EmailFailureException {
            Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false");
            LoginBody body = new LoginBody();
            body.setUsername("UserB");
            body.setPassword("PasswordB123");
            try {
                userService.loginUser(body);
                Assertions.assertTrue(false, "User should not have email verified.");
            } catch (UserNoVerifiedException ex) {
                List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
                String token = tokens.get(0).getToken();
                Assertions.assertTrue(userService.verifyUser(token), "Token should be valid");
                Assertions.assertNotNull(body, "The user should now be verified");

            }
        }

        @Test
        @Transactional
        public void testForgotPassword() throws MessagingException {
            Assertions.assertThrows(EmailNotFoundException.class, () -> userService.forgotPassword("userNoExists@junit.com"));
            Assertions.assertDoesNotThrow(() -> userService.forgotPassword("UserA@junit.com"), "Non existing email should be rejected");
            Assertions.assertEquals("UserA@junit.com", greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
        }

        @Test
        @Transactional
        public void testResetPassword () {
            LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
            String token = jwtService.generatePasswordResetJWT(user);
            PasswordResetBody body = new PasswordResetBody();
            body.setToken(token);
        body.setPassword("Password123456");
        userService.resetPassword(body);
        user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.verifyPassword("Password123456", user.getPassword()), "Password change should be writen to DB");
    }


    }





