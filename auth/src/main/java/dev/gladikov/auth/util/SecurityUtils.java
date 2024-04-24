package dev.gladikov.auth.util;

import dev.gladikov.auth.securtiy.authentication.UsernamePasswordAuthentication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.Base64;

@Slf4j
public class SecurityUtils {

    @SneakyThrows
    public static Authentication getCredentials(String auth) {
        String base64 = auth.replace ("Basic ","");
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        var result = new String(decodedBytes).split (":");
        return  new UsernamePasswordAuthentication (result[0],result[1]);
    }
}
