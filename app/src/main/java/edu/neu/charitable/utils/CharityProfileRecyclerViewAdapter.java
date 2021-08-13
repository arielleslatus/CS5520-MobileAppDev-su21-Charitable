package edu.neu.charitable.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.charitable.R;

public class CharityProfileRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String charityNames[], postContent[];
    Context context;

    public CharityProfileRecyclerViewAdapter(Context ct, String charityNames[],
                                             String postContent[]) {
        // Store values inside our constructor
        context = ct;
        this.charityNames = charityNames;
        this.postContent = postContent;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent,
                false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Sending text/content to the holder.
        if (holder instanceof  DonationViewHolder) {
            ((DonationViewHolder) holder).postText.setText(charityNames[position]);
        }

    }

    @Override
    public int getItemCount() {
        return charityNames.length;
    }

}