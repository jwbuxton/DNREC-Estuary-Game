package Model;

public class Orc {
	
	final public static int imgWidth = 165;
    final public static int imgHeight = 165;
	
	int xloc = 0;
    int yloc = 0;
    int picNum = 0;
    final int xIncr = 6;
    final int yIncr = 2;
    
    int direction = SE;
    
  //Directions, index for the image's location in pics[i]
  	final static int NE = 6; 
  	final static int NW = 4;
  	final static int SE = 0;
  	final static int SW = 2;

	
	public Orc(){
		xloc = (int) (Math.random()*(Board.frameWidth - imgWidth));
		yloc = (int) (Math.random()*(Board.frameHeight - imgHeight));
		direction = (int) (Math.random()*4)*2;
	}
	
	public int getPicNum(){
		return picNum;
	}
	
	public void updateOrc(int frameCount, int blankSpace){
		picNum = (picNum + 1) % frameCount;
		if (direction == SE){
    		xloc+=xIncr;
    		yloc+=yIncr;
		} else if (direction == SW) {
    		xloc-=xIncr;
			yloc+=yIncr;
		} else if (direction == NE){
    		xloc+=xIncr;
			yloc-=yIncr;
		} else {
			xloc-=xIncr;
			yloc-=yIncr;
		}
		
		if (xloc >= Board.frameWidth-imgWidth+blankSpace){
    		switch(direction){
    			case SE:
    				direction=SW;
    				break;
    			case NE:
    				direction=NW;
    				break;
    		}
    	}
    	else if (xloc <= -blankSpace){
    		switch(direction){
			case SW:
				direction=SE;
				break;
			case NW:
				direction=NE;
				break;
    		}
    	}
    	else if (yloc >= Board.frameHeight-imgHeight+blankSpace){
    		switch(direction){
			case SW:
				direction=NW;
				break;
			case SE:
				direction=NE;
				break;
    		}
    	}
    	else if (yloc <= -blankSpace){
    		switch(direction){
			case NE:
				direction=SE;
				break;
			case NW:
				direction=SW;
				break;
    		}
    	}
	}
	
	public int getDirection(){
		return direction;
	}
	
	public void setDirection(int d){
		direction = d;
	}

	public int getX() {
		return xloc;
	}

	public int getY() {
		return yloc;
	}
}