package dev.gladikov.auth.security;

import dev.gladikov.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class JwtServiceTest {

    @Autowired
    JwtService service;

    @Value("${token.keys.private}")
    String privateKey;
    @Value("${token.keys.public}")
    String publicKey;

    @Test
    void shouldCreateBean() {
        assertNotNull (service);
    }

    @Test
    void shouldValidateToken() {
        var token = service.generateToken (new User ("user", "password",
            List.of (new SimpleGrantedAuthority ("user"))));

        assertTrue (service.validate (token));
    }

    @Test
    void shouldParseClaims(){

    }

    @Test
    void shouldParseKeys() {
        assertNotNull (JwtService.getPrivateKeyFromString (privateKey));
        assertNotNull (JwtService.getPublicKeyFromString (publicKey));
    }

}