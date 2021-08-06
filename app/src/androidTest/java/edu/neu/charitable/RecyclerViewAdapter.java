package edu.neu.charitable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<EventCard> cardList;
    private RecyclerViewHolder recyclerViewHolder;

    public RecyclerViewAdapter(ArrayList<EventCard> cardList) {
        this.cardList = cardList;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {                  // Create a Recycler View Holder for a new card.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_card,        // Inflate the layout to set up the view.
                parent,
                false);
        this.recyclerViewHolder = new RecyclerViewHolder(view);
        return this.recyclerViewHolder;                                         // Return a new RecyclerViewHolder with the inflated view and listener.
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        EventCard currentCard = this.cardList.get(position);                                         // Get a reference to the current card at the given position.
        viewHolder.eventName.setText(currentCard.getName());                                     // Pass the given View Holder the current card's name.
        viewHolder.eventDescription.setText(currentCard.getDescription());                       // Pass the given View Holder the current card's description.
    }

    @Override
    public int getItemCount() {
        return this.cardList.size();                                                                // Return the number of cards in the list.
    }

    public RecyclerViewHolder getRecyclerViewHolder() {
        return this.recyclerViewHolder;
    }

}