package com.kaolinandcoal.pos;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

// A simple [RecyclerView.ViewHolder] that contains a representation of each discovered reader
public class ReaderHolder extends RecyclerView.ViewHolder {
    public final MaterialButton view;

    public ReaderHolder(@NonNull MaterialButton view) {
        super(view);
        this.view = view;
    }
}