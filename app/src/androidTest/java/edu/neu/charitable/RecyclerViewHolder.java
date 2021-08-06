package edu.neu.charitable;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView eventName;
    public TextView eventDescription;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.eventName = itemView.findViewById(R.id.card);                                       // Find the cardName view from the XML file.
        this.eventDescription = itemView.findViewById(R.id.cardDescription);                         // Find the cardDescription view from the XML file.

    }
}