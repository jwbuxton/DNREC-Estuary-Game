package g4.storyGame.view;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import g4.mainController.MainMenu;
import g4.storyGame.model.Table;

public class StoryView extends JPanel{

	//reference to Model, used to get data
	private final Table refTable;
	
	//This game's window
	private final JFrame frame;
	private final int FRAME_WIDTH = 950;
	private final int FRAME_HEIGHT = 500;
	
	//Dimensions & locations of images
	private final int IMG_WIDTH = 100;
	private final int IMG_HEIGHT = 100;
	private static final String[] imagesLoc = {"images/StoryImages/TestImage.png",
			"images/StoryImages/TestImage2.png",
			"images/StoryImages/TestImage3.png",
			"images/StoryImages/TestImage4.png"};
	//The number of possible sides of cubes (total number of images)
	public static final int NUM_SIDES = imagesLoc.length;
	
	//Every possible image
	private final BufferedImage[] images = new BufferedImage[imagesLoc.length];
	
	//Buttons/Images for Cubes
	private final JButton[] cubes;
	private List<BufferedImage> finalized = new ArrayList<BufferedImage>();
	
	public StoryView(Table t){
		//create all images
		for (int i = 0; i < imagesLoc.length; i++){
			images[i] = createImage(imagesLoc[i]);
		}
		
		//set refernce
		refTable = t;
		
		//set up window
		frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setBackground(MainMenu.BACKGROUND_BLUE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		//makes frame visible and sets no layout
		frame.setVisible(true);
		frame.setLayout(null);
		
		//create JButtons for Cubes
		
		cubes = new JButton[refTable.NUM_DICE];
		
		for (int i = 0; i < refTable.NUM_DICE; i++){
			cubes[i] = new JButton(new ImageIcon(images[refTable.getCubeAt(i).getImg()]));
			cubes[i].setBackground(MainMenu.BACKGROUND_BLUE);
			cubes[i].addActionListener(new CubeActionListener(i));
			frame.add(cubes[i]);
			cubes[i].setBounds(IMG_WIDTH/2 + (int)(i*1.2*IMG_WIDTH), IMG_HEIGHT/2, IMG_WIDTH, IMG_HEIGHT);
			cubes[i].setSize(IMG_WIDTH, IMG_HEIGHT);
		}
	}
	
	//This private class allows the Cube JButtons to trigger when clicked
	private class CubeActionListener implements ActionListener{
		private final int cubeNum; 
		CubeActionListener(int i){
			cubeNum = i;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			clicked(cubeNum);
		}
	}
	
	//Runs when a Cube's JButton is clicked
	private void clicked(int i) {
		if (!refTable.getCubeAt(i).isFixed())
			//if not fixed
			refTable.getCubeAt(i).fix();
		else if (!refTable.getCubeAt(i).isMoved()){
			//if not moved
			//move
			refTable.getCubeAt(i).move();
			//and disable the button
			cubes[i].setEnabled(false);
			
			//add to lower list of displayed cubes
			finalized.add(images[refTable.getCubeAt(i).getImg()]);
			
			//If everything has been added, display a close button
			if (finalized.size() == refTable.NUM_DICE){
				JButton quit = new JButton("Return to Menu");
				quit.setFont(new Font("SansSerif", Font.BOLD, 20));
				quit.setBackground(MainMenu.SEA_GREEN);
				quit.setForeground(MainMenu.TEXT_BROWN);
				quit.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				frame.add(quit);
				quit.setBounds(IMG_WIDTH/2, (int)(3.5*IMG_HEIGHT), 300, 50);
				quit.setSize(300, 50);
			}
		}
	}

	private BufferedImage createImage(String fileName){ //converts filename to buffered image
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File(fileName));
			return bufferedImage;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void paint(Graphics g) {

		//set icons to JButtons
		for (int i = 0; i < cubes.length; i++){
			cubes[i].setIcon(new ImageIcon(images[refTable.getCubeAt(i).getImg()]));
		}
		//draw finalized images
		for (int i = 0; i < finalized.size(); i++){
			g.drawImage(finalized.get(i),IMG_WIDTH/2 + (int)(i*1.2*IMG_WIDTH), 2*IMG_HEIGHT, null, this);
		}
	}
	
	public void dispose() {
		frame.dispose();
	}

	public void update() {
		frame.repaint();
	}
}
