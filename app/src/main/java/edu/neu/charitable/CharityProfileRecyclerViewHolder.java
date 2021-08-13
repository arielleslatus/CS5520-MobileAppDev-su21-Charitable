package edu.neu.charitable;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CharityProfileRecyclerViewHolder extends RecyclerView.ViewHolder{


    public TextView eventName;
    public TextView eventDescription;

    public CharityProfileRecyclerViewHolder(View itemView) {
        super(itemView);

        // Find the cardName view from the XML file.
        this.eventName = itemView.findViewById(R.id.card);


        // Find the cardDescription view from the XML file.
        this.eventDescription = itemView.findViewById(R.id.cardDescription);

    }
}
