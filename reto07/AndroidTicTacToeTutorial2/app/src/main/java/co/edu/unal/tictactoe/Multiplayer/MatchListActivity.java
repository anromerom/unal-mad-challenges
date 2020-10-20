package co.edu.unal.tictactoe.Multiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import co.edu.unal.tictactoe.MainActivity;
import co.edu.unal.tictactoe.R;
import kotlin.random.Random;

public class MatchListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String mLocalUserName;

    List<Match> matches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);


        Intent intent = getIntent();
        mLocalUserName = intent.getStringExtra("username");

        matches = new LinkedList<>();


        MatchAdapter adapter = new MatchAdapter(this, matches, mLocalUserName);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("matches");
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matches.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Match match = child.getValue(Match.class);
                    matches.add(match);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onCreateMatch(View view) {

        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("matches");


        DatabaseReference newMatchRef  = matchRef.push();
        String refID = newMatchRef.getKey();

        Match match = new Match(mLocalUserName, "", false, 0, -1, true, refID);

        newMatchRef.setValue(match);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", "MULTIPLAYER");
        intent.putExtra("role", "OWNER");
        intent.putExtra("match_id", refID);
        startActivity(intent);

    }
}