package com.kaolinandcoal.pos;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * The 'BackendService' interface handles the two simple calls we need to make to our backend.
 * This represents YOUR backend, so feel free to change the routes accordingly.
 */
public interface BackendService {

    /**
     * Get a connection token string from the backend
     */
    @POST("connection_token")
    Call<ConnectionToken> getConnectionToken();


    /**
     * Capture a specific payment intent on our backend
     */
    @FormUrlEncoded
    @POST("capture_payment_intent")
    Call<Void> capturePaymentIntent(@Field("payment_intent_id") @NotNull String id);
}