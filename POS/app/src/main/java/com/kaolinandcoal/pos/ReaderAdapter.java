package com.kaolinandcoal.pos;

import com.kaolinandcoal.pos.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.stripe.stripeterminal.external.models.Reader;

import java.util.ArrayList;
import java.util.List;

// Our [RecyclerView.Adapter] implementation that allows us to update the list of readers
public class ReaderAdapter extends RecyclerView.Adapter<ReaderHolder> {
    @NonNull
    private List<Reader> readers;
    @NonNull
    private ReaderClickListener clickListener;

    public ReaderAdapter(@NonNull ReaderClickListener clickListener) {
        super();
        readers = new ArrayList<Reader>();
        this.clickListener = clickListener;
    }

    public void updateReaders(@NonNull List<Reader> readers) {
        this.readers = readers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return readers.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ReaderHolder holder, int position) {
        holder.view.setText(readers.get(position).getSerialNumber());
        holder.view.setOnClickListener(v -> {
            clickListener.onClick(readers.get(position));
        });
    }

    @Override
    public ReaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_reader, parent, false);
        return new ReaderHolder((MaterialButton) view);
    }
}