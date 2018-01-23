package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Simple window display class to show the outcome of the game
 */
public class winnerWin {
 
	public winnerWin(int winnerNum) {
		//Create the frame and components
		JFrame frame = new JFrame("Game Over");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label1;
		JPanel panel = new JPanel();
		
		//Determine winner
		if(winnerNum>0) {
		    label1 = new JLabel("Player " + winnerNum + " won!");
		}
		else {
			label1 = new JLabel("It's a tie!");
		}
		
		//Show frame
		panel.add(label1);
		frame.add(panel);
		frame.setSize(200, 100);
		frame.setVisible(true);
	}
}
