package com.kaolinandcoal.pos;

import androidx.annotation.NonNull;

import com.stripe.stripeterminal.external.callable.TerminalListener;
import com.stripe.stripeterminal.external.models.ConnectionStatus;
import com.stripe.stripeterminal.external.models.PaymentStatus;
import com.stripe.stripeterminal.external.models.Reader;

public class TerminalEventListener implements TerminalListener {
    @Override
    public void onConnectionStatusChange(@NonNull ConnectionStatus connectionStatus) {
    }

    @Override
    public void onPaymentStatusChange(@NonNull PaymentStatus paymentStatus) {
    }

    @Override
    public void onUnexpectedReaderDisconnect(@NonNull Reader reader) {
    }
}