package org.example.user.model.response;

import lombok.Data;

import java.util.List;

@Data
public class CertificateResponse {

    private List<CertificateKeys> keys;
    @Data
    public static class CertificateKeys{
        private String kid;
        private String kty;
        private String alg;
        private String use;
        private String n;
        private String e;
        private List<String> x5c;
        private String x5t;

    }
}
