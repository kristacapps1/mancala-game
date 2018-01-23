package kalah.game.manager;

import java.util.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import javax.swing.JDialog;

import network.*;
import GUI.*;

// Run game program
public class runProgram {
	public Player playerOne;
	public Player playerTwo;
	public networkPlayer currentPlayer;
	public int holesPerSide;
	public int seedsPerHouse;

	public static void main(String[] args) {
		int holesPerSide = 0;
		int seedsPerHouse = 0;
		boolean playerOneAI = false;
		boolean playerTwoAI = false;
		int playerOneDifficulty = 0;
		int playerTwoDifficulty = 0;
		boardManager boardKeeper = new boardManager();
		System.out.println("Menu");
		System.out.println("1. Play Remote Game");
		System.out.println("2. Play Game with Server");
		System.out.println("3. Start Server");
		Scanner getmove = new Scanner(System.in);
		int optionNum = getmove.nextInt();
		int portNum = 0;
		String ipAddr = "";
		String tmpCl2 = "";
		String tmpClAI = "";
		int cl2Diff = 0;
		int servDifficulty = 0;

		// We are running game locally
		if (optionNum == 1) {
			// Get player options
			System.out.println("Player one: type \"player\" for player or \"ai\" for AI");
			Scanner getAI = new Scanner(System.in);
			String tmp = getAI.next();

			if (tmp.equals("player")) {
				playerOneAI = false;
			} else {
				playerOneAI = true;
				System.out.println("AI difficulty: 1 for easy, 2 for medium, 3 for hard");
				playerOneDifficulty = getAI.nextInt();
			}
			System.out.println("Player two: type \"player\" or \"ai\"");
			tmp = getAI.next();
			if (tmp.equals("player")) {
				playerTwoAI = false;
			} else {
				playerTwoAI = true;
				System.out.println("AI difficulty: 1 for easy, 2 for medium, 3 for hard");
				playerTwoDifficulty = getAI.nextInt();
			}

			// Run game
			runProgram progRunner = new runProgram(playerOneAI, playerTwoAI, playerOneDifficulty, playerTwoDifficulty);
			boardKeeper = new boardManager(holesPerSide, seedsPerHouse, progRunner.playerOne, progRunner.playerTwo);
			progRunner.runGame(boardKeeper);
		}
		// We are doing client server game
		if (optionNum == 2) {
			Server newServer;
			boolean servIsAI = false;
			boolean cliIsAI = false;
			int difficulty = 0;
			Player playerData;

			// Handle options
			System.out.println("Do you need a new server? (y/n)");
			Scanner getServ = new Scanner(System.in);
			String tmp = getServ.next();
			System.out.println("Enter the IP of server");
			ipAddr = getServ.next();
			System.out.println("Will client play as AI? (y/n)");
			Scanner getCl = new Scanner(System.in);
			String tmpCl = getCl.next();
			if (tmpCl.equals("y")) {
				cliIsAI = true;
				System.out.println("AI difficulty: 1 for easy, 2 for medium, 3 for hard");
				Scanner getdiff = new Scanner(System.in);
				difficulty = getdiff.nextInt();
			}
			System.out.println("Would you like two clients on this machine?(y/n)");
			Scanner getCl2 = new Scanner(System.in);
			tmpCl2 = getCl.next();
			if (tmpCl2.equals("y")) {
				System.out.println("Will client play as AI? (y/n)");
				tmpClAI = getCl.next();
				if (tmpClAI.equals("y")) {
					System.out.println("AI difficulty: 1 for easy, 2 for medium, 3 for hard");
					Scanner getCl2Diff = new Scanner(System.in);
					cl2Diff = getCl.nextInt();
				}
			}

			// Start Server
			if (tmp.equals("y")) {
				// Is server AI
				System.out.println("Will server play as AI? (y/n)");
				Scanner getAI = new Scanner(System.in);
				String tmpAI = getAI.next();
				if (tmpAI.equals("y")) {
					servIsAI = true;
					System.out.println("AI difficulty: 1 for easy, 2 for medium, 3 for hard");
					Scanner getdiff = new Scanner(System.in);
					servDifficulty = getdiff.nextInt();
				}
				// Make server and report port number
				try {
					newServer = new Server(0, servIsAI, servDifficulty);
					portNum = newServer.portNum;
					runProgram progRunner = new runProgram(playerOneAI, playerTwoAI, playerOneDifficulty,
							playerTwoDifficulty);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// If we already have a server, get the port number for client
			else {
				System.out.println("Enter Port Number");
				Scanner getPort = new Scanner(System.in);
				portNum = getPort.nextInt();
			}

			// Create and run the client
			try {
				Socket socket1 = new Socket(ipAddr, portNum);
				if (cliIsAI) {
					playerData = new Player(0, true, difficulty);
				} else {
					playerData = new Player(0, false, 0);
				}
				networkPlayer playerOneNet = new networkPlayer(socket1, playerData);
				playerOneNet.start();
				// If we need two clients, create another and attempt to connect
				if (tmpCl2.equals("y")) {
					Socket socket2 = new Socket(ipAddr, portNum);
					if (tmpClAI.equals("y")) {
						playerData = new Player(0, true, cl2Diff);
					} else {
						playerData = new Player(0, false, 0);
					}
					networkPlayer playerTwoNet = new networkPlayer(socket2, playerData);
					playerTwoNet.start();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (optionNum == 3) {
			boolean servIsAI = false;
			System.out.println("Will server play as AI? (y/n)");
			Scanner getAI = new Scanner(System.in);
			String tmpAI = getAI.next();
			if (tmpAI.equals("y")) {
				servIsAI = true;
			}
			// Make server and report port number
			try {
				Server newServer = new Server(0, servIsAI, servDifficulty);
				portNum = newServer.portNum;
				System.out.println("Server found on at " + InetAddress.getLocalHost() + " at port " + newServer.portNum);
				runProgram progRunner = new runProgram(playerOneAI, playerTwoAI, playerOneDifficulty,
						playerTwoDifficulty);
				// progRunner.runGameClientServer(newServer);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public void setHolesPerSide(int numHoles) {
		holesPerSide = numHoles;
	}

	public int getHolesPerSide() {
		return holesPerSide;
	}

	public void setSeedsPerHouse(int numSeeds) {
		this.seedsPerHouse = seedsPerHouse;
	}

	public int getSeedsPerHouse() {
		return seedsPerHouse;
	}

	public runProgram(boolean playerOneIsAI, boolean playerTwoIsAI, int playerOneDifficulty, int playerTwoDifficulty) {
		playerOne = new Player(1, playerOneIsAI, playerOneDifficulty);
		playerTwo = new Player(2, playerTwoIsAI, playerTwoDifficulty);
		playerOne.setOpponent(playerTwo);
		playerTwo.setOpponent(playerOne);
	}

	// Run remote game with GUI
	public void runGame(boardManager boardSet) {
		boolean isEndGame = false;
		// boardSet.currentPlayer = playerOne;
		boardSet.setCurrentPlayer(playerOne);
		Menu gui = new Menu(boardSet);
		gui.setVisible(true);

		// Wait for start game signal from gui
		while (!boardSet.startGame) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		while (true) {
			// Do moves while the game is not over
			isEndGame = boardSet.handleMove(boardSet.currentPlayer);
			if (isEndGame) {
				break;
			}

			// Swap players
			if (boardSet.currentPlayer == playerOne) {
				boardSet.setCurrentPlayer(playerTwo);
			} else {
				boardSet.setCurrentPlayer(playerOne);
			}

		}

		// let return, game over

	}
}
