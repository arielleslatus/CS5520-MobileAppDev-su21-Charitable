package edu.neu.charitable;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HomepageRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView eventName;
    public TextView eventDescription;

    public HomepageRecyclerViewHolder(View itemView) {
        super(itemView);
        this.eventName = itemView.findViewById(R.id.card);                                       // Find the cardName view from the XML file.
        this.eventDescription = itemView.findViewById(R.id.cardDescription);                         // Find the cardDescription view from the XML file.

    }
}