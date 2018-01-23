package kalah.game.manager;

/*
 * Class to store player information
 */

public class Player {
    public int player_id;
    public int score;
    public int opponent_id;
    public int AIdifficulty = 0;
    public boolean is_AI;
    public Player opponent;
    
    public Player(int player_id, boolean is_AI, int AIdifficulty){
	    this.player_id = player_id;
	    this.score = 0;
	    this.is_AI = is_AI;
	    this.AIdifficulty = AIdifficulty;
    }
    
    public void setOpponent(Player opponent) {
    	    this.opponent_id = opponent.player_id;
    	    this.opponent = opponent;
    }
    
    public Player getOpponent() {
	    return opponent;
    }
    
    public void increaseScore(int addPoints){
    	    this.score += addPoints;
    }
    public void setScore(int newScore) {
    	    this.score = newScore;
    }
    public void setAI(boolean isAI) {
	    this.is_AI = isAI;
    }
    public void setDifficulty(int difficulty) {
	    this.AIdifficulty = difficulty;
    }
    public void setID(int idNum) {
    	player_id = idNum;
    }
    public void setOpponentID(int opID) {
    	opponent_id = opID;
    }
    public boolean getAI() {
    	    if(is_AI) {
    	    	    return true;
    	    }
    	    return false;
    }
}
