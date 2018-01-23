package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import kalah.game.manager.boardManager;
import kalah.game.manager.runProgram;

/*
 * SERVER CLASS
 * Server actions in WorkerRunnable class
 */
public class Server {
	public int portNum = 8080;
	private Thread runningThread = null;
	private ServerSocket serverSocket = null;
	private boolean isStopped = false;
	private boolean isAI;
	private int servDifficulty;

	public Server(int portNum, boolean isAI, int difficulty) throws InterruptedException {
		this.portNum = portNum;
		this.isAI = isAI;
		this.servDifficulty = difficulty;
		runServer.start();
		Thread.sleep(20 * 200);
	}

	/*
	 * Run Server Application
	 */
	Thread runServer = new Thread() {
		public void run() {
			synchronized (this) {
				runningThread = Thread.currentThread();
			}
			ServerSocket listener;
			try {
				serverSocket = new ServerSocket(portNum);
				System.out.println("Kalah Server Now Running on port " + serverSocket.getLocalPort());
				portNum = serverSocket.getLocalPort();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (!isStopped()) {
				Socket clientSocket1 = null;
				Socket clientSocket2 = null;
				try {
					if (!isAI) {
						clientSocket1 = serverSocket.accept();
						clientSocket2 = serverSocket.accept();
					} else {
						clientSocket1 = serverSocket.accept();
					}
				} catch (IOException e) {
					if (isStopped()) {
						System.out.println("Server Stopped.");
						return;
					}
					throw new RuntimeException("Error accepting client connection", e);
				}
				
				//Start a server thread
				new Thread(
						new WorkerRunnable(clientSocket1, clientSocket2, "Multithreaded Server", isAI, servDifficulty))
								.start();
			}
		}
	};

	public ServerSocket getServer() {
		return serverSocket;
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}
}
