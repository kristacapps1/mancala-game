package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import GUI.BoardDrawing;
import GUI.winnerWin;
import kalah.game.manager.Player;
import kalah.game.manager.boardManager;
import kalah.game.manager.mancalaAI;
import kalah.game.manager.ruleChecker;

public class networkPlayer extends Thread {
    networkPlayer opponent;
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    Player playerData;
    Player opponentData;
    boolean endGame = false;
    boardManager boardState;
    
    /*
     * Create thread to handle socket
     */
    public networkPlayer(Socket socket, Player playerData) {
        this.socket = socket;
        this.playerData = playerData;
        opponentData = new Player(0, false, 0);
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Player died); " + e);
        }
    }
    
    /*
     * Set opponent
     */
    public void setOpponent(networkPlayer opponent) {
        this.opponent = opponent;
    }
    
    /*
     * Run method
     */
    public void run() {
        try {
            boolean firstPlayer = false;
            // Get WELCOME
            String inMessage = input.readLine();
            System.out.println(inMessage);
            
            // Get game info
            inMessage = input.readLine();
            System.out.println(inMessage);
            String delims = "[ ]+";
            String[] infoTokens = inMessage.split(delims);
            
            // See if I am first player
            if (infoTokens[4].equals("F")) {
                playerData.setID(1);
                playerData.setOpponentID(2);
                opponentData.setID(2);
                opponentData.setOpponentID(1);
            } else {
                playerData.setID(2);
                playerData.setOpponentID(1);
                opponentData.setID(1);
                playerData.setOpponentID(2);
            }
            
            playerData.setOpponent(opponentData);
            opponentData.setOpponent(playerData);
            boardState = new boardManager(Integer.valueOf(infoTokens[1]), Integer.valueOf(infoTokens[2]), playerData,
                                          opponentData);
            boardState.setupBoard(Integer.valueOf(infoTokens[1]), Integer.valueOf(infoTokens[2]));
            
            BoardDrawing boardDisp = new BoardDrawing(boardState.holesPerSide, boardState);
            boardDisp.setTitle("Kalah : Player " + playerData.player_id);
            
            // Let Server know Info received
            output.println("READY");
            
            // if player one, I move first
            if (playerData.player_id == 1) {
                // retrieve first move from board
                boardState.setCurrentPlayer(playerData);
                if (playerData.is_AI) {
                    mancalaAI clientAI = new mancalaAI();
                    int tmpMove = clientAI.AIMove(boardState.boardHoles, boardState.currentPlayer.player_id,
                                                  boardState.currentPlayer.AIdifficulty);
                    boardState.setMove(tmpMove);
                } else {
                    while (boardState.waitingForMove) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                
                // Send my move to the server
                output.println(boardState.moveNum);
                boardState.doMove(boardState.moveNum, 1);
                boardState.setWaitingMove(true);
                boardDisp.reprintButtons(boardState, boardState.holesPerSide);
                boardState.setCurrentPlayer(boardState.currentPlayer.opponent);
            } else {
            	// Otherwise it's my opponents turn
                boardState.setCurrentPlayer(playerData.opponent);
            }
            
            while (true) {
                // wait for move from server
                try {
                    inMessage = input.readLine();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Thread.sleep(5 * 200);
                System.out.println("OK");
                // If message is a move
                if (Character.isDigit(inMessage.charAt(0))) {
                    // Update board
                    boardState.doMove(Integer.valueOf(inMessage), boardState.currentPlayer.player_id);
                    boardDisp.reprintButtons(boardState, boardState.holesPerSide);
                    boardState.setCurrentPlayer(playerData);
                    boolean endOfIn = isNoInput(input);
                    if (endOfIn) {
                        inMessage = input.readLine();
                        boardState.doMove(Integer.valueOf(inMessage), boardState.currentPlayer.player_id);
                        boardDisp.reprintButtons(boardState, boardState.holesPerSide);
                        boardState.setCurrentPlayer(playerData);
                    }
                    else {
                        endGame = handleStringMsg(inMessage, boardState, boardDisp);
                        if (endGame) {
                            break;
                        }
                    }
                    // Otherwise it is string message
                } else {
                    endGame = handleStringMsg(inMessage, boardState, boardDisp);
                    if (endGame) {
                        break;
                    }
                }
                
                // Get move from client's gui or AI
                if (playerData.is_AI) {
                    mancalaAI clientAI = new mancalaAI();
                    int tmpMove = clientAI.AIMove(boardState.boardHoles, boardState.currentPlayer.player_id,
                                                  boardState.currentPlayer.AIdifficulty);
                    boardState.setMove(tmpMove);
                } else {
                    while (boardState.waitingForMove) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                
                // Send move back to server and update Client's board
                output.println(boardState.moveNum);
                boardState.doMove(boardState.moveNum, boardState.currentPlayer.player_id);
                boardDisp.reprintButtons(boardState, boardState.holesPerSide);
                boardState.setWaitingMove(true);
                boardState.setCurrentPlayer(playerData.opponent);
            }
            
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
    
    public boolean handleClientMove(Player playerData, BoardDrawing boardGUI) {
        endGame = false;
        boolean repeatTurn = false;
        ruleChecker checksMoves = new ruleChecker();
        
        mancalaAI playerAI = new mancalaAI();
        
        // Wait for player to Move in the gui
        // TODO: add timer
        while (boardState.waitingForMove) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // Set that we are now waiting for a move from the gui again
        boardState.setWaitingMove(true);
        
        /*
         * Do move and determine Does player get a free turn
         */
        repeatTurn = boardState.doMove(boardState.moveNum, boardState.currentPlayer.player_id);
        boardGUI.reprintButtons(boardState, boardState.holesPerSide);
        
        // Check if player gets another turn
        if (repeatTurn) {
            handleClientMove(playerData, boardGUI);
        }
        // Return that the game is not over
        return false;
    }
    
    public boolean handleStringMsg(String msg, boardManager boardState, BoardDrawing boardLayout) {
        /*
         * OPTIONS: TIMER LOSER WINNER TIE P
         */
    	if (msg.equals("TIMER")) {
    		System.out.println(msg);
    		winnerWin whoWon = new winnerWin(playerData.opponent.player_id);
            return true;
        }
        if (msg.equals("LOSER")) {
            handleEndGame(playerData.opponent.player_id, boardLayout);
            return true;
        }
        if (msg.equals("WINNER")) {
            handleEndGame(playerData.player_id, boardLayout);
            return true;
        }
        if (msg.equals("TIE")) {
            System.out.println(msg);
            handleEndGame(-1, boardLayout);
            return true;
        } else {
            return false;
        }
    }
    
    public void handleEndGame(int winner, BoardDrawing boardLayout) {
        /*
         * Determine winner and create a little display to show user the result
         */
        // Gather player 1 houses
        for (int indx = 0; indx < boardState.holesPerSide; indx++) {
            boardState.boardHoles.get(boardState.holesPerSide)
            .setNumSeeds(boardState.boardHoles.get(boardState.holesPerSide).getNumSeeds()
                         + boardState.boardHoles.get(indx).getNumSeeds());
            boardState.boardHoles.get(indx).setNumSeeds(0);
        }
        // Gather player 2 houses
        for (int indx = boardState.holesPerSide + 1; indx < (boardState.holesPerSide * 2) + 1; indx++) {
            boardState.boardHoles.get((boardState.holesPerSide * 2 + 1))
            .setNumSeeds(boardState.boardHoles.get((boardState.holesPerSide * 2) + 1).getNumSeeds()
                         + boardState.boardHoles.get(indx).getNumSeeds());
            boardState.boardHoles.get(indx).setNumSeeds(0);
        }
        // update display
        boardLayout.reprintButtons(boardState, boardState.holesPerSide);
        
        // Display winner or loser
        if (winner == playerData.player_id) {
            System.out.println("You win!");
            winnerWin whoWon = new winnerWin(playerData.player_id);
        } else if (winner == playerData.opponent.player_id) {
            System.out.println("You lose!");
            winnerWin whoWon = new winnerWin(playerData.opponent.player_id);
        } else {
            System.out.println("Its a tie!");
            winnerWin whoWon = new winnerWin(-1);
        }
    }
    
    public boolean isNoInput(BufferedReader br)
    {
        boolean result = true;
        
        try
        {
            result = br.ready();
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        return result;
    }
}

