package kalah.game.manager;

import java.util.*;
import java.util.Vector;

// Keep track of game score
public class scoreTracker {
    public int scorePlayerOne;
    public int scorePlayerTwo;
    
    public scoreTracker() {
        scorePlayerOne = 0;
        scorePlayerTwo = 0;
    }
    
    public void resetScores() {
        scorePlayerOne = 0;
        scorePlayerTwo = 0;
    }
    
    public boolean assessScorePerMove( ArrayList<kalahHole> boardState, int holeNum, int playerNum, int seedCount, int holesPerSide) {
        //TODO
        //add score IF goes in kalah
        if((boardState.get(holeNum).isKalah) &&
           (boardState.get(holeNum).playerSideNum == playerNum)) {
            increaseScore(playerNum,1);
            //IF last seed in players kalah move again using bool flag
            if(seedCount == 0) {
                return true;
            }
        }
        //IF last seed in players empty house do capture
        if((boardState.get(holeNum).getNumSeeds() == 1)
           && (seedCount==0) && (boardState.get(holeNum).playerSideNum == playerNum)) {
            // Get opposite house index
            int oppositeIndex = ((holesPerSide*2)+1) - (holeNum+1);
            if(boardState.get(oppositeIndex).getNumSeeds()>0) {
                //update score and kalah sums
                increaseScore(playerNum, 1+ boardState.get(oppositeIndex).numSeeds);
                if(playerNum == 1) {
                    boardState.get(holesPerSide).setNumSeeds(boardState.get(holesPerSide).numSeeds + 1 + boardState.get(oppositeIndex).numSeeds);
                }
                if(playerNum == 2) {
                    boardState.get((holesPerSide*2)+1).setNumSeeds(boardState.get((holesPerSide*2)+1).numSeeds + 1 + boardState.get(oppositeIndex).numSeeds);
                }
                // empty opposite houses
                boardState.get(holeNum).setNumSeeds(0);
                boardState.get(oppositeIndex).setNumSeeds(0);
            }
            
        }
        return false;
    }
    
    public void increaseScore(int playerNum,int scoreNum) {
        if(playerNum == 1) {
            scorePlayerOne += scoreNum;
        }
        if(playerNum == 2) {
            scorePlayerTwo += scoreNum;
        }
    }
}
