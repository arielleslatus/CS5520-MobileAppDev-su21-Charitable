package edu.neu.charitable.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import edu.neu.charitable.R;
import edu.neu.charitable.models.Donation;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.models.User;

public class DirectDonationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_DONATION = 1;

    public List<Donation> donations;
    private FirebaseDatabase mDb;
    private String current_user;

    public DirectDonationRecyclerViewAdapter(List<Donation> donations) {
        this.donations = donations;
        mDb = FirebaseDatabase.getInstance();
        current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dd_donation, parent, false);
        return new DirectDonationRecyclerViewAdapter.DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DirectDonationRecyclerViewAdapter.DonationViewHolder) {
            bindDonationView(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return donations == null ? 0 : donations.size();
    }

    @Override
    public int getItemViewType(int position) {
        Donation d = donations.get(position);
        return VIEW_TYPE_DONATION;
    }

    private class DonationViewHolder extends RecyclerView.ViewHolder {

        public Button buttonThank;
        public Button buttonWrite;
        public TextView postText;
        public TextView timeText;
        public CardView donationPostCard;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);

            postText = (TextView) itemView.findViewById(R.id.dd_don_text);
            timeText = (TextView) itemView.findViewById(R.id.dd_don_time);
            buttonThank = (Button) itemView.findViewById(R.id.dd_don_thank);
            buttonWrite= (Button) itemView.findViewById(R.id.dd_don_thank_words);
            donationPostCard = (CardView) itemView.findViewById(R.id.dd_don_card);
        }
    }

    private void bindDonationView(@NonNull RecyclerView.ViewHolder holder, int position) {
        Donation donation = donations.get(position);

        Button buttonThank = ((DonationViewHolder) holder).buttonThank;
        Button buttonWrite = ((DonationViewHolder) holder).buttonWrite;
        TextView postText = ((DonationViewHolder) holder).postText;
        TextView timeText = ((DonationViewHolder) holder).timeText;
        CardView donationPostCard = ((DonationViewHolder) holder).donationPostCard;

        mDb.getReference("Users").child(donation.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User u = snapshot.getValue(User.class);

                    postText.setText("@" + u.username + " has sent you $" + donation.amount + "!");
                } else {
                    postText.setText("You have received $" + donation.amount + "!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(donation.timestamp), TimeZone.getDefault().toZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a ' ' MM.dd");
        timeText.setText(formatter.format(dt));

        thankListener(holder, buttonThank, donation);
        writeListener(holder, buttonWrite, donation);


    }

    private void thankListener(RecyclerView.ViewHolder holder, Button bt, Donation don) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post("thank", current_user, don.charity, don.user, don.amount, "", 0);
                mDb.getReference("user_posts").child(don.user).push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Toast.makeText(((DirectDonationRecyclerViewAdapter.DonationViewHolder) holder).donationPostCard.getContext(), "User Thanked Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void writeListener(RecyclerView.ViewHolder holder, Button bt, Donation don) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(((DonationViewHolder) holder).donationPostCard.getContext());

                alert.setTitle("Thank the Sender");

                final EditText input = new EditText(((DonationViewHolder) holder).donationPostCard.getContext());
                input.setSingleLine(false);
                input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                input.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                input.setMaxLines(1000);
                input.setVerticalScrollBarEnabled(true);
                input.setMovementMethod(new ScrollingMovementMethod());

                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Post post = new Post("thank", current_user, don.charity, don.user, don.amount, input.getText().toString(), 0);
                        mDb.getReference("user_posts").child(don.user).push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                Toast.makeText(((DonationViewHolder) holder).donationPostCard.getContext(), "User Thanked Successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();
            }
        });
    }

}
