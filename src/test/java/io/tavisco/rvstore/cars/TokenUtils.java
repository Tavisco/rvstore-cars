package io.tavisco.rvstore.cars;

import io.smallrye.jwt.build.Jwt;
import io.tavisco.rvstore.cars.enums.JwtCustomClaims;
import lombok.experimental.UtilityClass;

@UtilityClass
class TokenUtils {

    private static final String PRIMARY_MOCK_UID = "123UID";
    private static final String PRIMARY_MOCK_NICKNAME = "Nickname";
    private static final String SECONDARY_MOCK_UID = "456UID";
    private static final String SECONDARY_MOCK_NICKNAME = "Apelido";

    static String generateValidPrimaryUserToken() {
        return Jwt.upn("primary@quarkus.io")
                .issuer("https://quarkus.io/using-jwt-rbac")
                .groups("Everyone")
                .claim(JwtCustomClaims.UID.getText(), PRIMARY_MOCK_UID)
                .claim(JwtCustomClaims.NICKNAME.getText(), PRIMARY_MOCK_NICKNAME)
                .sign();
    }

    static String generateValidSecondaryUserToken() {
        return Jwt.upn("secondary@quarkus.io")
                .issuer("https://quarkus.io/using-jwt-rbac")
                .groups("Everyone")
                .claim(JwtCustomClaims.UID.getText(), SECONDARY_MOCK_UID)
                .claim(JwtCustomClaims.NICKNAME.getText(), SECONDARY_MOCK_NICKNAME)
                .sign();
    }

}