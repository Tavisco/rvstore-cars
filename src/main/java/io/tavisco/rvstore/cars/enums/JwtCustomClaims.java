package io.tavisco.rvstore.cars.enums;

public enum JwtCustomClaims {
    UID("uid"),
    NICKNAME("nickname");

    String text;

    JwtCustomClaims(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
