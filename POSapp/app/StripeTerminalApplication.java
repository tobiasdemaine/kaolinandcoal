package com.kaolinandcoal.pos;

import android.app.Application;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.stripe.stripeterminal.TerminalApplicationDelegate;

public class StripeTerminalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // If you already have a class that extends 'Application',
        // put whatever code you had in the 'onCreate' method here.

        TerminalApplicationDelegate.onCreate(this);
    }
}