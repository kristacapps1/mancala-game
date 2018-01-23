package kalah.game.manager;

import java.util.Vector;
import java.util.*;


public class ruleChecker {
    
    public ruleChecker() {}
    
    public boolean isValidMove( ArrayList<kalahHole> boardState, int holeNum, int playerNum, int holesPerSide) {
        // check empty house
        if(boardState.get(holeNum).numSeeds == 0) {
            return false;
        }
        //check player sides
        else if(playerNum == 1 && holeNum > holesPerSide  - 1 ) {
            //System.out.println("player one invalid move");
            return false;
        }
        else if((playerNum == 2) && ((holeNum > holesPerSide*2) || (holeNum < holesPerSide+1))) {
            //System.out.println("player two invalid move");
            return false;
        }
        return true;
    }
    
    public boolean checkEndGame(ArrayList<kalahHole> boardState, int holesPerSide) {
        boolean sideClearedPOne = true;
        boolean sideClearedPTwo = true;
        //check if player 1 side is clear
        for(int i = 0; i< holesPerSide;i++) {
            if(boardState.get(i).numSeeds != 0) {
                sideClearedPOne = false;
            }
        }
        //check if player 2 side is clear
        for(int i = holesPerSide+1; i< (holesPerSide*2)+1;i++) {
            if(boardState.get(i).numSeeds != 0) {
                sideClearedPTwo = false;
            }
        }
        if(sideClearedPOne || sideClearedPTwo) {
            return true;
        }
        else {
            return false;
        }
    }
}
