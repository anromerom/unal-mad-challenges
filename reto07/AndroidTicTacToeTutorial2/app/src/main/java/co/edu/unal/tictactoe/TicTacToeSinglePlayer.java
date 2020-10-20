package co.edu.unal.tictactoe;

import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.Random;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O.
 */

public class TicTacToeSinglePlayer extends AbstractTicTacToe{


    //Difficulty level
    public enum DifficultyLevel {Easy, Harder, Expert};
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;


    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel mDifficultyLevel) {
        this.mDifficultyLevel = mDifficultyLevel;
    }

    public TicTacToeSinglePlayer() {

        // Seed the random number generator
        mRand = new Random();
    }



    private void displayBoard()	{
        System.out.println();
        System.out.println(mBoard[0] + " | " + mBoard[1] + " | " + mBoard[2]);
        System.out.println("-----------");
        System.out.println(mBoard[3] + " | " + mBoard[4] + " | " + mBoard[5]);
        System.out.println("-----------");
        System.out.println(mBoard[6] + " | " + mBoard[7] + " | " + mBoard[8]);
        System.out.println();
    }

    public int getOpponentMove()
    {
        // First see if there's a move O can make to win

        int move = -1;

        if(mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder){
            move = getWinningMove();
            if(move == -1){
             move = getRandomMove();
            }
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert){
            move = getWinningMove();
            if (move == -1){
                move = getBlockingMove();
            }
            if(move == -1){
                move = getRandomMove();
            }
        }
        return move;
    }


    private int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT){
                //VERTICAL
                if (mBoard[ (i + N) % BOARD_SIZE ] == COMPUTER_PLAYER
                        && mBoard[ (i + 2 * N) % BOARD_SIZE ] == COMPUTER_PLAYER) return i;

                    //HORIZONTAL
                else if (mBoard[((i + 1) % N)+ i - (i % N)] == COMPUTER_PLAYER
                        && mBoard[((i + 2) % N)+ i - (i % N)] == COMPUTER_PLAYER) return i;

                    //DIAGONAL \
                else if( (i  == 0 && mBoard[4] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER) ||
                        (i  == 4 && mBoard[0] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER) ||
                        (i  == 8 && mBoard[4] == COMPUTER_PLAYER && mBoard[0] == COMPUTER_PLAYER)) return i;

                    //DIAGONAL /
                else if( (i  == 2 && mBoard[4] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER) ||
                        (i  == 4 && mBoard[2] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER) ||
                        (i  == 6 && mBoard[4] == COMPUTER_PLAYER && mBoard[2] == COMPUTER_PLAYER)) return i;
            }
        }
        return -1;
    }


    private int getBlockingMove(){
        // See if there's a move O can make to block X from winning
        for (int i = 0; i < BOARD_SIZE; i++) {

            //VERTICAL
            if (mBoard[i] == OPEN_SPOT) {
                if (mBoard[(i + N) % BOARD_SIZE] == HUMAN_PLAYER
                        && mBoard[(i + 2 * N) % BOARD_SIZE] == HUMAN_PLAYER) return i;

                    //HORIZONTAL
                else if (mBoard[((i + 1) % N) + i - (i % N)] == HUMAN_PLAYER
                        && mBoard[((i + 2) % N) + i - (i % N)] == HUMAN_PLAYER) return i;

                    //DIAGONAL \
                else if ((i == 0 && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER) ||
                        (i == 4 && mBoard[0] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER) ||
                        (i == 8 && mBoard[4] == HUMAN_PLAYER && mBoard[0] == HUMAN_PLAYER))
                    return i;

                    //DIAGONAL /
                else if ((i == 2 && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER) ||
                        (i == 4 && mBoard[2] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER) ||
                        (i == 6 && mBoard[4] == HUMAN_PLAYER && mBoard[2] == HUMAN_PLAYER))
                    return i;
            }
        }
        return -1;
    }

    private int getRandomMove() {
        int move = -1;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] != OPEN_SPOT);

        return move;

    }

    public boolean setMove(char player, int location)
    {
        if(mBoard[location] == OPEN_SPOT)
        {   mBoard[location] = player;
            return true;
        }
        return false;
    }



}