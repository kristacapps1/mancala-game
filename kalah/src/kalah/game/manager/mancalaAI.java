package kalah.game.manager;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/* 
 * this struct holds the data for move options
 */
class moveStruct{
	int move;
	int weight;
	moveStruct(int mv, int wt){
		move=mv;
		weight=wt;
	}
};
/* 
 * this struct holds the data nodes within the minimax tree for ease of code
 */
class nodeStruct{
	ArrayList<kalahHole> state;
	int metric=0;
	int move;
	nodeStruct(ArrayList<kalahHole> board){
		state=board;
	}
	nodeStruct(){
		state=null;
	}
};

class minThread implements Callable<Integer>{
	nodeStruct node;
	int side;
	int startSide;
	int depth;
	int alph;
	int bet;
	int finalVal;
	/* 
	 * constructor that passes necessary information to the thread
	 */
	public minThread(nodeStruct nde, int sde,int strtSide,int dpth,int alp, int bt) {
		node=nde;
		side=sde;
		startSide=strtSide;
		depth=dpth;
		alph=alp;
		bet=bt;
	}
	/* 
	 * this is the thread executable, starts the minimax tree. I only call it for the initial branches of the tree.
	 * Doing it that way makes sure it does not use too much memory, and it cuts down on the execution time by
	 * time/n, n is the number of initial branches
	 */
	public Integer call() throws Exception{
		mancalaAI man=new mancalaAI();
		int alpha=alph;
		int beta=bet;
		
		if(man.terminalTest(node,depth)) {
			return node.metric;
		}
		int newSide=0;
		int v=9999;
		
		ArrayList<nodeStruct> choices=man.miniMaxSuccessors(node,side,startSide);
		if(side==1) newSide=2;
		if(side==2) newSide=1;
		
		for(int i=0;i<choices.size();i++) {
			int mx=man.maxVal(choices.get(i),newSide,startSide,depth+1,alpha,beta);
			if(mx<v) {
				v=mx;
				choices.get(i).metric=man.maxVal(choices.get(i),newSide,startSide,depth+1,alpha,beta);
			}
			beta=Math.min(v, beta);
			if(beta<=alpha)break;
			
		}
		return man.getWorstMetric(choices);
	}	
}
/* 
 * The mancalaAI class holds all of the methods for the AI
 */
public class mancalaAI{
	int max_depth=5;
	int count=0;
	int AI_Side;
	/* 
	 * AIMove takes in the difficulty to choose the AI mode
	 */
	public int AIMove(ArrayList<kalahHole> holes, int side,int difficulty) {
		int move=-1;
		if(difficulty==1) {
			move=generateRandomeMove(holes,side);
		}
		else if(difficulty==2) {
			move=oneMoveAhead(holes,side);
		}
		else if(difficulty==3) {
			move=ThreadedMiniMax(holes,side,max_depth);
		}
		return move;
	}
	/* 
	 * ThreadedMiniMax is the version of minimax that utilizes threads to cut down on execution time
	 */
	public int ThreadedMiniMax(ArrayList<kalahHole> holes, int side,int maxDepth) {
		max_depth=maxDepth;
		int depth=0;
		nodeStruct root=new nodeStruct(holes);		
		
		int newSide=0;
		ArrayList<nodeStruct> choices=miniMaxSuccessors(root,side,side);
		
		if(side==1) newSide=2;
		if(side==2) newSide=1;
		int alpha=-9999;
		int beta=9999;
		
		ExecutorService executor = Executors.newFixedThreadPool(choices.size());
		List<Future<Integer>> list = new ArrayList<Future<Integer>>();
		
		for(int i=0;i<choices.size();i++) {
			Callable<Integer> callable = new minThread(choices.get(i),newSide,side,depth+1,alpha,beta);
			Future<Integer> future = executor.submit(callable);
			list.add(future);
		}
		
		ArrayList<Integer> vals=new ArrayList<Integer>();
		for(Future<Integer> ff:list) {
			try {
                vals.add(ff.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
		}
		return getBestMove(choices);
	}
	/* 
	 * minimax is the non-threaded version of the minimax implimentation
	 */
	public int miniMax(ArrayList<kalahHole> holes, int side,int maxDepth) {
		max_depth=maxDepth;
		int depth=0;
		nodeStruct root=new nodeStruct(holes);		
		
		int newSide=0;
		ArrayList<nodeStruct> choices=miniMaxSuccessors(root,side,side);
		
		if(side==1) newSide=2;
		if(side==2) newSide=1;
		int alpha=-9999;
		int beta=9999;
		
		for(int i=0;i<choices.size();i++) {
			choices.get(i).metric=minVal(choices.get(i),newSide,side,depth+1,alpha,beta);
		}

		return getBestMove(choices);
	}
	/* 
	 * helper function for minimax implementation
	 */
	protected int maxVal(nodeStruct node, int side,int startSide,int depth,int alph, int bet) {
		int alpha=alph;
		int beta=bet;
		if(terminalTest(node,depth)) {
			count++;
			return node.metric;
		}
		int newSide=0;
		int v=-9999;
		
		ArrayList<nodeStruct> choices=miniMaxSuccessors(node,side,startSide);
		if(side==1) newSide=2;
		if(side==2) newSide=1;

		for(int i=0;i<choices.size();i++) {
			int mx=maxVal(choices.get(i),newSide,startSide,depth+1,alpha,beta);
			if(mx>v) {
				v=mx;
				choices.get(i).metric=minVal(choices.get(i),newSide,startSide,depth+1,alpha,beta);
			}
			alpha=Math.max(v, alpha);
			if(beta<=alpha)break;
		}
		return getBestMetric(choices);
	}
	/* 
	 * another helper function for minimax
	 */
	protected int minVal(nodeStruct node, int side,int startSide,int depth,int alph, int bet) {
		int alpha=alph;
		int beta=bet;
		
		if(terminalTest(node,depth)) {
			count++;
			return node.metric;
		}
		int newSide=0;
		int v=9999;
		
		ArrayList<nodeStruct> choices=miniMaxSuccessors(node,side,startSide);
		//System.out.format("\nLLevel %d  Side %d",depth,side);
		if(side==1) newSide=2;
		if(side==2) newSide=1;
		
		for(int i=0;i<choices.size();i++) {
			int mx=maxVal(choices.get(i),newSide,startSide,depth+1,alpha,beta);
			if(mx<v) {
				v=mx;
				choices.get(i).metric=maxVal(choices.get(i),newSide,startSide,depth+1,alpha,beta);
			}
			beta=Math.min(v, beta);
			if(beta<=alpha)break;
			
		}
		return getWorstMetric(choices);
	}

	/* 
	 * this function will return a random, valid move. This could be used as the easiest setting for the game
	 */
	public int generateRandomeMove(ArrayList<kalahHole> holes, int side) {
		Random rand = new Random();
		int numHoles=(holes.size()-2)/2;
		int n;
		for(;;) {
			n = rand.nextInt(numHoles);
			if(side==2) n=2*numHoles-n;
			if(isValidMove(holes,side,n)) break;
			else continue;
		}
		return n;
	}
	/* 
	 * this function will return the best next move. This could be used as the normal difficulty setting for the game
	 */
	public int oneMoveAhead(ArrayList<kalahHole> state, int side) {
		nodeStruct board=new nodeStruct(state);
		ArrayList<nodeStruct> weightedMoves = successors(board,side);
		int bestMove=getBestMove(weightedMoves);
			
		return bestMove;
	}
	/* 
	 * generates an array of valid moves for a given side
	 */
	public ArrayList<Integer> genValidMoves(ArrayList<kalahHole> holes, int side) {
		ArrayList<Integer> validMoves = new ArrayList<Integer>();
		
		int total=holes.size();
		int playerHoles=(total-2)/2;
		int max=-1;
		int min=0;
		
		if(side==1) {
			min=0;
			max=playerHoles;
		}
		else if(side==2) {
			min=playerHoles+1;
			max=2*playerHoles;
		}

		for(int i=min;i<=max;i++) {
			if(isValidMove(holes,side,i)) validMoves.add(i);
		}
		return validMoves;
	}
	
	/* 
	 * this function checks if a move is valid, returns true or false
	 */
	protected boolean isValidMove(ArrayList<kalahHole> holes, int side, int move) {
		boolean isValid=true;
		int numHoles=(holes.size()-2)/2;
		if(side==1) {
			if(move>(numHoles-1) || move<0) isValid= false;
			if(holes.get(move).getNumSeeds()==0) isValid= false;
			
		}
		if(side==2) {
			if(move<(numHoles+1)||move>(2*numHoles)) isValid= false;
			if(holes.get(move).getNumSeeds()==0) isValid= false;
		}
		return isValid;
	}
	/* 
	 * this function will simulate the move for the given board state, then return a value given how successful the move was
	 */
	public int simulateMove(ArrayList<kalahHole> gameBoard, int side, int move, int startSide) {
		ArrayList<kalahHole> board=makeBoardCopy(gameBoard);  //make copy of board
		
		int p1Metric=0;
		int p2Metric=0;
		
		int total=board.size();
		int playerHoles=(total-2)/2;
		
		int piecesLeft=board.get(move).getNumSeeds();                       //pick up the pieces
		board.get(move).setNumSeeds(0);
		
		int index=move+1;
		
		for(int i=0;i<piecesLeft;i++) {			//drop pieces
			if(index==(2*playerHoles+2))index=0;//16
			addOne(board.get(index));
			index++;
		}
		index--;
		if(side==1) {					//adjusts board appropriately after drops
			boolean extraMove=false;
			boolean mySide=false;
			if(index==playerHoles) extraMove=true;
			if(index>=0 && index<playerHoles) mySide=true;
			if(board.get(index).getNumSeeds()==1 && mySide) {
				int oppIndex=2*playerHoles-index;
				int takenSeeds=board.get(oppIndex).getNumSeeds();
				board.get(oppIndex).setNumSeeds(0);
				board.get(playerHoles).setNumSeeds(takenSeeds+board.get(playerHoles).getNumSeeds());
				
			}
			
			if(extraMove) {
				
				nodeStruct node=new nodeStruct(board);
				ArrayList<nodeStruct> moves= successors(node,1);
				if(moves.size()!=0) {
					int bestMove=getBestMove(moves);
					board=moveUpdate(board,1,bestMove);
				}			
			}
			
			if(endGame(board)==1) {
				for(int i=playerHoles+1;i<2*playerHoles+1;i++) {
					int currentVal=board.get(i).getNumSeeds();
					board.get(i).setNumSeeds(0);
					board.get(2*playerHoles+1).setNumSeeds(board.get(2*playerHoles+1).getNumSeeds()+currentVal);
				}
			}
			
			int myScore=board.get(playerHoles).getNumSeeds();
			int theirScore=board.get(2*playerHoles+1).getNumSeeds();
			int scoreMetric= myScore-theirScore;			
			p1Metric= scoreMetric;
			p2Metric=-1*p1Metric;
		}
		if(side==2) {
			boolean extraMove=false;
			boolean mySide=false;
			if(index==2*playerHoles+1) extraMove=true;
			if(index>playerHoles && index<=2*playerHoles) mySide=true;
			if(board.get(index).getNumSeeds()==1 && mySide) {
				int oppIndex=2*playerHoles-index;
				int takenSeeds=board.get(oppIndex).getNumSeeds();
				board.get(oppIndex).setNumSeeds(0);
				board.get(2*playerHoles+1).setNumSeeds(takenSeeds+board.get(2*playerHoles+1).getNumSeeds());
			}
			if(extraMove) {
				
				nodeStruct node=new nodeStruct(board);
				ArrayList<nodeStruct> moves= successors(node,2);
				if(moves.size()!=0) {
					int bestMove=getBestMove(moves);
					board=moveUpdate(board,2,bestMove);
				}			
			}
			if(endGame(board)==2) {
				for(int i=0;i < playerHoles;i++) {
					int currentVal=board.get(i).getNumSeeds();
					board.get(i).setNumSeeds(0);
					board.get(playerHoles).setNumSeeds(board.get(playerHoles).getNumSeeds()+currentVal);
				}
			}

			int myScore=board.get(2*playerHoles+1).getNumSeeds();
			int theirScore=board.get(playerHoles).getNumSeeds();
			int scoreMetric= myScore-theirScore;			
			p2Metric= scoreMetric;
			p1Metric=-1*p2Metric;
		}

		if(startSide==1) return p1Metric;
		if(startSide==2) return p2Metric;
		else return 0;	
	}
	/* 
	 * this function adds one to a kalahHole
	 */
	protected void addOne(kalahHole thisHole){
		thisHole.setNumSeeds(thisHole.getNumSeeds()+1);
	}
	/* 
	 * this function prints the current board (testing purposes)
	 */
	protected void printBoard(ArrayList<kalahHole> gameBoard){
		int total=gameBoard.size();
		int playerHoles=(total-2)/2;
		
		for(int i=2*playerHoles;i>(playerHoles);i--) {
			int currentNum=gameBoard.get(i).getNumSeeds();
			System.out.format("%d  ",currentNum);
		}
		int p1Score=gameBoard.get(playerHoles).getNumSeeds();
		int p2Score=gameBoard.get(2*playerHoles+1).getNumSeeds();
		System.out.format("\n%d                  %d\n",p2Score,p1Score);
		
		for(int i=0;i<playerHoles;i++) {
			int currentNum=gameBoard.get(i).getNumSeeds();
			System.out.format("%d  ",currentNum);
		}
	}
	/* 
	 * this function returns a deep copy of a board state
	 */
	protected ArrayList<kalahHole> makeBoardCopy(ArrayList<kalahHole> board){
		ArrayList<kalahHole> holes=new ArrayList<kalahHole>();
		for(kalahHole h: board) {
			int i=h.numSeeds;
			int j=h.playerSideNum;
			boolean k=h.isKalah;
			holes.add(new kalahHole(i,j,k));
		}
		return holes;
	}
	/* 
	 * generates all of the possible moves and associated board states they lead to
	 */
	public ArrayList<nodeStruct> successors(nodeStruct root, int side){
		ArrayList<Integer> validMoves=genValidMoves(root.state,side);
		ArrayList<nodeStruct> boardList = new ArrayList<nodeStruct>();
		
		for(int i=0;i<validMoves.size();i++) {
			nodeStruct node=new nodeStruct();
			int move=validMoves.get(i);
			node.state=moveUpdate(makeBoardCopy(root.state),side,move) ;
			node.metric=simulateMove(root.state,side,move,side);
			node.move=move;
			boardList.add(node);
		}
		return boardList;
	}
	/* 
	 * generates all of the possible moves and associated board states they lead to specific for minimax
	 */
	public ArrayList<nodeStruct> miniMaxSuccessors(nodeStruct root, int currentSide, int startSide){
		ArrayList<Integer> validMoves=genValidMoves(root.state,currentSide);
		ArrayList<nodeStruct> boardList = new ArrayList<nodeStruct>();
		
		for(int i=0;i<validMoves.size();i++) {
			nodeStruct node=new nodeStruct();
			int move=validMoves.get(i);
			node.state=moveUpdate(makeBoardCopy(root.state),currentSide,move) ;
			node.metric=simulateMove(root.state,currentSide,move,startSide);
			node.move=move;
			
			boardList.add(node);
		}
		return boardList;
	}
	/* 
	 * Updates the board state for a given move
	 */
	public ArrayList<kalahHole> moveUpdate(ArrayList<kalahHole> gameBoard,int side, int move){
		ArrayList<kalahHole> board=makeBoardCopy(gameBoard);  //make copy of board
		
		int total=board.size();
		int playerHoles=(total-2)/2;
		
		int piecesLeft=board.get(move).getNumSeeds();                       //pick up the pieces
		board.get(move).setNumSeeds(0);
		
		int index=move+1;
		
		for(int i=0;i<piecesLeft;i++) {			//drop pieces
			if(index==(2*playerHoles+2))index=0;//16
			addOne(board.get(index));
			index++;
		}
		index--;
		if(side==1) {					//adjusts board appropriately after drops
			boolean extraMove=false;
			boolean mySide=false;
			if(index==playerHoles) extraMove=true;
			if(index>=0 && index<playerHoles) mySide=true;
			if(board.get(index).getNumSeeds()==1 && mySide) {
				int oppIndex=2*playerHoles-index;
				int takenSeeds=board.get(oppIndex).getNumSeeds();
				board.get(oppIndex).setNumSeeds(0);
				board.get(playerHoles).setNumSeeds(takenSeeds+board.get(playerHoles).getNumSeeds());
			}
			if(extraMove) {
				
				nodeStruct node=new nodeStruct(board);
				ArrayList<nodeStruct> moves= successors(node,1);
				if(moves.size()!=0) {
					int bestMove=getBestMove(moves);
					board=moveUpdate(board,1,bestMove);
				}			
			}
			if(endGame(board)==1) {
				for(int i=playerHoles+1;i<2*playerHoles+1;i++) {
					int currentVal=board.get(i).getNumSeeds();
					board.get(i).setNumSeeds(0);
					board.get(2*playerHoles+1).setNumSeeds(board.get(2*playerHoles+1).getNumSeeds()+currentVal);
				}
			}
			
			return board;
		}
		if(side==2) {
			boolean extraMove=false;
			boolean mySide=false;
			if(index==2*playerHoles+1) extraMove=true;
			if(index>playerHoles && index<=2*playerHoles) mySide=true;
			if(board.get(index).getNumSeeds()==1 && mySide) {
				int oppIndex=2*playerHoles-index;
				int takenSeeds=board.get(oppIndex).getNumSeeds();
				board.get(oppIndex).setNumSeeds(0);
				board.get(2*playerHoles+1).setNumSeeds(takenSeeds+board.get(2*playerHoles+1).getNumSeeds());
			}
			if(extraMove) {
				
				nodeStruct node=new nodeStruct(board);
				ArrayList<nodeStruct> moves= successors(node,2);
				if(moves.size()!=0) {
					int bestMove=getBestMove(moves);
					board=moveUpdate(board,2,bestMove);
				}			
			}
			if(endGame(board)==2) {
				for(int i=0;i < playerHoles;i++) {
					int currentVal=board.get(i).getNumSeeds();
					board.get(i).setNumSeeds(0);
					board.get(playerHoles).setNumSeeds(board.get(playerHoles).getNumSeeds()+currentVal);
				}
			}
			
			return board;
		}
		return null;
	}
	/* 
	 * detects if game is over, returns side that has remaining pieces
	 */
	protected int endGame(ArrayList<kalahHole> gameBoard) {
		boolean sideOneEmpty=true;
		boolean sideTwoEmpty=true;
		int total=gameBoard.size();
		int playerHoles=(total-2)/2;
		
		for(int i=0;i < playerHoles;i++) {
			int currentVal=gameBoard.get(i).getNumSeeds();
			if(currentVal != 0) sideOneEmpty=false;
		}
		
		for(int i=playerHoles+1;i<2*playerHoles+1;i++) {
			int currentVal=gameBoard.get(i).getNumSeeds();
			if(currentVal != 0) sideTwoEmpty=false;
		}
		
		if(sideOneEmpty) return 1;
		if(sideTwoEmpty) return 2;
		else return 0;
	}
	/* 
	 * detects if the node is a terminus
	 */
	protected boolean terminalTest(nodeStruct node, int depth) {
		if(endGame(node.state)>0 || depth >= max_depth) return true;
		else return false;
	}
	/* 
	 * this function retrieves the best move from a list of weighted moves
	 */
	protected int getBestMove(ArrayList<nodeStruct> weightedMoves) {
		//System.out.println(weightedMoves.size());
		nodeStruct best=weightedMoves.get(0);
		for(int i=1;i<weightedMoves.size();i++) {
			if(weightedMoves.get(i).metric > best.metric) best=weightedMoves.get(i);
		}
		return best.move;
	}
	/* 
	 * returns the best boardstate 
	 */
	protected int getBestMetric(ArrayList<nodeStruct> weightedMoves) {
		
		nodeStruct best=weightedMoves.get(0);
		for(int i=1;i<weightedMoves.size();i++) {
			if(weightedMoves.get(i).metric > best.metric) best=weightedMoves.get(i);
		}
		return best.metric;
	}
	/* 
	 * returns the worst boardstate
	 */
	protected int getWorstMetric(ArrayList<nodeStruct> weightedMoves) {
		if(weightedMoves.size()>0) {
			nodeStruct worst=weightedMoves.get(0);
			for(int i=1;i<weightedMoves.size();i++) {
				if(weightedMoves.get(i).metric < worst.metric) worst=weightedMoves.get(i);
			}
			return worst.metric;
		}
		else {
			return 0;
		}
	}
}