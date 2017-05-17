package g4.beachGame.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Board {
	/** the amount of seconds that is equals 1 hour on the time of the game**/
	private final static int GAMESEC_PER_HOUR = 3; //sun time bar has 12 notches.
	
	/**width of the board**/
	private final static int WIDTH = 1100; 
	
	/**width of the shore (not including the last column reserved for protectors)**/
	public final static int SHORE_WIDTH = WIDTH-100; 

	/**the number of spots along the shore where the user can place protectors**/
	public final static int SPACES_OF_SHORE = 12;
	
	/**height of the board**/
	public final static int HEIGHT = 600;
	
	/**where the shoreline starts**/
	public final static int SHORE_HEIGHT = HEIGHT/2;
	
	/**whether or not the a spot along the shore has receded to the bottom of the screen**/
	private boolean isShoreDestroyed = false; 
	
	/**which protect is currently being used placed**/
	private int protector = -1;
	
	/**the width of the user, which may change depending on the state**/
	public int user_width = 100;
	
	/**final fields that represents types of objects on the board**/
	public final static int SHORE = 0; 
	public final static int WATER = 1; 
	public final static int GRASS = 2; 
	public final static int GRASS_L =3; 
	public final static int WALL = 4;
	public final static int GABION = 5;
	public final static int GABION_L = 6;
	public final static int GABION_2L = 7;
	
	/**The total rows on the board */
	public final static int TOTALROWS=6;
	
	/**Amount by which to raise height of board due to timer*/
	public final static int RAISE=15;
	
	/**final fields that represent the depth of the shore**/
	public final static int TOP_ROW1=3;
	public final static int TOP_ROW2=4*HEIGHT/TOTALROWS -RAISE ;
	public final static int TOP_ROW3=5*HEIGHT/TOTALROWS -2*RAISE;
	
	/**the array representing what was originally the shore**/
	public int[][] beach = new int[3][SPACES_OF_SHORE]; //height, width
	
	/**the array representing the protectors**/
	public int[] posArr = {HEIGHT/2, TOP_ROW2, TOP_ROW3};

	/**The number of hours for which the game lasts */
	public final int TOTAL_HOURS = 24;
	
	/**how much time is left in the game**/
	public int hoursLeft = TOTAL_HOURS; 
	
	/**nano seconds per second conversion**/
	final static double NANOSECOND_PER_SECOND=1000000000.0;
	
	/**time when user starts playing**/
	final long START_TIME= System.nanoTime();
	
	/**how much time has passed in seconds since user started playing**/
	public double elapsedTime=0;
	
	/**current Waves on the screen moving towards the shore; splitWaves
	 * are the waves that are smaller because a portion **/
	private ArrayList<Wave> currWaves, splitWaves;
	
	/**current Boats on the screen**/
	ArrayList<Boat> currBoats;
	
	/**current Turtles on the screen**/
	ArrayList<Turtle> turtles;
	
	/**the user for this board**/
	public User user;
	
	/**represents if the turtle died or not*/
	private boolean turtleDie = false;
	
	/**the time that tutorial ends and boats can start coming across the screen*/
	private int timeTutorialEnds=17;
	
	/**
	 * Constructor to create a new board of waves and protectors
	 */
	public Board(){
		currBoats = new ArrayList<Boat>();
		setCurrWaves(new ArrayList<Wave>());
		splitWaves = new ArrayList<Wave>();
		user = new User();
		turtles = new ArrayList<Turtle>();
	}

	/**
	 * Updates how much time has elapsed in the game in seconds
	 * */
	public void updateElapsedTime(){
		long currTime=System.nanoTime();
		elapsedTime = (currTime-START_TIME)/NANOSECOND_PER_SECOND;
		hoursLeft = 24 - (int) (elapsedTime /GAMESEC_PER_HOUR);
	}
	
	/**
	 * Checks if player has lost either by seeing if turtle has not made it to ocean in time or 
	 * if the shore has receded too much 
	 * 
	 * @return true if player has lost, false if player has not lost
	 */
	public boolean checkLost(){
		Iterator<Turtle> turtleIt = turtles.iterator();
		while (turtleIt.hasNext()){
			Turtle turtle = turtleIt.next();
			if (turtle.getFramesLeft()<=0)
				turtleDie = true;
		}
		return (isShoreDestroyed||turtleDie);
	}
	
	/**
	 * Creates a random new boat of a random variety
	 * Boats from most frequent to least frequent: Sailboat, Speedboats,and CruiseLiner
	 */
	public void createBoat(){
		if (this.hoursLeft<=timeTutorialEnds){
			//these numbers are just used here to demarkate the ratio at which certain boats come
			int randomNum = 1 + (int)(Math.random() * 7);
			if (randomNum>0 && randomNum<=4)
				// 4 out of 7 times, a sailboat comes
				currBoats.add(new Sailboat());
			else if(randomNum<=6)
				// 2 out of 7 times, a speedboat comes
				currBoats.add(new Speedboat());
			else
				//and only one of out 7 times, a cruiseliner comes
				currBoats.add(new CruiseLiner());
		}
	}
	
	/**
	 * Remove boats from list of current Boats to paint
	 */
	public void checkBoats(){
		Iterator<Boat> boatIt = getCurrBoats().iterator();
		while (boatIt.hasNext()){
			Boat currBoat = boatIt.next();
			if (currBoat.getXLoc()>WIDTH ||currBoat.getXLoc()<0){
				boatIt.remove();
			}
		}
	}
	
	/**
	 * Creates a new wave generated by the input parameter boat and adds it to the
	 * of arrayList of waves currently on the board
	 * 
	 * @param boat - the boat that creates the wave
	 */
	public void createWave(Boat boat){
		if (this.hoursLeft<=17)
			getCurrWaves().add(new Wave(boat));
	}
	
	/**
	 * Creates a turtle by adding to ArrayList of turtles
	 */
	public void createTurtle(){
		turtles.add(new Turtle(this));
	}
	
	/**
	 * Splits wave into smaller waves so that each piece can fall to the lowest piece of shore. Smaller waves
	 * are added to splitWaves array.
	 * 
	 * @param wave - wave to be split
	 */
	public void splitWave(Wave wave){
		int xCoord = wave.getX(); //current wave x coordinate
		int[] xLocs = new int[4]; xLocs[0] = wave.getX(); //array holding the new x locations of split waves
		int numxLocs = 1; //number of x locations in the array
		if (!wave.isOutOfRange()){
			//if wave is off screen
			if (wave.getX()+wave.getLength() > Board.SHORE_WIDTH)
				return;
			//shoreCellPrev is used to see if the current cell differs from the previous cell 
			int shoreCellPrev = beach[(int) (Math.ceil(wave.getY()*6/Board.HEIGHT))-3][xCoord*SPACES_OF_SHORE/SHORE_WIDTH];
			while (xCoord<wave.getX()+wave.getLength()){
				if (beach[(int) (Math.ceil(wave.getY()*6/Board.HEIGHT))-3][xCoord*SPACES_OF_SHORE/SHORE_WIDTH] != shoreCellPrev){
					xLocs[numxLocs] = xCoord;
					numxLocs++;
				}
				shoreCellPrev = beach[(int) (Math.ceil(wave.getY()*6/Board.HEIGHT))-3][xCoord*SPACES_OF_SHORE/SHORE_WIDTH];
				xCoord++;
			}
			xLocs[numxLocs]=xCoord;
			for (int i = 0; i < xLocs.length-1; i++){
				if (i==0 || xLocs[i+1]!=0)
					splitWaves.add(new Wave(wave.speed, xLocs[i+1]-xLocs[i], xLocs[i], wave.getY()));
			}
		}
	}

	/**
	 * Sets the beach grid when the wave hits the shore.  If the shore is already at
	 * the bottom of the screen, the shore is destroyed. If the wave hits the shore, that cell becomes 
	 * water. If the wave hits a protector, the cell becomes the shore.
	 * @param l - the left most spot on the shore the wave hits
	 * @param r - the right most spot on the shore the wave hits
	 */
	public void waveHit(int l, int r) {
		// where the leftmost and right most portion of the wave hits
		int left = (int) (SPACES_OF_SHORE * l / SHORE_WIDTH);
		int right = (int) (SPACES_OF_SHORE * r / SHORE_WIDTH);
		if (left > 11 || right > 11)
			return;
		for (int i = left; i < right + 1; i++) {
			int depth = 0;
			while (depth < beach.length && beach[depth][i] == WATER && i<=SPACES_OF_SHORE) {
				depth++;
			}
			if (depth == beach.length-1&&beach[depth][i]==SHORE){ // the shore is about to reach the bottom of the screen
				isShoreDestroyed = true;
				beach[depth][i] = WATER;
			}
			else if (beach[depth][i] == SHORE){ //shore erodes
				beach[depth][i] = WATER;
			}
			else if (beach[depth][i] != WATER || beach[depth][i] != SHORE) { //wave hits protector
				int protectorHit = beach[depth][i];
				if (protectorHit == GRASS_L || protectorHit == GABION_2L || protectorHit == GABION_L)
					beach[depth][i]--; // protector loses a life
				else
					beach[depth][i] = SHORE;
			}
		}
	}
	
	/**
	 * Returns the protector closest to the user by the user's position on the grid.
	 * @return the integer representing the protector chosen
	 */
	public int chooseProtector() {
		//if user is close enough to protectors
		if ((int)(user.getxLoc()+user_width)*SPACES_OF_SHORE/SHORE_WIDTH == SPACES_OF_SHORE){
			//uses y location to determine which protector is chosen
			if (user.getyLoc() <TOP_ROW2)
				protector = GRASS_L;
			else if (user.getyLoc() >= TOP_ROW2
					&& user.getyLoc() < TOP_ROW3)
				protector = GABION_2L; 
			else{
				protector = WALL;
			}
		}
		return protector;
	}
	
	/**
	 * Sets specific cell of beachgrid on the shoreline at user's xlocation to a protector.
	 */
	public void placeProtector(){
		int depth = 0;
		//places protector using only user's x location
		int spot = (int) (user.getxLoc()+(User.CRAB_WIDTH/2))*SPACES_OF_SHORE/SHORE_WIDTH;
		while (depth < beach.length && beach[depth][spot] != SHORE)
			depth++;
		beach[depth][spot] = getProtector();
		protector = -1;
	}
	
	/** @return the integer representing the current protector */
	public int getProtector(){return protector;}
	
	/**
	 * Returns the array of current boats on the screen.
	 * @return the array of current boats on the screen
	 */
	public ArrayList<Boat> getCurrBoats() {return currBoats;}
	
	/**
	 * Returns the width of the board.
	 * @return the width of the board
	 */
	public static int getWidth(){return WIDTH;}
	
	/**
	 * Returns the height of the board.
	 * @return the height of the board
	 */
	public int getHeight(){return HEIGHT;}
	
	/**
	 * Returns the arraylist of current waves on the screen.
	 * @return the arraylist of current waves on the screen
	 */
	public ArrayList<Wave> getCurrWaves() {return currWaves;}
	
	public ArrayList<Wave> getSplitWaves(){return splitWaves;}
	/**
	 * Sets the attribute currWaves to the parameter currWaves.
	 * @param currWaves the arraylist to replace the attribute currWaves
	 */
	public void setCurrWaves(ArrayList<Wave> currWaves) {
		this.currWaves = currWaves;
	}
	
	/**
	 * Returns arrayList of turtles
	 * @return arrayList of turtles attribute in Board
	 */
	public ArrayList<Turtle> getCurrTurtles(){
		return turtles;
	}
	
	/**
	 * Returns the boolean isShoreDestroyed
	 * @return isShoreDestroyed attribute in Board
	 */
	public boolean getIsShoreDestroyed(){
		return isShoreDestroyed;
	}
	
	/**
	 * Sets isShoreDestroyed attribute to the boolean x
	 * @param x - the boolean to replace isShoreDestroyed with
	 */
	public void setIsShoreDestroyed(boolean isDestroyed){
		isShoreDestroyed = isDestroyed;
	}
	
	/**
	 * Sets turtleDie attribute to the boolean x
	 * @param x - the boolean to replace turtleDie with
	 */
	public void setTurtleDie(boolean isDead){
		turtleDie = isDead;
	}
}


