package com.cartify.authservice.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.JOSEObjectType;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtService {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public JwtService(
            @Value("${onecart.jwt.private:}") String privatePem,
            @Value("${onecart.jwt.public:}") String publicPem
    ) {
        if(privatePem != null && !privatePem.isBlank()) this.privateKey = parsePrivate(privatePem);
        if(publicPem != null && !publicPem.isBlank()) this.publicKey = parsePublic(publicPem);
    }

    public String createAccessToken(Long userId, String username, String email, long ttlSeconds) throws Exception {
        var claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("email", email)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(ttlSeconds)))
                .build();

        var header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
        var jws = new SignedJWT(header, claims);
        jws.sign(new RSASSASigner(privateKey));
        return jws.serialize();
    }

    public boolean verify(String jwt) throws Exception {
        var parsed = SignedJWT.parse(jwt);
        return parsed.verify(new RSASSAVerifier(publicKey));
    }

    private static RSAPrivateKey parsePrivate(String pem) {
        String clean = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        try {
            var key = Base64.getDecoder().decode(clean);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key));
        } catch (Exception e) {throw new IllegalStateException("Invalid private key", e);}
    }
    private static RSAPublicKey parsePublic(String pem) {
        String clean = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        try {
            var key = Base64.getDecoder().decode(clean);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
        } catch (Exception e) { throw new IllegalStateException("Invalid public key", e); }
    }
}
