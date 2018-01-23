package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import kalah.game.manager.*;

public class BoardDrawing {
    ArrayList<JButton> buttons;
    JButton kalahOne;
    JButton kalahTwo;
    JFrame frame;
    
    public BoardDrawing(int holesPerSide,boardManager boardState) {
        buttons = new ArrayList<JButton>();
        kalahOne = new JButton("0");
        kalahTwo = new JButton("0");
        
        // Add all the house buttons
        for(int j = 0; j<= (holesPerSide*2); j++) {
            buttons.add(new JButton());
        }
        
        // House buttons listener
        for(int i = 0; i<= (holesPerSide*2); i++) {
            buttons.get(i).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object tmpButton = e.getSource();
                    int j = 0;
                    
                    /*
                     * Find which index in the grid layout this button
                     * is located at
                     */
                    for(; j<buttons.size(); j++) {
                        if(buttons.get(j) == tmpButton) {
                            break;
                        }
                    }
                    /*Determine and set move number to done
                     *Must be translated from grid layout indices which are
                     *
                     * 0  1  2  3  4  5  6
                     * 7  8  9  10 11 12 13
                     *
                     * To boardmanager indices
                     * |    | 12 | 11 | 10 | 9  | 8 | 7 |   |
                     * | 13 |---------------------------| 6 |
                     * |    | 0  | 1  | 2  | 3  | 4 | 5 |   |
                     */
                    if(j<holesPerSide) {
                        if(boardState.currentPlayer.player_id==2) {
                            boardState.setMove((holesPerSide*2)-j);
                            boardState.setWaitingMove(false);
                        }
                        
                    }
                    else if(j == holesPerSide) {
                        if(boardState.currentPlayer.player_id==1) {
                            boardState.setMove(0);
                            boardState.setWaitingMove(false);
                        }
                    }
                    else {
                        if(boardState.currentPlayer.player_id==1) {
                            boardState.setMove(j-holesPerSide);
                            boardState.setWaitingMove(false);
                        }
                    }
                    
                    //Signal board manager that we are ready to process move
                    boardState.setWaitingMove(false);
                    
                    //update displays after the move
                    reprintButtons(boardState,holesPerSide);
                }
            });
        }
        
        // houses button grid layout
        //ButtonGroup buttones = new ButtonGroup();
        JPanel p = new JPanel();
        p.setLayout((new GridLayout(2,holesPerSide)));
        
        //Add all the houses buttons
        for(int index = 0; index < holesPerSide*2; index++) {
            buttons.get(index).setPreferredSize(new Dimension(50,50));
            p.add(buttons.get(index));
        }
        
        /*
         * Add kalah displays
         */
        JPanel m = new JPanel();
        m.setLayout(new BorderLayout());
        m.setAlignmentX(0);
        kalahOne.setPreferredSize(new Dimension(50,50));
        m.add(kalahOne,BorderLayout.WEST);
        kalahTwo.setPreferredSize(new Dimension(50,50));
        m.add(kalahTwo,BorderLayout.EAST);
        m.add(p,BorderLayout.CENTER);
        
        
        /*
         * Create board frame and set parameters
         */
        frame = new JFrame("Kalah");
        frame.setSize((holesPerSide*100 + 200),400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(m);
        frame.setVisible(true);
        
        /*
         * UPDATE KALAH HOUSES
         */
        reprintButtons(boardState,holesPerSide);
    }
    
    /*
     * Function to update the board display
     * House button texts
     * indices must be converted
     * See the above
     */
    public void reprintButtons(boardManager boardState,int holesPerSide) {
        int tempIndex = holesPerSide*2;
        kalahTwo.setText(Integer.toString(boardState.boardHoles.get(holesPerSide).numSeeds));
        kalahOne.setText(Integer.toString(boardState.boardHoles.get((holesPerSide*2)+1).numSeeds));
        for(int tmp = 0; tmp < (holesPerSide*2)+1; tmp++) {
            if(tmp < holesPerSide) {
                buttons.get(tmp).setText(Integer.toString(boardState.boardHoles.get(tempIndex).numSeeds));
                tempIndex--;
            }
            else if(tmp == holesPerSide) {
                tempIndex = 0;
                buttons.get(tmp).setText(Integer.toString(boardState.boardHoles.get(tempIndex).numSeeds));
                tempIndex++;
            }
            else {
                buttons.get(tmp).setText(Integer.toString(boardState.boardHoles.get(tempIndex).numSeeds));
                tempIndex++;
            }
        }
    }
    public void setTitle(String newTitle) {
        frame.setTitle(newTitle);
    }
}
