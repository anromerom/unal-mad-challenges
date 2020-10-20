package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import co.edu.unal.tictactoe.Multiplayer.MatchListActivity;

public class SelectActivity extends AppCompatActivity {


    EditText mUserNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mUserNameEditText = findViewById(R.id.editTextUserName);

    }

    public void onSinglePlayerClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", "SINGLEPLAYER");
        intent.putExtra("match_id", "null");
        startActivity(intent);
    }


    public void onMultiPlayerClick(View view) {
        Intent intent = new Intent(this, MatchListActivity.class);
        intent.putExtra("username", mUserNameEditText.getText().toString());
        startActivity(intent);
    }
}