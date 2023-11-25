package com.kaolinandcoal.pos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.external.callable.Callback;
import com.stripe.stripeterminal.external.callable.DiscoveryListener;
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback;
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration;
//import com.stripe.stripeterminal.external.models.DiscoveryMethod;
import com.stripe.stripeterminal.external.models.PaymentIntent;
import com.stripe.stripeterminal.external.models.PaymentIntentParameters;
import com.stripe.stripeterminal.external.models.PaymentMethodType;
import com.stripe.stripeterminal.external.models.Reader;
import com.stripe.stripeterminal.external.models.TerminalException;
import com.stripe.stripeterminal.log.LogLevel;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_LOCATION = 1;

    // Register the permissions callback to handles the response to the system
    // permissions dialog.
    private final ActivityResultLauncher requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> MainActivity.this.onPermissionResult(result));

    private final PaymentIntentParameters paymentIntentParams = new PaymentIntentParameters.Builder(
            Arrays.asList(PaymentMethodType.CARD_PRESENT))
            .setAmount(500)
            .setCurrency("aud")
            .build();

    private final DiscoveryConfiguration discoveryConfig = new DiscoveryConfiguration.BluetoothDiscoveryConfiguration(0,
            true);

    private final ReaderClickListener readerClickListener = new ReaderClickListener(
            new WeakReference<MainActivity>(this));
    private final ReaderAdapter readerAdapter = new ReaderAdapter(readerClickListener);

    /*** Payment processing callbacks ***/

    // (Step 1 found below in the startPayment function)
    // Step 2 - once we've created the payment intent, it's time to read the card
    private final PaymentIntentCallback createPaymentIntentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NonNull PaymentIntent paymentIntent) {
            Terminal.getInstance().collectPaymentMethod(paymentIntent, collectPaymentMethodCallback,null);
        }

        @Override
        public void onFailure(@NonNull TerminalException e) {
            // Update UI w/ failure
        }
    };

    // Step 3 - we've collected the payment method, so it's time to confirm the
    // payment
    private final PaymentIntentCallback collectPaymentMethodCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NonNull PaymentIntent paymentIntent) {
            Terminal.getInstance().confirmPaymentIntent(paymentIntent, confirmPaymentIntentCallback);
        }

        @Override
        public void onFailure(@NonNull TerminalException e) {
            // Update UI w/ failure
        }
    };

    // Step 4 - we've confirmed the payment! Show a success screen
    private final PaymentIntentCallback confirmPaymentIntentCallback = new PaymentIntentCallback() {
        @Override
        public void onSuccess(@NonNull PaymentIntent paymentIntent) {
            try {
                ApiClient.capturePaymentIntent(paymentIntent.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            runOnUiThread(() -> {
                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,
                        R.style.Theme_MaterialComponents_DayNight_DarkActionBar1))
                        .setMessage("Successfully captured payment!").setCancelable(true).create().show();
            });
        }

        @Override
        public void onFailure(@NonNull TerminalException e) {
            // Update UI w/ failure
        }
    };

    private void startPayment() {
        // Step 1: create payment intent
        Terminal.getInstance()
                .createPaymentIntent(paymentIntentParams, createPaymentIntentCallback);
    }

    private boolean verifyGpsEnabled() {
        final LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = false;
        try {
            if (locationManager != null) {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception e) {
        }

        if (!gpsEnabled) {
            // notify user
            new AlertDialog.Builder(
                    new ContextThemeWrapper(this, R.style.Theme_MaterialComponents_DayNight_DarkActionBar1))
                    .setMessage("Please enable location services")
                    .setCancelable(false)
                    .setPositiveButton("Open location settings",
                            (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .create()
                    .show();
        }

        return gpsEnabled;
    }

    private void initialize() {
        // Initialize the Terminal as soon as possible
        try {
            Terminal.initTerminal(
                    getApplicationContext(), LogLevel.VERBOSE, new TokenProvider(),
                    new TerminalEventListener());
        } catch (TerminalException e) {
            throw new RuntimeException(
                    "Location services are required in order to initialize " +
                            "the Terminal.",
                    e);
        }

        final boolean isConnectedToReader = Terminal.getInstance().getConnectedReader() != null;
        updateReaderConnection(isConnectedToReader);
    }

    private void discoverReaders() {
        final Callback discoveryCallback = new Callback() {
            @Override
            public void onSuccess() {
                // Update your UI
            }

            @Override
            public void onFailure(@NonNull TerminalException e) {
                // Update your UI
            }
        };

        final DiscoveryListener discoveryListener = new DiscoveryListener() {
            @Override
            public void onUpdateDiscoveredReaders(@NonNull List<Reader> readers) {
                runOnUiThread(() -> readerAdapter.updateReaders(readers));
            }
        };

        Terminal.getInstance()
                .discoverReaders(discoveryConfig, discoveryListener, discoveryCallback);

    }

    void updateReaderConnection(boolean isConnected) {
        final RecyclerView recyclerView = findViewById(R.id.reader_recycler_view);

        findViewById(R.id.collect_payment_button)
                .setVisibility(isConnected ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.discover_button)
                .setVisibility(isConnected ? View.INVISIBLE : View.VISIBLE);

        recyclerView.setVisibility(isConnected ? View.INVISIBLE : View.VISIBLE);

        if (!isConnected) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(readerAdapter);
        }
    }

    // Upon starting, we should verify we have the permissions we need, then start
    // the app
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        findViewById(R.id.discover_button).setOnClickListener(v -> discoverReaders());
        findViewById(R.id.collect_payment_button).setOnClickListener(v -> startPayment());
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermissionsIfNecessary();
    }

    private Boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this,
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsIfNecessary() {
        if (Build.VERSION.SDK_INT >= 31) {
            requestPermissionsIfNecessarySdk31();
        } else {
            requestPermissionsIfNecessarySdkBelow31();
        }
    }

    private void requestPermissionsIfNecessarySdkBelow31() {
        // Check for location permissions
        if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // If we don't have them yet, request them before doing anything else
            requestPermissionLauncher.launch(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION));
        } else if (!Terminal.isInitialized() && verifyGpsEnabled()) {
            initialize();
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private void requestPermissionsIfNecessarySdk31() {
        // Check for location and bluetooth permissions
        List<String> deniedPermissions = new ArrayList<>();
        if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            deniedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!isGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
            deniedPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (!isGranted(Manifest.permission.BLUETOOTH_SCAN)) {
            deniedPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
        }

        if (!deniedPermissions.isEmpty()) {
            // If we don't have them yet, request them before doing anything else
            requestPermissionLauncher.launch(deniedPermissions.toArray(new String[deniedPermissions.size()]));
        } else if (!Terminal.isInitialized() && verifyGpsEnabled()) {
            initialize();
        }
    }

    /**
     * Receive the result of our permissions check, and initialize if we can
     */
    void onPermissionResult(Map<String, Boolean> result) {
        List<String> deniedPermissions = result.entrySet().stream()
                .filter(it -> !it.getValue())
                .map(it -> it.getKey())
                .collect(Collectors.toList());

        // If we receive a response to our permission check, initialize
        if (deniedPermissions.isEmpty() && !Terminal.isInitialized() && verifyGpsEnabled()) {
            initialize();
        }
    }
}