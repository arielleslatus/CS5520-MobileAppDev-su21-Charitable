package edu.neu.charitable.utils.charityProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CharityProfileRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String charityNames[], postContent[];
    Context context;

    public CharityProfileRecyclerViewAdapter(Context ct, String charityNames[],
                                             String postContent[] ) {
        // Store values inside our constructor
        context = ct;
        charityNames = charityNames;
        postContent = postContent;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_donation, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView myText1, getMyText2;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.donation_text);
            myText2 = itemView.findViewById(R.id.donation_time_applauds);
        }
    }




}
