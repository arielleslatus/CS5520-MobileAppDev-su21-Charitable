package edu.neu.charitable.utils;

import android.provider.ContactsContract;
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

import edu.neu.charitable.R;
import edu.neu.charitable.models.User;


public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<User> users;
    private FirebaseDatabase mDB;

    public UsersRecyclerViewAdapter(List<User> users) {
        this.users = users;
        mDB = FirebaseDatabase.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            User user = users.get(position);

            Button buttonFriend = ((ItemViewHolder) holder).buttonFriend;
            TextView userName = ((ItemViewHolder) holder).userName;
            TextView userHandle = ((ItemViewHolder) holder).userHandle;
            TextView userCity = ((ItemViewHolder) holder).userCity;

            userName.setText(user.fullName);
            userHandle.setText("@" + user.username);
            userCity.setText(user.city);


            buttonFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String this_user = FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid();

                    //get DB id for other user
                    mDB.getReference("username_id").child(user.username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String other_user = (String) snapshot.getValue();

                                //checking if friend exists
                                mDB.getReference("user_friends").child(this_user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean friend_exists = false;
                                        if (snapshot.exists()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                if (ds.exists()) {
                                                    String found = ds.getValue(String.class);
                                                    if (found.equals(other_user) || found.equals(this_user)) {
                                                        friend_exists = true;
                                                    }
                                                }
                                            }
                                        }

                                        //add friend
                                        if (!friend_exists) {
                                            mDB.getReference("user_friends").child(this_user).push().setValue(other_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText( ((ItemViewHolder) holder).userCard.getContext(), "Friend Added", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText( ((ItemViewHolder) holder).userCard.getContext(), "Friend already added", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText( ((ItemViewHolder) holder).userCard.getContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });


        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return users.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public Button buttonFriend;
        public TextView userName;
        public TextView userHandle;
        public TextView userCity;
        public CardView userCard;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonFriend = (Button) itemView.findViewById(R.id.add_user);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userHandle = (TextView) itemView.findViewById(R.id.user_handle);
            userCity = (TextView) itemView.findViewById(R.id.user_city);
            userCard = (CardView) itemView.findViewById(R.id.card_user);

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
