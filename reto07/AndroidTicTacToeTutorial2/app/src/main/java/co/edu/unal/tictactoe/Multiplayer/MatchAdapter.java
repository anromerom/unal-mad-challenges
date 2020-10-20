package co.edu.unal.tictactoe.Multiplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import co.edu.unal.tictactoe.MainActivity;
import co.edu.unal.tictactoe.R;

class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final String username;
    private final Context context;
    List<Match> matches;

    LayoutInflater mInflater;

    public MatchAdapter(Context context, List<Match> matches, String username) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.matches = matches;
        this.username = username;
    }


    @NonNull
    @Override
    public MatchAdapter.MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_match_item, parent, false);
        return new MatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.MatchViewHolder holder, int position) {

        if (matches == null) return;

        Match match = matches.get(position);
        holder.mUserNameText.setText(match.getOwner());
        holder.mPlayButton.setEnabled(match.isAvailable());


        holder.mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference("matches").child(match.getRefID());
                match.setAvailable(false);
                match.setGuest(username);

                gameRef.setValue(match);

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("mode", "MULTIPLAYER");
                intent.putExtra("role", "GUEST");
                intent.putExtra("match_id", match.getRefID());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder {

        private final TextView mUserNameText;
        private final FloatingActionButton mPlayButton;


        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserNameText = itemView.findViewById(R.id.matchUserName);
            mPlayButton = itemView.findViewById(R.id.matchPlayButton);
        }
    }
}
