package com.kaolinandcoal.pos;

import androidx.annotation.NonNull;

// A one-field data class used to handle the connection token response from our backend
public class ConnectionToken {
    @NonNull
    private final String secret;

    public ConnectionToken(@NonNull String secret) {
        this.secret = secret;
    }

    @NonNull
    public String getSecret() {
        return secret;
    }
}