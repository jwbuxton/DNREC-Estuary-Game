package g4.beachGame.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import g4.beachGame.controller.BeachCont;

public class Board {
	/** the amount of seconds that is equals 1 hour on the time of the game**/
	final static int GAMESEC_PER_HOUR = 5; //sun time bar has 12 notches.
	
	/**width of the board**/
	final static int WIDTH = 1100; 
	
	/**width of the shore (not including the last column reserved for protectors)**/
	final static int SHORELINE_WIDTH = WIDTH-100; 

	/**the number of spots along the shore where the user can place protectors**/
	final static int SPACES_OF_SHORE = 12;
	
	/**height of the board**/
	final static int HEIGHT = 600;
	
	/**the x location where the shoreline is **/
	public static int shoreline = HEIGHT/2; //where the shore starts
	
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
	
	/**the array representing what was originally the shore**/
	public static int[][] beach = new int[3][SPACES_OF_SHORE]; //height, width
	
	//I KNOW NO IDEA WHAT THIS ACTUALLY IS
	/**the array representing the protectors**/
	public int[] posArr = {HEIGHT/2, 4*HEIGHT/6 - 15, 5*HEIGHT/6 - 30};
	
	/**how much time is left in the game**/
	int hoursLeft; 
	
	/**nano seconds per second conversion**/
	final static double NANOSECOND_PER_SECOND=1000000000.0;
	/**time when user starts playing**/
	final static long START_TIME= System.nanoTime();
	
	/**how much time has passed in seconds since user started playing**/
	public double elapsedTime=0;
	
	/**current Waves on the screen moving towards the shore**/
	private ArrayList<Wave> currWaves;
	
	/**current Boats on the screen**/
	ArrayList<Boat> currBoats;
	
	/**current Turtles on the screen**/
	ArrayList<Turtle> turtles;
	
	/**the user**/
	public User user;
	
	/**the game should get easier as people keep losing**/
	int difficulty;
	
	/**
	 * creates a new board of waves and protectors
	 * */
	public Board(){
		currBoats = new ArrayList<Boat>();
		setCurrWaves(new ArrayList<Wave>());
		user = new User();
		turtles = new ArrayList<Turtle>();
		hoursLeft = 24;
	}

	/**
	 * updates how much time has elapsed in the game in seconds
	 * */
	public void updateElapsedTime(){
		long currTime=System.nanoTime();
		elapsedTime = (currTime-START_TIME)/NANOSECOND_PER_SECOND;
		hoursLeft = (int) (elapsedTime /GAMESEC_PER_HOUR);
	}

	/**
	 * @return true if player has won, false if player has lost
	 * */
	public boolean win(){
		if (hoursLeft==0 && shoreline>0)
			return true;
		else 
			return false;
	}
	
	/**
	 * 
	 * @return true if player has lost, false if player has not lost
	 */
	public boolean checkLost(){
		return isShoreDestroyed;
	}
	
	/**
	 * creates a random new boat of a random variety
	 * Boats from most frequent to least frequent: Sailboat, Speedboats,and CruiseLiner
	 */
	public void createBoat(){
		int randomNum = 1 + (int)(Math.random() * 7);
		if (randomNum>0 && randomNum<4)
			currBoats.add(new Sailboat());
		else if(randomNum<=6)
			currBoats.add(new Speedboat());
		else
			currBoats.add(new CruiseLiner());
	}
	
	/**
	 * remove boats from list of current Boats to paint
	 */
	public void checkBoats(){
		Iterator<Boat> boatIt = getCurrBoats().iterator();
		while (boatIt.hasNext()){
			Boat currBoat = boatIt.next();
			if (currBoat.getXLoc()>WIDTH ||currBoat.getXLoc()<0){
				currBoats.remove(currBoat);
			}
		}
	}
	
	/**
	 * @param boat : the boat that creates the wave
	 * creates a new wave generated by the input parameter boat and adds it to the
	 * of arrayList of waves currently on the board
	 */
	public void createWave(Boat boat){
		getCurrWaves().add(new Wave(boat));
	}
	
	public void createTurtle(){
		turtles.add(new Turtle());
	}
	
	/**
	 * This method sets the beach grid when the wave hits the shore.  If the shore is already at
	 * the bottom of the screen, the shore is destroyed. If the wave hits the shore, that cell becomes 
	 * water. If the wave hits a protector, the cell becomes the shore.
	 * @param l the left most spot on the shore the wave hits
	 * @param r the right most spot on the shore the wave hits
	 */
	public void waveHit(int l, int r){
		int depth = 0;
		//where the leftmost and right most portion of the wave hits
		int left = (int)(SPACES_OF_SHORE*l/SHORELINE_WIDTH);
		int right = (int)(SPACES_OF_SHORE*r/SHORELINE_WIDTH);
		System.out.println("left length" + l + "spot" + left);
		System.out.println("wave length" + r + "spot" +right);
		for (int i = left; i<right; i++){
			if (i < SHORELINE_WIDTH){
				while (depth < beach.length && beach[depth][i] == WATER)
					depth++;
				if (depth == beach.length) // the shore has reached the bottom of the screen
					isShoreDestroyed = true;
				else if (beach[depth][i] == SHORE){
					beach[depth][i] = WATER;
				}
				else if (beach[depth][i] != WATER || beach[depth][i]!=SHORE){
					int protectorHit = beach[depth][i];
					if (protectorHit == GRASS_L || protectorHit == GABION_2L || protectorHit == GABION_L)
						beach[depth][i]--; //protector loses a life
					else
						beach[depth][i] = SHORE;
				}
			}
		}
	}
	
	public void replaceProtector(int depth, int spot, int protector){
		if (beach[depth][spot]==GRASS || beach[depth][spot]==GABION || beach[depth][spot]==WALL)
			beach[depth][spot]=SHORE;
		else {
			beach[depth][spot]--;
		}
	}

	/**
	 * Returns the protector closest to the user by the user's position on the grid.
	 * @return the integer representing the protector chosen
	 */
	public int chooseProtector() {
		if ((int)(user.getxLoc()+user_width)*12/SHORELINE_WIDTH == 11){ //need to change magic number
			if (user.getyLoc() <4*HEIGHT/6-15)
				protector = GRASS_L;
			else if (user.getyLoc() >= 4*HEIGHT/6 -15 && user.getyLoc() < 5*HEIGHT/6 -30)
				protector = GABION_2L; 
			else
				protector = WALL; 
		}
		return protector;
	}
	
	/**
	 * Sets specific cell of beachgrid on the shoreline at user's xlocation to a protector.
	 */
	public void placeProtector(){
		int depth = 0;
		int spot = (int) user.getxLoc()*SPACES_OF_SHORE/SHORELINE_WIDTH;
		while (depth < beach.length && beach[depth][spot] == WATER)
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
	public int getWidth(){return WIDTH;}
	
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

	/**
	 * Sets the attribute currWaves to the parameter currWaves.
	 * @param currWaves the arraylist to replace the attribute currWaves
	 */
	public void setCurrWaves(ArrayList<Wave> currWaves) {
		this.currWaves = currWaves;
	}
	
	public ArrayList<Turtle> getCurrTurtles(){
		return turtles;
	}
	
}


