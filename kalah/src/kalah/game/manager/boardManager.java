package kalah.game.manager;

import java.util.Scanner;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import GUI.BoardDrawing;
import GUI.winnerWin;

import javax.swing.GroupLayout.Alignment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

// Overall Board Manager
public class boardManager{
    public scoreTracker trackerOne;
    public static int holesPerSide;
    public int startingSeedNum;
    public ArrayList<kalahHole> boardHoles;
    public ruleChecker checksMoves;
    public Player playerOne;
    public Player playerTwo;
    public Player currentPlayer;
    public volatile boolean waitingForMove = true;
    public volatile boolean startGame = false;
    public volatile int moveNum;
    public BoardDrawing boardLayout;
    public boolean endGame = false;
    public int winner = 0;
    public int timeoutVal;
    //int i = 0;
    
    //static final int MOVE_NUM = holesPerSide;
    
    int choice;
    
    //checksMoves = new ruleChecker();
    final int FRAME_HEIGHT = 500;
    final int FRAME_WIDTH = 900;
    final int outerPadding = 15, innerPadding = 20;
    final int pitWidth = 90, pitHeight = 90;
    final int storeWidth = 90, storeHeight = 200;
    final int fixY = 50;
    
    JPanel ContentPane;
    
    
    //mancalaAI playerAI;
    
    /* Vector Indices for Board Setup (6,4)
     * |    | 12 | 11 | 10 | 9 | 8  | 7  |   |
     * | 13 |----------------------------| 6 |
     * |    | 0  | 1  | 2  | 3  | 4  | 5 |   |
     * Player 1 Holes: 0-6 with 6 as kalah
     * Player 2 Holes: 7-13 with 13 as kalah
     * ((holesPerSide*2)+1)
     */
    
    
    public boardManager(int holesPerSide, int startingSeedNum, Player playerOne, Player playerTwo) {
        this.startingSeedNum = startingSeedNum;
        this.holesPerSide = holesPerSide;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        //Player currentPlayer;
        trackerOne =  new scoreTracker();
        boardHoles = new ArrayList<kalahHole>(14);
        checksMoves = new ruleChecker();
        
        // Initialize Board Setup
    }
    public void setupBoard(int newHolesPerSide, int newSeedNum) {
        setHolesPerSide(newHolesPerSide);
        for(int i = 0; i<holesPerSide ; i++) {
            kalahHole newKalahHole = new kalahHole(startingSeedNum, 1, false);
            boardHoles.add(newKalahHole);
        }
        kalahHole newKalahP2 = new kalahHole(0, 1, true);
        boardHoles.add(newKalahP2);
        for(int i = 0; i<holesPerSide ; i++) {
            kalahHole newKalahHole = new kalahHole(startingSeedNum, 2, false);
            boardHoles.add(newKalahHole);
        }
        kalahHole kalahP2 = new kalahHole(0, 2, true);
        boardHoles.add(kalahP2);
        //BoardDrawing guiBoard = new BoardDrawing(holesPerSide,this);
    }
    
    public boardManager() {
        // TODO Auto-generated constructor stub
    }
    
    public void setHolesPerSide(int numHoles) {
        holesPerSide = numHoles;
    }
    public void setStartingSeedNum(int numSeeds) {
        startingSeedNum = numSeeds;
    }
    public int getHolesPerSide() {
        return holesPerSide;
    }
    public int getStartingSeedNum() {
        return startingSeedNum;
    }
    public void setCurrentPlayer(Player newCurPlayer) {
        currentPlayer = newCurPlayer;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public void setMove(int moveNum) {
        this.moveNum = moveNum;
    }
    public void setStartGame(boolean startingGame) {
        startGame = startingGame;
    }
    public void setWaitingMove(boolean waitForMove) {
        waitingForMove = waitForMove;
    }
    public void setTimeoutVal(int timeVal) {
        timeoutVal = timeVal;
    }
    // Determine player and if AI and call doMove
    public boolean handleMove(Player playerData) {
        //printBoardState();
        endGame = false;
        boolean repeatTurn = false;
        //int moveNum = holesPerSide;
        
        mancalaAI playerAI = new mancalaAI();
        
        // Determine human or AI player
        if(playerData.getAI()) {
            // Add artificial 5 second pause for human users
            try {
                Thread.sleep(5*200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get move number from the AI
            moveNum = playerAI.AIMove(boardHoles,currentPlayer.player_id, currentPlayer.AIdifficulty);
        }
        else {
            //Wait for player to Move in the gui
            //TODO: add timer
            while(waitingForMove) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            //Set that we are now waiting for a move from the gui again
            setWaitingMove(true);
        }
        
        /* Do move and determine
         * Does player get a free turn
         */
        repeatTurn = doMove(moveNum,currentPlayer.player_id);
        boardLayout.reprintButtons(this,holesPerSide);
        
        // Check if the game is over
        endGame = checksMoves.checkEndGame(boardHoles, holesPerSide);
        if(endGame) {
            // call function to clean up the board sides
            handleEndGame();
            // return that yes the game is over
            return endGame;
        }
        
        // Check if player gets another turn
        if(repeatTurn) {
            handleMove(playerData);
        }
        // Return that the game is not over
        return false;
    }
    
    // Do move on board and update values
    public boolean doMove(int holeNumber, int playerNum) {
        int index = 1;
        boolean moveAgain = false;
        
        //Preserve data for end set
        int tempNumSeeds = boardHoles.get(holeNumber).numSeeds;
        int tmpHoleNumber = holeNumber;
        
        if(checksMoves.isValidMove(boardHoles, holeNumber, playerNum,holesPerSide)) {
            for(int i = tempNumSeeds; i != 0;i--) {
                // If we are at player two kalah, wrap index around to beginning
                if((holeNumber + index) > ((holesPerSide*2)+1)) {
                    holeNumber = 0;
                    index = 0;
                }
                //Check for opponent kalah
                if((playerNum == 1)&&((holeNumber + index) == ((holesPerSide*2)+1))) {
                    //Don't add seed to opponents kalah
                    i++;
                }
                else if((playerNum == 2)&&((holeNumber + index) == (holesPerSide))) {
                    //Don't add seed to opponents kalah
                    i++;
                }
                else {
                    boardHoles.get(holeNumber + index).setNumSeeds(boardHoles.get(holeNumber + index).getNumSeeds()+1);
                    moveAgain = trackerOne.assessScorePerMove(boardHoles, holeNumber+index, playerNum,i-1,holesPerSide);
                }
                index++;
            }
            boardHoles.get(tmpHoleNumber).numSeeds -= tempNumSeeds;
        }
        return moveAgain;
    }
    
    public ArrayList<kalahHole> getHoles(){
        return boardHoles;
    }
    
    /*
     * Function to print the board in the terminal for debug
     * Or server
     */
    public void printBoardState() {
        String boardHalf="";
        for(int i = (holesPerSide*2)+1; i>holesPerSide;i--) {
            boardHalf = boardHalf + "| " + boardHoles.get(i).numSeeds;
        }
        System.out.println(boardHalf);
        boardHalf = "   ";
        for(int i = 0; i<=(holesPerSide);i++) {
            boardHalf = boardHalf + "| " + boardHoles.get(i).numSeeds;
        }
        System.out.println(boardHalf);
    }
    
    public void handleEndGame() {
        //Gather all remaining seeds to kalahs and set the houses to 0
        //Gather player 1 houses
        for(int indx = 0; indx < holesPerSide; indx++) {
            boardHoles.get(holesPerSide).setNumSeeds(boardHoles.get(holesPerSide).getNumSeeds()
                                                     +boardHoles.get(indx).getNumSeeds());
            boardHoles.get(indx).setNumSeeds(0);
        }
        // Gather player 2 houses
        for(int indx = holesPerSide+1; indx < (holesPerSide*2)+1; indx++) {
            boardHoles.get((holesPerSide*2+1)).setNumSeeds(boardHoles.get((holesPerSide*2)+1).getNumSeeds()
                                                           +boardHoles.get(indx).getNumSeeds());
            boardHoles.get(indx).setNumSeeds(0);
        }
        //update display
        boardLayout.reprintButtons(this, holesPerSide);
        
        /*
         * Determine winner and create a little display to show user the result
         */
        if(boardHoles.get(holesPerSide).numSeeds
           > boardHoles.get((holesPerSide*2)+1).numSeeds) {
            winnerWin whoWon = new winnerWin(1);
        }
        else if(boardHoles.get(holesPerSide).numSeeds
                < boardHoles.get((holesPerSide*2)+1).numSeeds) {
            winnerWin whoWon = new winnerWin(2);
        }
        else {
            winnerWin whoWon = new winnerWin(-1);
        }
    }
}
