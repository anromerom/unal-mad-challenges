package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

                                // TODO: Set the diff level of mGame based on which item was selected.

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

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];

        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

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


        mGame = new TicTacToeGame();
        mHumanCount = 0;
        mComputerCount = 0;
        mTieCount = 0;

        startNewGame();
    }

    private void startNewGame(){

        mGame.clearBoard();
        mGameOver = false;

        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        mInfoTextView.setText(R.string.first_human);

    }

    public class ButtonClickListener implements View.OnClickListener{

        private int location;
        public ButtonClickListener(int location) {
            this.location = location;

        }

        @Override
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1)
                    {
                        mInfoTextView.setText(R.string.result_tie);

                        mTiesTextView.setText(getString(R.string.ties_count, mTieCount++));

                        mGameOver = true;
                    }
                else if (winner == 2)
                    {
                        mInfoTextView.setText(R.string.result_human_wins);
                        mHumanCount++;
                        mHumanTextView.setText(getString(R.string.human_count, mHumanCount++));

                        mGameOver = true;
                    }
                else
                    {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mComputerCount++;
                        mComputerTextView.setText(getString(R.string.android_count, mComputerCount++));

                        mGameOver = true;
                    }
            }
        }

    }

    private void setMove(char player, int move) {
        mGame.setMove(player, move);
        mBoardButtons[move].setEnabled(false);
        mBoardButtons[move].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[move].setTextColor(Color.rgb(0,200,0));
        else
            mBoardButtons[move].setTextColor(Color.rgb(200,0,0));

    }


}