package org.example.user.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.exceptions.CustomException;
import org.example.user.model.domain.ApplicationUser;
import org.example.user.model.enums.UserRole;
import org.example.user.model.enums.UserStatus;
import org.example.user.model.request.LoginRequest;
import org.example.user.model.response.CertificateResponse;
import org.example.user.model.response.TokenResponse;
import org.example.user.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {


    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${jks.key}")
    private String jksKey;
    @Value("${jks.private-key}")
    private String privateKeyKey;
    @Value("${jks.name}")
    private String jksLocation;
    @Value("${jks.alias}")
    private String keyAlias;

    @Value("${spring.application.name}")
    private String applicationName;



    public Authentication getAuthentication(String token) {

        KeyStore.PrivateKeyEntry entry = getPrivateKeyEntry();

        JwtParser build = Jwts.parser().verifyWith(entry.getCertificate().getPublicKey())
                .build();

        Jwt<?, ?> parse = build.parse(token);
        Claims payload = (Claims) parse.getPayload();
        return new MyAuthentication(
                token,
                UserRole.valueOf((String) payload.get("role")),
                Long.valueOf(payload.getSubject()),
                (String) payload.get("username"),
                true);


    }

    private KeyStore.PrivateKeyEntry getPrivateKeyEntry() {
        try {
            ClassPathResource resource = new ClassPathResource(jksLocation);
            KeyStore jks = KeyStore.getInstance("JKS");
            jks.load(resource.getInputStream(), jksKey.toCharArray());
            KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(privateKeyKey.toCharArray());
            //getting private key entry from jks
            return (KeyStore.PrivateKeyEntry) jks.getEntry(keyAlias, protectionParameter);
        } catch (Exception e) {
            throw new CustomException("Unexpected error while parsing jks;", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public TokenResponse login(LoginRequest request) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsernameAndStatus(request.getUsername(), UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Username or password is incorrect!;", HttpStatus.FORBIDDEN));
        if (!passwordEncoder.matches(request.getPassword(), applicationUser.getPassword())) {
            throw new CustomException("Username or password is incorrect!;", HttpStatus.FORBIDDEN);
        }

        Date from = Date.from(OffsetDateTime.now().plusHours(1).toInstant());
        String jwt = Jwts.builder()
                .header()
                .keyId(applicationName)
                .type("JWT")
                .and()
                .expiration(from)
                .claim("username", applicationUser.getUsername())
                .subject(String.valueOf(applicationUser.getId()))
                .claim("firstname", applicationUser.getFirstname())
                .claim("role", applicationUser.getRole())
                .signWith(getPrivateKeyEntry().getPrivateKey())
                .compact();
        return new TokenResponse(jwt, from.getTime());

    }


    public CertificateResponse getCerts() {
      try {
          KeyStore.PrivateKeyEntry entry = getPrivateKeyEntry();
          X509Certificate certificate = (X509Certificate) entry.getCertificate();
          RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();

          CertificateResponse responseBody = new CertificateResponse();
          CertificateResponse.CertificateKeys certificateKeys = new CertificateResponse.CertificateKeys();
          certificateKeys.setAlg("RS256");
          certificateKeys.setKty(publicKey.getAlgorithm());
          Base64.Encoder encoder = Base64.getEncoder();
          certificateKeys.setX5t(encoder.encodeToString(certificate.getSignature()));
          certificateKeys.setX5c(List.of(encoder.encodeToString(certificate.getEncoded())));
          certificateKeys.setE(encoder.encodeToString(publicKey.getPublicExponent().toByteArray()));
          certificateKeys.setN(encoder.encodeToString(publicKey.getModulus().toByteArray()));
          certificateKeys.setUse("sig");
          certificateKeys.setKid(applicationName);
          responseBody.setKeys(List.of(certificateKeys));
          return responseBody;
      }catch (Exception e){
          throw new CustomException("Unexpected error while getting certificates", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
}
