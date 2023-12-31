package com.kaolinandcoal.pos;

import androidx.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.stripe.stripeterminal.external.models.ConnectionTokenException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Use 10.0.2.2 when running with an emulator
    // See
    // https://developer.android.com/studio/run/emulator-networking.html#networkaddresses
    //public static final String BACKEND_URL = "https://terminal.kaolinandcoal.com";
    //public static final String BACKEND_URL = "http://localhost:4242";
    public static final String BACKEND_URL = "http://192.168.0.103:4242";

    private static final OkHttpClient mClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();
    private static final Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .client(mClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final BackendService mService = mRetrofit.create(BackendService.class);

    public static String createConnectionToken() throws ConnectionTokenException {
        try {
            final Response<ConnectionToken> result = mService.getConnectionToken().execute();
            if (result.isSuccessful() && result.body() != null) {
                return result.body().getSecret();
            } else {
                throw new ConnectionTokenException("Creating connection token failed");
            }
        } catch (IOException e) {
            throw new ConnectionTokenException("Creating connection token failed", e);
        }
    }

    public static void capturePaymentIntent(@NonNull String id) throws IOException {
        mService.capturePaymentIntent(id).execute();
    }
}