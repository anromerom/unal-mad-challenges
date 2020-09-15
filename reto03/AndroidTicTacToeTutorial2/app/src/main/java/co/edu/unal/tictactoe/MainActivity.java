package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private boolean mGameOver;
    private int mHumanCount;
    private int mComputerCount;
    private int mTieCount;

    private TextView mInfoTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.new_game);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
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
                        mTieCount++;
                        mGameOver = true;
                    }
                else if (winner == 2)
                    {
                        mInfoTextView.setText(R.string.result_human_wins);
                        mHumanCount++;
                        mGameOver = true;
                    }
                else
                    {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mComputerCount++;
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