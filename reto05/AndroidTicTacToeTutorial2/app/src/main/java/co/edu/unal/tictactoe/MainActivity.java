package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private TicTacToeGame mGame;
    private Button[] mBoardButtons;
    private boolean mGameOver;
    private int mHumanCount;
    private int mComputerCount;
    private int mTieCount;

    private TextView mInfoTextView;
    private TextView mHumanTextView;
    private TextView mComputerTextView;
    private TextView mTiesTextView;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    MediaPlayer mWinMediaPlayer;
    MediaPlayer mLoseMediaPlayer;
    MediaPlayer mTieMediaPlayer;

    private boolean humanTurn;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
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
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.

                int selected = 2;

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                TicTacToeGame.DifficultyLevel level = TicTacToeGame.DifficultyLevel.Expert;;
                                switch (item){
                                    case 0:
                                        level = TicTacToeGame.DifficultyLevel.Easy;
                                        break;
                                    case 1:
                                        level = TicTacToeGame.DifficultyLevel.Harder;
                                        break;
                                    case 2:
                                        level = TicTacToeGame.DifficultyLevel.Expert;
                                        break;
                                }

                                mGame.setDifficultyLevel(level);

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;

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

        mGame = new TicTacToeGame();
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
    }

    private void startNewGame(){

        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver = false;
        humanTurn = true;
        mInfoTextView.setText(R.string.first_human);

    }



    private boolean setMove(char player, int move) {

        if (mGame.setMove(player, move)) {
            mBoardView.invalidate();   // Redraw the board
            if(humanTurn)
                mHumanMediaPlayer.start();
            else
                mComputerMediaPlayer.start();
            checkForWinner();
            return true;
        }
        return false;
    }


    private void checkForWinner() {

        int winner = mGame.checkForWinner();
        Handler handler = new Handler();


        if (winner == 0)
            mInfoTextView.setText(R.string.turn_human);
        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);

            mTiesTextView.setText(getString(R.string.ties_count, mTieCount++));
            mTieMediaPlayer.start();
            mGameOver = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNewGame();
                }
            }, 3000);
        } else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins);
            mHumanCount++;
            mHumanTextView.setText(getString(R.string.human_count, mHumanCount++));
            mWinMediaPlayer.start();
            mGameOver = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNewGame();
                }
            }, 3000);
        } else {
            mInfoTextView.setText(R.string.result_computer_wins);
            mComputerCount++;
            mComputerTextView.setText(getString(R.string.android_count, mComputerCount++));
            mLoseMediaPlayer.start();
            mGameOver = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNewGame();
                }
            }, 3000);
        }
    }


        private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int location = row * 3 + col;




            if (!mGameOver && humanTurn && setMove(TicTacToeGame.HUMAN_PLAYER, location))	{
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    humanTurn = false;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            checkForWinner();
                            humanTurn = true;
                        }
                    }, 2000);

                }

            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };



}

