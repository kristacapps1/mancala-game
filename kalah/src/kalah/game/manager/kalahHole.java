package kalah.game.manager;

// Class to store house data
public class kalahHole {
	public int numSeeds;
	public int playerSideNum;
	public boolean isKalah;
	
    public kalahHole(int numSeeds, int playerSideNum, boolean isKalah){
    	    this.numSeeds = numSeeds;
    	    this.playerSideNum = playerSideNum;
    	    this.isKalah = isKalah;
    }
    
    public void setNumSeeds(int newNumSeeds) {
    	    numSeeds = newNumSeeds;
    }
    
    public int getNumSeeds() {
    	    return numSeeds;
    }
}
