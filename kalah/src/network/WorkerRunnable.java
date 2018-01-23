package network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import GUI.BoardDrawing;
import GUI.serverMenu;
import GUI.winnerWin;
import kalah.game.manager.Player;
import kalah.game.manager.boardManager;
import kalah.game.manager.mancalaAI;
import kalah.game.manager.ruleChecker;

import java.io.*;

/**
 
 */
public class WorkerRunnable implements Runnable {

	protected Socket clientSocket1 = null;
	protected Socket clientSocket2 = null;
	protected String serverText = null;
	boolean isAI = false;
	int difficulty = 0;
	boolean beginAI = false;

	public WorkerRunnable(Socket clientSocket1, Socket clientSocket2, String serverText, boolean isAI, int difficulty) {
		this.clientSocket1 = clientSocket1;
		this.clientSocket2 = clientSocket2;
		this.serverText = serverText;
		this.isAI = isAI;
		this.difficulty = difficulty;
	}

	public void run() {
		try {
			// IF one client, only wait for one client
			// IF two AI, just start two AI
			// InputStream input = clientSocket.getInputStream();
			// clientSocket1.setSoTimeout(5*1000);
			BufferedReader input1;
			BufferedReader input2 = null;
			PrintWriter output1;
			PrintWriter output2 = null;
			long time = System.currentTimeMillis();
			boolean endGame = false;
			Player playerOne = new Player(1, false, 0);
			Player playerTwo;
			// Get starting configuration
			if (!isAI) {
				playerTwo = new Player(2, false, 0);
			} else {
				playerTwo = new Player(2, true, difficulty);
				playerTwo.setDifficulty(difficulty);
			}
			boardManager boardState = new boardManager(0, 0, playerOne, playerTwo);
			serverMenu chooseOptions = new serverMenu(boardState);
			chooseOptions.setVisible(true);

			while (!boardState.startGame) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clientSocket1.setSoTimeout(boardState.timeoutVal);
			input1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
			output1 = new PrintWriter(clientSocket1.getOutputStream(), true);
			if (!isAI) {
				clientSocket2.setSoTimeout(boardState.timeoutVal);
				input2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
				output2 = new PrintWriter(clientSocket2.getOutputStream(), true);
				playerTwo = new Player(2, false, 0);
				networkPlayer playerTwoNet = new networkPlayer(clientSocket2, playerTwo);
			}
			Player currentPlayer = playerTwo;
			networkPlayer playerOneNet = new networkPlayer(clientSocket1, playerOne);
			// networkPlayer playerTwoNet = new networkPlayer(clientSocket2,playerTwo);
			playerOne.setOpponent(playerTwo);
			playerTwo.setOpponent(playerOne);
			ruleChecker checksMoves = new ruleChecker();
			String msg = "WELCOME";

			output1.println(msg);
			if (!isAI) {
				output2.println(msg);
			}

			boardState.setCurrentPlayer(playerOne);
			// broadcast starting configuration to clients
			msg = "INFO " + boardState.holesPerSide + " " + boardState.startingSeedNum + " " + boardState.timeoutVal
					+ " F S";
			output1.println(msg);
			if (!isAI) {
				msg = "INFO " + boardState.holesPerSide + " " + boardState.startingSeedNum + " " + boardState.timeoutVal
						+ " S S";
				output2.println(msg);
			}
			// boardState.setupBoard(boardState.holesPerSide, boardState.startingSeedNum);

			while (true) {
				// runProgram progRunner = new runProgram();
				try {
					currentPlayer = currentPlayer.opponent;
					boardState.setCurrentPlayer(currentPlayer);
					if (currentPlayer == playerOne) {
						msg = input1.readLine();
						if (Character.isDigit(msg.charAt(0))) {
							// Do Move on server board
							boardState.doMove(Integer.valueOf(msg), 1);
							endGame = checksMoves.checkEndGame(boardState.boardHoles, boardState.holesPerSide);
							if (endGame) {
								break;
							}

							// Send move to other client
							if (!isAI) {
								output2.println(msg);
							}
							beginAI = true;
						} else {
							System.out.println(msg);
						}
					} else {
						if (isAI && beginAI) {
							// Get move from AI
							mancalaAI clientAI = new mancalaAI();
							int tmpMove = clientAI.AIMove(boardState.boardHoles, 2, difficulty);
							boardState.setMove(tmpMove);
							boardState.doMove(tmpMove, 2);
							endGame = checksMoves.checkEndGame(boardState.boardHoles, boardState.holesPerSide);
							if (endGame) {
								break;
							}
							// send move to other client
							output1.println(tmpMove);

						} else if (!isAI) {
							msg = input2.readLine();
							if (Character.isDigit(msg.charAt(0))) {
								System.out.println("OK");
								boardState.doMove(Integer.valueOf(msg), 2);
								endGame = checksMoves.checkEndGame(boardState.boardHoles, boardState.holesPerSide);
								if (endGame) {
									break;
								}
								// send move to other client
								output1.println(msg);
							} else {
								System.out.println(msg);
							}
						} else {
							/*
							 * waiting for player to be ready
							 */
						}
					}

				} catch (SocketTimeoutException s) {
					if (boardState.currentPlayer.player_id == 1) {
						output1.println("TIMER");
						if (!isAI) {
							output2.println("WINNER");
						}
						break;
					} else {
						if (!isAI) {
							output2.println("TIMER");
						}
						output1.println("WINNER");
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			int winnerID = handleEndGame(boardState);
			if (winnerID == 1) {
				if (!isAI) {
					output2.println("LOSER");
				}
				output1.println("WINNER");
			} else if (winnerID == 2) {
				output1.println("LOSER");
				if (!isAI) {
					output2.println("WINNER");
				}
			} else {
				output1.println("TIE");
				if (!isAI) {
					output2.println("TIE");
				}
			}
			output1.close();
			input1.close();
			if (!isAI) {
				output2.close();
				input2.close();
			}

		} catch (IOException e) {
			// report exception somewhere.
			e.printStackTrace();
		}
	}

	public int handleEndGame(boardManager boardState) {
		// Gather all remaining seeds to kalahs and set the houses to 0
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

		// Determine winner
		int winnerID = 0;
		if (boardState.boardHoles.get(boardState.holesPerSide).numSeeds > boardState.boardHoles
				.get((boardState.holesPerSide * 2) + 1).numSeeds) {
			System.out.println("Player 1 wins!");
			winnerID = 1;
		} else if (boardState.boardHoles.get(boardState.holesPerSide).numSeeds < boardState.boardHoles
				.get((boardState.holesPerSide * 2) + 1).numSeeds) {
			System.out.println("Player 2 wins!");
			winnerID = 2;
		} else {
			System.out.println("Its a tie!");
			winnerID = -1;
		}
		return winnerID;
	}
}
