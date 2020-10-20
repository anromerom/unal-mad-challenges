package co.edu.unal.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.edu.unal.tictactoe.Multiplayer.Match;

public class MainActivity extends AppCompatActivity {

    private AbstractTicTacToe mGame;
    private boolean mGameOver;
    private int mHumanCount;
    private int mComputerCount;
    private int mTieCount;

    private String MODE;
    private String ROLE;
    private String REF_ID;

    private TextView mInfoTextView;
    private TextView mHumanTextView;
    private TextView mComputerTextView;
    private TextView mTiesTextView;

    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    MediaPlayer mWinMediaPlayer;
    MediaPlayer mLoseMediaPlayer;
    MediaPlayer mTieMediaPlayer;

    private boolean humanTurn;
    private boolean mSoundOn;

    SharedPreferences mPrefs;

    Match match;

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_move);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer_move);
        mWinMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_win);
        mLoseMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_lose);
        mTieMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_tie);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mWinMediaPlayer.release();
        mLoseMediaPlayer.release();
        mTieMediaPlayer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(R.string.new_game);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED) {

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if(mGame instanceof TicTacToeSinglePlayer){
                TicTacToeSinglePlayer mGameSingle = (TicTacToeSinglePlayer) mGame;

                if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                    mGameSingle.setDifficultyLevel(TicTacToeSinglePlayer.DifficultyLevel.Easy);
                else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                    mGameSingle.setDifficultyLevel(TicTacToeSinglePlayer.DifficultyLevel.Harder);
                else
                    mGameSingle.setDifficultyLevel(TicTacToeSinglePlayer.DifficultyLevel.Expert);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings_menu:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_dialog, null);

        switch(id) {
                      case DIALOG_QUIT_ID:

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case DIALOG_ABOUT_ID:



                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();

                break;


        }

        return dialog;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MODE = getIntent().getStringExtra("mode");

        if(MODE == null || MODE.equals("SINGLEPLAYER")){
            mGame = new TicTacToeMultiplayer();
        }else if (MODE.equals("MULTIPLAYER")){
            mGame = new TicTacToeMultiplayer();
            ROLE = getIntent().getStringExtra("role");
            REF_ID = getIntent().getStringExtra("match_id");
            DatabaseReference matchRef =  FirebaseDatabase.getInstance().getReference("matches").child(REF_ID);
            matchRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    match = snapshot.getValue(Match.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    matchRef.removeValue();
                    Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                    startActivity(intent);
                }
            });

        }

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mHumanTextView = (TextView) findViewById(R.id.humanwins);
        mHumanTextView.setText(R.string.human_count);
        mHumanTextView.setText(getString(R.string.human_count, 0));

        mComputerTextView = (TextView) findViewById(R.id.computerwins);
        mComputerTextView.setText(R.string.android_count);
        mComputerTextView.setText(getString(R.string.android_count, 0));

        mTiesTextView = (TextView) findViewById(R.id.ties);
        mTiesTextView.setText(R.string.ties_count);
        mTiesTextView.setText(getString(R.string.ties_count, 0));


        mHumanCount = 0;
        mComputerCount = 0;
        mTieCount = 0;

        startNewGame();

        // Preferencias
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);


        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if(mGame instanceof TicTacToeSinglePlayer) {
            TicTacToeSinglePlayer mGameSingle = (TicTacToeSinglePlayer) mGame;

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGameSingle.setDifficultyLevel(TicTacToeSinglePlayer.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGameSingle.setDifficultyLevel(TicTacToeSinglePlayer.DifficultyLevel.Harder);
            else
                mGameSingle.setDifficultyLevel(TicTacToeSinglePlayer.DifficultyLevel.Expert);
        }

    }

    private void startNewGame(){

        mGame.clearBoard();
        mBoardView.invalidate();
        if(ROLE.equals("OWNER")){
            humanTurn = false;
            mInfoTextView.setText(R.string.turn_computer);

        } else {
            humanTurn = true;
            mInfoTextView.setText(R.string.first_human);
        }
        mGameOver = false;

        if (MODE.equals("MULTIPLAYER")){

            DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("matches").child(REF_ID);

            matchRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Match match = snapshot.getValue(Match.class);

                        if(match == null) return;

                        if((ROLE.equals("OWNER") && match.isOwnerPlaying()) || (ROLE.equals("GUEST") && !match.isOwnerPlaying())){
                            if( match.getLastmove() >= 0)
                            {
                                setMove(TicTacToeSinglePlayer.COMPUTER_PLAYER, match.getLastmove());
                                checkForWinner();
                                humanTurn = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        matchRef.removeValue();
                        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                        startActivity(intent);
                    }
                }
            );
        }

    }

    private boolean setMove(char player, int move) {

        if (mGame.setMove(player, move)) {
            mBoardView.invalidate();   // Redraw the board
            if(humanTurn){
                if(mSoundOn) mHumanMediaPlayer.start();
                match.setLastmove(move);
                match.setTurn(match.getTurn() + 1);
                match.setOwnerPlaying(!match.isOwnerPlaying());
                DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("matches").child(REF_ID);
                matchRef.setValue(match);
            }
            else
                if(mSoundOn) mComputerMediaPlayer.start();
            checkForWinner();
            return true;
        }
        return false;
    }


    private void gameOver(){
        Handler handler = new Handler();

        Toast toast = Toast.makeText(getApplicationContext(), "Fin del juego", Toast.LENGTH_LONG);

        if( MODE.equals("SINGLEPLAYER")){
            mGameOver = true;
            handler.postDelayed(() -> startNewGame(), 3000);
        } else if (MODE.equals("MULTIPLAYER")){
            handler.postDelayed(() -> MainActivity.this.finish(), 5000);

        }


    }

    private void checkForWinner() {

        int winner = mGame.checkForWinner();



        if (winner == 0)
            mInfoTextView.setText(R.string.turn_human);
        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);
            mTiesTextView.setText(getString(R.string.ties_count, mTieCount++));
            if(mSoundOn) mTieMediaPlayer.start();
            gameOver();
        } else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins);
            mHumanCount++;
            mHumanTextView.setText(getString(R.string.human_count, mHumanCount++));
            if(mSoundOn) mWinMediaPlayer.start();

            String defaultMessage = getResources().getString(R.string.result_human_wins);
            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));

            gameOver();
        } else {
            mInfoTextView.setText(R.string.result_computer_wins);
            mComputerCount++;
            mComputerTextView.setText(getString(R.string.android_count, mComputerCount++));
            if(mSoundOn) mLoseMediaPlayer.start();
            gameOver();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("matches").child(REF_ID);
        mRef.removeValue();

    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int location = row * 3 + col;

            if (!mGameOver && humanTurn && setMove(TicTacToeSinglePlayer.HUMAN_PLAYER, location))	{
                setMove(TicTacToeSinglePlayer.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    humanTurn = false;
                    waitForOpponent();
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };



    void waitForOpponent( ){
        if(MODE.equals("SINGLEPLAYER")){

            TicTacToeSinglePlayer mGameSingle = (TicTacToeSinglePlayer) mGame;
            Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            int move = mGameSingle.getOpponentMove();
                            setMove(TicTacToeSinglePlayer.COMPUTER_PLAYER, move);
                            checkForWinner();
                            humanTurn = true;
                        }, 2000);
        }
    }
}