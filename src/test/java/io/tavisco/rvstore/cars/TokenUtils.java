package io.tavisco.rvstore.cars;

import io.smallrye.jwt.build.Jwt;
import io.tavisco.rvstore.cars.enums.JwtCustomClaims;
import lombok.experimental.UtilityClass;

@UtilityClass
class TokenUtils {

    private static final String MOCK_UID = "123UID";
    private static final String MOCK_NICKNAME = "Nickname";

    static String generateValidUserToken() {
        return Jwt.upn("jdoe@quarkus.io")
                .issuer("https://quarkus.io/using-jwt-rbac")
                .groups("Everyone")
                .claim(JwtCustomClaims.UID.getText(), MOCK_UID)
                .claim(JwtCustomClaims.NICKNAME.getText(), MOCK_NICKNAME)
                .sign();
    }

}