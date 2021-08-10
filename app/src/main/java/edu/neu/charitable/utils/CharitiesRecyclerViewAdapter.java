package edu.neu.charitable.utils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.neu.charitable.DonateDummy;
import edu.neu.charitable.R;
import edu.neu.charitable.models.Charity;

public class CharitiesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<Charity> chars;
    private FirebaseDatabase mDB;

    public CharitiesRecyclerViewAdapter(List<Charity> chars) {
        this.chars = chars;
        mDB = FirebaseDatabase.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charity, parent, false);
            return new CharitiesRecyclerViewAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new CharitiesRecyclerViewAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharitiesRecyclerViewAdapter.ItemViewHolder) {

            Charity chr = chars.get(position);

            Button buttonDonate = ((ItemViewHolder) holder).buttonDonate;
            Button buttonFollow = ((ItemViewHolder) holder).buttonFollow;
            TextView charityName = ((ItemViewHolder) holder).charityName;
            TextView charityUrl = ((ItemViewHolder) holder).charityUrl;
            TextView charityCity = ((ItemViewHolder) holder).charityCity;

            charityName.setText(chr.name);
            charityUrl.setText(chr.url);
            charityCity.setText(chr.city + " . " + chr.country);


            buttonDonate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DonateDummy.class);
                    intent.putExtra("AUTOFILL_CHARITY", chr.name);
                    v.getContext().startActivity(intent);
                }
            });


            buttonFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String this_user = FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid();

                    mDB.getReference("username_charities")
                            .child(this_user)
                            .orderByChild("name")
                            .equalTo(chr.name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(v.getContext(), "already following", Toast.LENGTH_LONG).show();
                            } else {
                                mDB.getReference("username_charities")
                                        .child(this_user)
                                        .push()
                                        .setValue(chr)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(v.getContext(), "followed " + chr.name + "!", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(v.getContext(), "problem following " + chr.name, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });


        } else if (holder instanceof CharitiesRecyclerViewAdapter.LoadingViewHolder) {
            ((CharitiesRecyclerViewAdapter.LoadingViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return chars == null ? 0 : chars.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chars.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public Button buttonDonate;
        public Button buttonFollow;
        public TextView charityName;
        public TextView charityUrl;
        public TextView charityCity;
        public CardView charityCard;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonFollow = (Button) itemView.findViewById(R.id.add_charity);
            buttonDonate = (Button) itemView.findViewById(R.id.donate_charity);
            charityName = (TextView) itemView.findViewById(R.id.charity_name);
            charityUrl = (TextView) itemView.findViewById(R.id.charity_url);
            charityCity = (TextView) itemView.findViewById(R.id.charity_city);
            charityCard = (CardView) itemView.findViewById(R.id.card_charity);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar_loading);
        }
    }




}
