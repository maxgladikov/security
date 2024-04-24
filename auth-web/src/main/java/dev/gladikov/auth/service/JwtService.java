package dev.gladikov.auth.service;

import dev.gladikov.auth.dto.JwtAuthenticationResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Example for use RSA encrypt  https://github.com/jwtk/jjwt
 * set private key and public key to String variable
 * generated command below
 * ssh-keygen -t rsa -b 2048 -f jwtRS256.key
 * # Don't add passphrase
 * openssl rsa -in jwtRS256.key -pubout -outform PEM -out jwtRS256.key.pub
 * openssl pkcs8 -topk8 -in jwtRS256.key -nocrypt -out jwtRS256.pk8.key
 */

@Service
public class JwtService {

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    @Value("${token.issuer}")
    private String issuer;

    @Value("${token.lifetime}")
    private long tokenLifeTime;

    @Autowired
    public JwtService(@Value("${token.keys.private}") String privateKey,
                      @Value("${token.keys.public}") String publicKey) {
        this.privateKey = getPrivateKeyFromString (privateKey);
        this.publicKey = getPublicKeyFromString (publicKey);
    }

    public String generateToken(UserDetails user) {
        return Jwts.builder ()
            .claims ()
            .add ("authorities", user.getAuthorities ())
            .id (UUID.randomUUID ().toString ())
            .issuer (issuer)
            .and ()
            .expiration (new Date (System.currentTimeMillis () + 1000 * 60 * tokenLifeTime))
            .issuedAt (new Date (System.currentTimeMillis ()))
            .signWith (privateKey)
            .compact ();
    }

    public JwtAuthenticationResponse generateToken(Authentication user) {
        String token = Jwts.builder ()
            .claims ()
            .add ("authorities", user.getAuthorities ().stream ().map (GrantedAuthority::getAuthority)
                .collect (Collectors.joining (", ")))
            .subject ((String) user.getPrincipal ())
            .id (UUID.randomUUID ().toString ())
            .issuer (issuer)
            .and ()
            .expiration (new Date (System.currentTimeMillis () + 1000 * 60 * tokenLifeTime))
            .issuedAt (new Date (System.currentTimeMillis ()))
            .signWith (privateKey)
            .compact ();
        return new JwtAuthenticationResponse (token);
    }

    public boolean validateToken(String token) {
        Jwts.parser ().verifyWith (publicKey);
        if (isTokenExpired (token))
            throw new JwtException ("token is expired");
        return true;
    }

    public Function<String,String> validate = token -> {validateToken (token); return token;};
    public Function<String, Map<String,String>> userAndRoles = token ->

    public String extractUserName(String token) {
        return extractClaim (token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration (token).before (new Date ());
    }

    private Date extractExpiration(String token) {
        return extractClaim (token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims (token);
        return claimsResolvers.apply (claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser ()
            .verifyWith (publicKey).build ().parseSignedClaims (token).getPayload ();
    }

    private static SecretKey getSymmetricSigningKey(String symmetricKey) {
        byte[] keyBytes = Decoders.BASE64.decode (symmetricKey);
        return Keys.hmacShaKeyFor (keyBytes);
    }

    public static PublicKey getPublicKeyFromString(String key) {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace ("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace ("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replaceAll ("\n", "");
        byte[] encoded = java.util.Base64.getDecoder ().decode (publicKeyPEM);
        PublicKey pubKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance ("RSA");
            pubKey = kf.generatePublic (new X509EncodedKeySpec (encoded));
        } catch (Exception e) {
            throw new RuntimeException ("Could not parse public key");
        }
        return pubKey;
    }

    public static PrivateKey getPrivateKeyFromString(String key) {
        String privKeyPEM = key.replace ("-----BEGIN PRIVATE KEY-----\n", "");
        privKeyPEM = privKeyPEM.replace ("-----END PRIVATE KEY-----", "");
        privKeyPEM = privKeyPEM.replace ("\n", "");
        byte[] byteKey = Base64.getDecoder ().decode (privKeyPEM);
        PKCS8EncodedKeySpec PK8privateKey = new PKCS8EncodedKeySpec (byteKey);
        PrivateKey pri = null;
        try {
            KeyFactory kf = KeyFactory.getInstance ("RSA");
            pri = kf.generatePrivate (PK8privateKey);
        } catch (Exception e) {
            throw new RuntimeException ("Could not parse private key");
        }
        return pri;
    }

    public static String generateRandomSymmetricKey() {
        SecretKey key = Jwts.SIG.HS256.key ().build ();
        return Encoders.BASE64.encode (key.getEncoded ());
    }

    public static Map<String, String> generateAsymmetricKeys() {
        KeyPair keyPair = Jwts.SIG.RS256.keyPair ().build ();
        PrivateKey pri = keyPair.getPrivate ();
        PublicKey pub = keyPair.getPublic ();
        Map<String, String> map = new HashMap<> ();
        map.put ("private", Encoders.BASE64.encode (pri.getEncoded ()));
        map.put ("public", Encoders.BASE64.encode (pub.getEncoded ()));
        return map;
    }


}

