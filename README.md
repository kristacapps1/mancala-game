######################################   
Kalah World  
######################################  

This is a Java project to play the game  
Mancala either remotely or through server   
connection.   

########################################  
Build and Run  
########################################  

The game can easily be run as is from Linux command line  
using the included PlayKalah.jar file.  
java -jar PlayKalah.jar  

Because the beginning of the game uses command line interface,  
the jar file must be run from the command line.  

Alternatively, the repo includes the Eclipse project files  
and can be loaded into Eclipse.  

The top file to run would then be runProgram.java  
under kalah/src/kalah/game/manager/ in the kalah.game.manager package  

#########################################  
Gameplay  
#########################################  
The game starts off with three options:  
Menu  
1. Play Remote Game  
2. Play Game with Server  
3. Start Server  

Option 1:  
  - Playing a remote game runs the game on your system only and  
    gives you the option to play vs another person or an AI. You  
    can even put two AIs against each other. Choosing AI will result  
    in a prompt to choose difficulty easy, medium, or hard.  
    
Option 2:  
  - Choosing play game with server means you will connect to another system  
    or potentially your own. 
      > Setting up a Server:  
         - It will ask if you need a new server and if so it will start one for you,  
           which you can run on your own system by typing 'localhost' as the IP. If  
           you select no, you will need the IP of another server to connect to. When  
           a server is started, the port number will be output to the console so that  
           another client can use it to connect. Be sure your firewalls allow incoming  
           and outgoing connections, and that you have the correct IP.  
       > Choosing Players  
         - There are a number of options for player situations  
            > Two clients on different machines can play against each other  
              through the server. Each player can be either human or AI  
            > Two clients can run on the same machine and play each other through  
              the server. Each player can be human or AI  
            > A client can play against the server. In this case the client can  
              be a human or an AI but the server MUST be an AI  
           Note that it is possible to have two clients and the server on the same  
           machine.  
        > Choosing game options  
          - Now whichever machine is running the server will see a server menu pop up.  
            In this menu the server person can choose the number of houses per side,  
            the number of seeds per house, and a timeout limit for turns. Note that if  
            the timeout ever does run out during the game, whoever's turn it was will  
            automatically lose, even if they were ahead.  
         > Playing the Game  
           - A GUI will come up on each client screen. This means two GUIs will pop up  
             if clients are on the same machine. The GUIs are labeled with player number  
             at the top bar. You may see server and client communication messages in the  
             console, most often 'OK', which is sent when the server successfully recieves  
             information. We no longer need the console and you can ignore these. When a  
             a player wins, a pop-up message will show on each screen. The rules for the  
             game Kalah can be found at:  
             http://mancala.wikia.com/wiki/Kalah  
             
      
             
            

