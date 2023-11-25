package com.kaolinandcoal.pos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stripe.stripeterminal.external.callable.BluetoothReaderListener;
import com.stripe.stripeterminal.external.callable.Cancelable;
import com.stripe.stripeterminal.external.models.BatteryStatus;
import com.stripe.stripeterminal.external.models.ReaderDisplayMessage;
import com.stripe.stripeterminal.external.models.ReaderEvent;
import com.stripe.stripeterminal.external.models.ReaderInputOptions;
import com.stripe.stripeterminal.external.models.ReaderSoftwareUpdate;
import com.stripe.stripeterminal.external.models.TerminalException;

public class TerminalBluetoothReaderListener implements BluetoothReaderListener {
    @Override
    public void onRequestReaderInput(@NonNull ReaderInputOptions readerInputOptions) {
    }

    @Override
    public void onRequestReaderDisplayMessage(@NonNull ReaderDisplayMessage readerDisplayMessage) {
    }

    @Override
    public void onStartInstallingUpdate(@NonNull ReaderSoftwareUpdate update, @NonNull Cancelable cancelable) {
        // Show UI communicating that a required update has started installing
    }

    @Override
    public void onReportReaderSoftwareUpdateProgress(float progress) {
        // Update the progress of the install
    }

    @Override
    public void onFinishInstallingUpdate(@Nullable ReaderSoftwareUpdate update, @Nullable TerminalException e) {
        // Report success or failure of the update
    }

    @Override
    public void onReportReaderEvent(@NonNull ReaderEvent readerEvent) {

    }

    @Override
    public void onBatteryLevelUpdate(float v, @NonNull BatteryStatus batteryStatus, boolean b) {

    }

    @Override
    public void onReportAvailableUpdate(@NonNull ReaderSoftwareUpdate readerSoftwareUpdate) {

    }

    @Override
    public void onReportLowBatteryWarning() {

    }
}