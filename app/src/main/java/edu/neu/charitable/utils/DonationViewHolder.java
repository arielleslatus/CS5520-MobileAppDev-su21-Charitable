package edu.neu.charitable.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.charitable.R;

public class DonationViewHolder extends RecyclerView.ViewHolder {

    public Button buttonMatch;
    public Button buttonShare;
    public Button buttonApplaud;
    public TextView postText;
    public TextView timeText;
    public CardView donationPostCard;

    public DonationViewHolder(@NonNull View itemView) {
        super(itemView);

        postText = (TextView) itemView.findViewById(R.id.donation_text);
        timeText = (TextView) itemView.findViewById(R.id.donation_time_applauds);
        buttonMatch = (Button) itemView.findViewById(R.id.donation_match);
        buttonShare= (Button) itemView.findViewById(R.id.donation_share);
        buttonApplaud = (Button) itemView.findViewById(R.id.donation_applaud);
        donationPostCard = (CardView) itemView.findViewById(R.id.donation_card);
    }
}