package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import kalah.game.manager.Player;
import kalah.game.manager.boardManager;
import network.networkPlayer;

public class serverMenu extends JFrame {
    
    private JPanel contentPane;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    int numSeeds;
    int holeNum;
    int timeVal = 0;
    public Player playerOne;
    public Player playerTwo;
    public networkPlayer currentPlayer;
    public int holesPerSide;
    public int seedsPerHouse;
    
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    boardManager bm = new boardManager();
                    Menu frame = new Menu(bm);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Create the frame.
     */
    public serverMenu(boardManager boardState) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Menu.class.getResource("/GUI/resources/mancala.png")));
        setFont(new Font("Arial Narrow", Font.BOLD, 12));
        setTitle("Kalah World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        JLabel lblWelcomeToKalah = new JLabel("Kalah Game!");
        lblWelcomeToKalah.setForeground(new Color(139, 0, 0));
        lblWelcomeToKalah.setFont(new Font("Showcard Gothic", Font.BOLD, 20));
        
        JLabel lblPlayingVs = new JLabel("Playing vs");
        
        JLabel lblNumberOfSeeds = new JLabel("Number of Seeds");
        
        JLabel lblNewLabel = new JLabel("AI Difficulty");
        
        Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(1, new JLabel("Easy"));
        labels.put(2, new JLabel("Normal"));
        labels.put(3, new JLabel("Hard"));
        
        JButton btnStartGame = new JButton("Start Game!");
        btnStartGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Set values and setup the board in the backend
                boardState.setStartingSeedNum(numSeeds);
                boardState.setCurrentPlayer(boardState.playerOne);
                boardState.setupBoard(boardState.holesPerSide, boardState.startingSeedNum);
                boardState.setTimeoutVal(timeVal);
                
                // Let the boardManager know we are ready to start the game
                boardState.setStartGame(true);
                dispose();
            }
        });
        
        JLabel lblNumberOfHouses = new JLabel("Number of Houses");
        JLabel labelTimeout = new JLabel("Timeout (ms)");
        
        
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() instanceof JRadioButton){
                    JRadioButton radioButton = (JRadioButton) e.getSource();
                    if(radioButton.isSelected()){
                        radioButton.getText();
                        
                    }
                }
            }
        };
        
        //Seeds per house selector
        JComboBox comboBox = new JComboBox();
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                numSeeds = (int)comboBox.getSelectedItem();
                boardState.setStartingSeedNum(numSeeds); ;
                
            }
        });
        for(int i = 1; i < 11; i++) {
            comboBox.addItem(i);
        }
        
        // Holes per side selector
        JComboBox comboBox_1 = new JComboBox();
        comboBox_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                holeNum = (int)comboBox_1.getSelectedItem();
                boardState.setHolesPerSide(holeNum); ;
            }
        });
        for(int i = 1; i < 9; i++) {
            comboBox_1.addItem(i);
        }
        
        // Holes per side selector
        JComboBox timeoutOptions = new JComboBox();
        timeoutOptions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeVal = (int)timeoutOptions.getSelectedItem();
                boardState.setTimeoutVal(timeVal);
            }
        });
        for(int i = 1; i < 31; i++) {
            timeoutOptions.addItem(i*1000);
        }
        
        
        JPanel p = new JPanel();
        JPanel labelNames = new JPanel();
        p.setLayout(new GridLayout(3,1));
        labelNames.setLayout(new GridLayout(3,1));
        
        lblNumberOfSeeds.setPreferredSize(new Dimension(300,100));
        lblNumberOfHouses.setPreferredSize(new Dimension(300,100));
        labelTimeout.setPreferredSize(new Dimension(300,100));
        
        labelNames.add(lblNumberOfSeeds);
        labelNames.add(lblNumberOfHouses);
        labelNames.add(labelTimeout);
        
        comboBox.setPreferredSize(new Dimension(100,100));
        comboBox_1.setPreferredSize(new Dimension(100,100));
        timeoutOptions.setPreferredSize(new Dimension(100,100));
        
        p.add(comboBox);
        p.add(comboBox_1);
        p.add(timeoutOptions);
        
        add(labelNames);
        add(p);
        add(btnStartGame,BorderLayout.PAGE_END);

    }
}

