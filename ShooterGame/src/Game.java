import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Timer;

public class Game implements ActionListener, KeyListener {
	// Global Constants
	public static final int FRAME_WIDTH = 640;
	public static final int FRAME_HEIGHT = 480;
	public static final int FIELD_WIDTH = 640;
	public static final int FIELD_HEIGHT = 455;
	public static final int ADD_MISSILE_EVERY_X_TIME = 5000;
	public static final int MAX_MISSILES = 20;

	// Local Constants
	private final int TIMER_SPEED = 10;
	private final int TIMER_DELAY = 0;

	private final int SHOOTER_SPEED = 6;

	private final int NUM_SMALL_ALIENS = 8;
	private final int NUM_MEDIUM_ALIENS = 4;
	private final int NUM_LARGE_ALIENS = 4;

	private Clip audioClip;

	private int score = 0;
	private int playerLives = 5;
	private int missileCount = 10;
	private int MissileChance;
	private int RandomAlien;
	private JFrame gameFrame;
	private Timer timer;
	private Timer missileTimer;
	private int counter = 0;

	// These Images could be loaded without the use of the 'getClass' and
	// 'getResource'
	// methods, but using those two methods allows all of the files that make up
	// this
	// program (the .CLASS files and the graphics files) to be put into a single
	// .JAR
	// file and then loaded and run directly from that (executable) file; in
	// addition,
	// a benefit of using the 'ImageIcon' class is that, unlike some of the other
	// file-loading classes, the 'ImageIcon' class fully loads the Image when the
	// object is created, making it possible to immediately determine and use the
	// dimensions of the Image
	private ImageIcon imgBackground = new ImageIcon(getClass().getResource("space.png"));
	private ImageIcon imgShooter = new ImageIcon(getClass().getResource("Untitled.png"));

	private JLabel lblShooter = new JLabel(imgShooter);
	private int shooterX, shooterY;

	private boolean pressedLeft = false, pressedRight = false, pressedSpace = false, pressedUp = false,
			pressedDown = false;
	private boolean controlKeyPressed = false, missileFired = false;

	private JLabel lblGameOver = new JLabel("Game Over!");
	private Font fontGameOver = new Font("Helvetica", Font.BOLD, 24);
	private int textWidth = lblGameOver.getFontMetrics(fontGameOver).stringWidth(lblGameOver.getText());

	private JLabel lblScore = new JLabel("Score: " + score);
	private Font fontScore = new Font("Helvetica", Font.BOLD, 16);

	private JLabel lblPlayerLives = new JLabel("Lives: " + playerLives);
	private Font fontPlayerLives = new Font("Helvetica", Font.BOLD, 12);

	private JLabel lblMissileCount = new JLabel("Missles: " + missileCount);

	// Create ArrayLists to hold the 'Alien' objects (all types) and the 'Missile'
	// objects that will be used throughout the game
	ArrayList<Alien> aliens = new ArrayList<Alien>();
	ArrayList<Missile> alienMissiles = new ArrayList<Missile>();
	ArrayList<Missile> playerMissiles = new ArrayList<Missile>();
	
	


	public static void main(String[] args) {
		new Game();
	}

	// ConstructorS
	public Game() {
		gameFrame = new JFrame();

		// Set the background image and other JFrame properties
		gameFrame.setContentPane(new JLabel(imgBackground));
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		gameFrame.setTitle("Pascal and Jack's Simple Shooter Game");
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setResizable(false);
		gameFrame.setFocusable(true);
		
		

		// Set up the "Game Over" JLabel
		lblGameOver.setVisible(false);
		lblGameOver.setSize(textWidth, 50);
		lblGameOver.setLocation(FIELD_WIDTH / 2 - textWidth / 2, FIELD_HEIGHT / 2 - 50);
		lblGameOver.setFont(fontGameOver);
		lblGameOver.setForeground(Color.YELLOW);

		// Set up the "score" JLabel
		lblScore.setSize(200, 15);
		lblScore.setLocation(0, 30);
		lblScore.setFont(fontScore);
		lblScore.setForeground(Color.YELLOW);
		lblScore.setVisible(true);
		gameFrame.add(lblScore);

		// Set up the "missile count" JLabel
		lblMissileCount.setSize(200, 15);
		lblMissileCount.setLocation(275, 30);
		lblMissileCount.setFont(fontScore);
		lblMissileCount.setForeground(Color.YELLOW);
		lblMissileCount.setVisible(true);
		gameFrame.add(lblMissileCount);

		// Set up "PlayerLives" JLabel
		lblPlayerLives.setSize(200, 43);
		lblPlayerLives.setLocation(560, 20);
		lblPlayerLives.setFont(fontPlayerLives);
		lblPlayerLives.setForeground(Color.YELLOW);
		lblPlayerLives.setVisible(true);
		gameFrame.add(lblPlayerLives);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu aboutMenu = new JMenu("About");
		JMenuItem about = new JMenuItem("About");
		JMenuItem pause = new JMenuItem("Pause");
		JMenuItem exit = new JMenuItem("Exit");
		gameFrame.add(menuBar);
		
		menuBar.setSize(640, 25);
		fileMenu.setVisible(true);
		fileMenu.setText("File");
		menuBar.add(fileMenu);
		editMenu.setVisible(true);
		editMenu.setText("Edit");
		menuBar.add(editMenu);
		aboutMenu.setVisible(true);
		aboutMenu.setText("About");
		menuBar.add(aboutMenu);
		menuBar.setVisible(true);
		
		aboutMenu.add(about);
		about.setVisible(true);
		fileMenu.add(exit);
		exit.setVisible(true);
		editMenu.add(pause);
		pause.setVisible(true);
		pause.addActionListener(new Game());
		about.addActionListener(new Game());
		exit.addActionListener(new Game());
		pause.setActionCommand("pause");
		exit.setActionCommand("exit");
		about.setActionCommand("about");
		

		
		setUpShooter();
		setUpLargeAliens();
		setUpMediumAliens();
		setUpSmallAliens();
		SoundPlayLoopStopWithWAV();

		gameFrame.addKeyListener(this);
		gameFrame.setVisible(true);

		// Set (and start) a new Swing Timer to fire every 'TIMER_SPEED' milliseconds,
		// after an initial delay of 'TIMER_DELAY' milliseconds; this Timer, along
		// with the distance (number of pixels) that the aliens, missiles, and shooter
		// move with each cycle, controls how fast the objects move on the playing
		// field; note that if adding a "pause/unpause" feature to this game, the
		// value of the 'TIMER_DELAY' constant should probably be set to zero
		timer = new Timer(TIMER_SPEED, this);
		missileTimer = new Timer(ADD_MISSILE_EVERY_X_TIME, this);
		timer.setInitialDelay(TIMER_DELAY);
		missileTimer.setInitialDelay(ADD_MISSILE_EVERY_X_TIME);
		timer.setActionCommand("timer");
		missileTimer.setActionCommand("Missile Timer");
		timer.start();
		missileTimer.start();

	}

	// Set the size and starting position of the player's shooter
	public void setUpShooter() {
		// Set the size of the JLabel that contains the shooter image
		lblShooter.setSize(imgShooter.getIconWidth(), imgShooter.getIconHeight());

		// Set the shooter's initial position on the playing field; note
		// that subtracting 30 pixels accounts for the JFrame title bar
		shooterX = (FIELD_WIDTH / 2) - (lblShooter.getWidth() / 2);
		shooterY = FIELD_HEIGHT - lblShooter.getHeight() - 40;
		lblShooter.setLocation(shooterX, shooterY);

		// Add the shooter JLabel to the JFrame
		gameFrame.add(lblShooter);
	}

	// Create and randomly place the appropriate number of SMALL aliens on the
	// playing field
	public void setUpSmallAliens() {
		// Determine the width and height of each alien being placed
		Alien tempAlien = new SmallAlien(0, 0);
		int alienWidth = tempAlien.getWidth();
		int alienHeight = tempAlien.getHeight();

		for (int i = 0; i < NUM_SMALL_ALIENS; i++) {
			// Set the starting positions of each of the aliens being placed
			int x = (int) (Math.random() * (FIELD_WIDTH - alienWidth - 7) + 1);
			int y = (int) (Math.random() * (FRAME_HEIGHT - alienHeight - 26 - lblShooter.getHeight() - 60));

			// Create a new 'Alien' object and add it to the 'aliens' ArrayList
			aliens.add(new SmallAlien(x, y));
		}
	}

	public void setUpMediumAliens() {
		// Determine the width and height of each alien being placed
		Alien tempAlien = new MediumAlien(0, 0);
		int alienWidth = tempAlien.getWidth();
		int alienHeight = tempAlien.getHeight();

		for (int i = 0; i < NUM_MEDIUM_ALIENS; i++) {
			// Set the starting positions of each of the aliens being placed
			int x = (int) (Math.random() * (FIELD_WIDTH - alienWidth - 7) + 1);
			int y = (int) (Math.random() * (FRAME_HEIGHT - alienHeight - 26 - lblShooter.getHeight() - 60));

			// Create a new 'Alien' object and add it to the 'aliens' ArrayList
			aliens.add(new MediumAlien(x, y));
		}
	}

	// Create and randomly place the appropriate number of LARGE aliens on the
	// playing field
	public void setUpLargeAliens() {
		// Determine the width and height of each alien being placed
		Alien tempAlien = new LargeAlien(0, 0);
		int alienWidth = tempAlien.getWidth();
		int alienHeight = tempAlien.getHeight();

		for (int i = 0; i < NUM_LARGE_ALIENS; i++) {
			// Set the starting positions of each of the aliens being placed
			int x = (int) (Math.random() * (FIELD_WIDTH - alienWidth - 7) + 1);
			int y = (int) (Math.random() * (FRAME_HEIGHT - alienHeight - 26 - lblShooter.getHeight() - 60));

			// Create a new 'Alien' object and add it to the 'aliens' ArrayList
			aliens.add(new LargeAlien(x, y));
		}
	}

	// This method will be called automatically whenever the Timer fires
	public void actionPerformed(ActionEvent event) {
		counter++;
		
		if (event.getActionCommand().equals("pause"))
		{
			if(timer.isRunning())
			{
				timer.stop();
			}else {
				timer.start();
			}
		}
		

		if (event.getActionCommand().equals("Missile Timer")) {
			if (missileCount < MAX_MISSILES) {
				if (missileCount < 19) {
					missileCount += 2;
					lblMissileCount.setText("Missiles: " + missileCount);
				} else {
					missileCount++;
					lblMissileCount.setText("Missiles: " + missileCount);
				}
			}
		}

		if (event.getActionCommand().equals("timer"))

			// Change the shooter's position if the player is pressing the left
			// or right arrow keys
			if (pressedLeft && shooterX > 4) {
				shooterX -= SHOOTER_SPEED;
				// shooterX = FIELD_WIDTH - lblShooter.getWidth() - 6 - 6;
			}
		if (pressedUp && shooterY > 0) {
			shooterY -= SHOOTER_SPEED;
			// shooterX = FIELD_WIDTH - lblShooter.getWidth() - 6 - 6;
		}

		if (pressedDown && shooterY < 410) {
			shooterY += SHOOTER_SPEED;
			// shooterX = FIELD_WIDTH - lblShooter.getWidth() - 6 - 6;
		}

		if (pressedRight && shooterX < FIELD_WIDTH - lblShooter.getWidth() - 6 - 4) {
			shooterX += SHOOTER_SPEED;
			// shooterX = 5;
		}

		lblShooter.setLocation(shooterX, shooterY);

		// Move the remaining aliens across the playing field
		for (int i = 0; i < aliens.size(); i++) {
			Alien alien = aliens.get(i);
			alien.moveAlien();
		}

		// Move the existing missiles up the playing field
		for (int j = 0; j < playerMissiles.size(); j++) {
			Missile missile = playerMissiles.get(j);
			missile.moveMissile();

			// If the missile gets past the top of the playing field, remove it
			if (missile.getY() < 0 - missile.getHeight()) {
				gameFrame.getContentPane().remove(missile.getMissileImage());
				playerMissiles.remove(j);
			}

		}

		for (int r = 0; r < alienMissiles.size(); r++) {
			Missile missile1 = alienMissiles.get(r);
			missile1.moveMissile();

			if (missile1.getY() > 600 + missile1.getHeight()) {
				gameFrame.getContentPane().remove(missile1.getMissileImage());
				alienMissiles.remove(r);
			}
		}

		// If the player has pressed the space bar, launch a missile; the variable
		// 'missileFired' prevents the player from holding down the space bar to
		// fire missiles continuously (by forcing the player to release the space
		// bar between firings)
		if (pressedSpace && !missileFired && missileCount > 0) {
			// Determine the width and height of the missile being launched
			Shooter_Missile tempMissile = new Shooter_Missile(0, 0);
			int missileWidth = tempMissile.getWidth();
			int missileHeight = tempMissile.getHeight();

			// Set the starting position of the missile being launched
			int x = shooterX + (lblShooter.getWidth() / 2) - (missileWidth / 2);
			int y = shooterY - lblShooter.getHeight() + 20;

			// Create a new 'Missile' object and add it to the 'missiles' ArrayList
			playerMissiles.add(new Shooter_Missile(x, y));

			missileFired = true;

			missileCount--;
			lblMissileCount.setText("Missiles: " + missileCount);
		}

		int ChanceSize = (NUM_SMALL_ALIENS + NUM_LARGE_ALIENS + NUM_MEDIUM_ALIENS) * 100;
		boolean alienMissileFired = false;
		MissileChance = (int) Math.random() * (NUM_SMALL_ALIENS + NUM_LARGE_ALIENS + NUM_MEDIUM_ALIENS);
		RandomAlien = (int) (Math.random() * aliens.size());

		if (counter == 100) {
			counter = 0;
			if ((0 <= MissileChance && MissileChance <= ChanceSize) && !alienMissileFired) {
				// Determine the width and height of the missile being launched
				Alien_Missile tempMissile = new Alien_Missile(0, 0);
				int missileWidth = tempMissile.getWidth();

				// Set the starting position of the missile being launched
				int x = aliens.get(RandomAlien).getX() + (aliens.get(RandomAlien).getWidth() / 2) - (missileWidth / 2);
				int y = aliens.get(RandomAlien).getY() + aliens.get(RandomAlien).getHeight();

				// Create a new 'Missile' object and add it to the 'missiles' ArrayList
				alienMissiles.add(new Alien_Missile(x, y));
				System.out.println("Alien missile created!");

				alienMissileFired = true;

				// missileCount--;
				lblMissileCount.setText("Missiles: " + missileCount);

			}
		}

		// Draw the aliens (all types)
		for (int i = 0; i < aliens.size(); i++) {
			Alien alien = aliens.get(i);
			JLabel aLabel = alien.getAlienImage();
			aLabel.setLocation(alien.getX(), alien.getY());
			aLabel.setSize(alien.getWidth(), alien.getHeight());
			gameFrame.add(aLabel);
		}

		// Draw the missiles
		for (int i = 0; i < alienMissiles.size(); i++) {
			Missile missile = alienMissiles.get(i);
			JLabel mLabel = missile.getMissileImage();
			mLabel.setLocation(missile.getX(), missile.getY());
			mLabel.setSize(missile.getWidth(), missile.getHeight());
			gameFrame.add(mLabel);
		}

		for (int i = 0; i < playerMissiles.size(); i++) {
			Missile missile = playerMissiles.get(i);
			JLabel mLabel = missile.getMissileImage();
			mLabel.setLocation(missile.getX(), missile.getY());
			mLabel.setSize(missile.getWidth(), missile.getHeight());
			gameFrame.add(mLabel);
		}

		// Redraw/Update the playing field
		gameFrame.repaint();

		checkCollisions();

		// This line synchronizes the graphics state by flushing buffers containing
		// graphics events and forcing the frame drawing to happen now; otherwise,
		// it can sometimes take a few extra milliseconds for the drawing to take
		// place, which can result in jerky graphics movement; this line ensures
		// that the display is up-to-date; it is useful for animation, since it can
		// reduce or eliminate flickering
		Toolkit.getDefaultToolkit().sync();
	}

	// For every alien and missile currently on the playing field, create a
	// "rectangle" around both the alien and the missile, and then check to
	// see if the two rectangles intersect each other
	public void checkCollisions() {
		// The 'try-catch' exception trapping is needed to prevent an error from
		// occurring when an element is removed from the 'aliens' and 'missiles'
		// ArrayLists, causing the 'for' loops to end prematurely
		for (int i = 0; i < aliens.size(); i++)
			for (int j = 0; j < playerMissiles.size(); j++)
				try {
					// int alienMissileCounter = 0;
					Rectangle rAlien = new Rectangle(aliens.get(i).getX(), aliens.get(i).getY(),
							aliens.get(i).getWidth(), aliens.get(i).getHeight());
					Rectangle rPlayerMissile = new Rectangle(playerMissiles.get(j).getX(), playerMissiles.get(j).getY(),
							playerMissiles.get(j).getWidth(), playerMissiles.get(j).getHeight());

					// If an alien and a missile intersect each other, remove both
					// of them from the playing field and the ArrayLists
					if (rAlien.intersects(rPlayerMissile)) {
						if (aliens.get(i).getType() == "Large") {
							gameFrame.getContentPane().remove(aliens.get(i).getAlienImage());
							aliens.remove(i);
							gameFrame.getContentPane().remove(playerMissiles.get(j).getMissileImage());
							playerMissiles.remove(j);
							score = score + 10;
							lblScore.setText("Score: " + score);
							gameFrame.repaint();
						}

						if (aliens.get(i).getType() == "Medium") {
							gameFrame.getContentPane().remove(aliens.get(i).getAlienImage());
							aliens.remove(i);
							gameFrame.getContentPane().remove(playerMissiles.get(j).getMissileImage());
							playerMissiles.remove(j);
							score = score + 5;
							lblScore.setText("Score: " + score);
							gameFrame.repaint();
						}

						if (aliens.get(i).getType() == "Small") {
							gameFrame.getContentPane().remove(aliens.get(i).getAlienImage());
							aliens.remove(i);
							gameFrame.getContentPane().remove(playerMissiles.get(j).getMissileImage());
							playerMissiles.remove(j);
							score = score + 1;
							lblScore.setText("Score: " + score);
							gameFrame.repaint();
						}

						if (missileCount < MAX_MISSILES) {
							if (missileCount < 19)
								missileCount += 2;
							lblMissileCount.setText("Missiles: " + missileCount);
						} else {
							missileCount++;
							lblMissileCount.setText("Missiles: " + missileCount);
						}
					}

				} catch (Exception error) {
				}

		for (int j = 0; j < alienMissiles.size(); j++) {
			try {
				System.out.println("Spot #1");
				// int alienMissileCounter = 0;

				Rectangle rPlayer = new Rectangle(lblShooter.getX(), lblShooter.getY(), lblShooter.getWidth(),
						lblShooter.getHeight());
				Rectangle rAlienMissile = new Rectangle(alienMissiles.get(j).getX(), alienMissiles.get(j).getY(),
						alienMissiles.get(j).getWidth(), alienMissiles.get(j).getHeight());
				System.out.println("Spot #2");

				if (rAlienMissile.intersects(rPlayer)) {
					System.out.println("alien missile intersects player");
					gameFrame.getContentPane().remove(alienMissiles.get(j).getMissileImage());
					alienMissiles.remove(j);
					playerLives--;
					lblPlayerLives.setText("Lives: " + playerLives);
					gameFrame.repaint();

				}

			} catch (Exception error) {
			}
		}
		for (int i = 0; i < playerMissiles.size(); i++)
			for (int j = 0; j < alienMissiles.size(); j++)
				try {

					Rectangle rPlayerMissile = new Rectangle(playerMissiles.get(i).getX(), playerMissiles.get(i).getY(),
							playerMissiles.get(i).getWidth(), playerMissiles.get(i).getHeight());
					Rectangle rAlienMissile = new Rectangle(alienMissiles.get(j).getX(), alienMissiles.get(j).getY(),
							alienMissiles.get(j).getWidth(), alienMissiles.get(j).getHeight());

					if (rAlienMissile.intersects(rPlayerMissile)) {
						System.out.println("alien Missile Collides with player missile");
						gameFrame.getContentPane().remove(alienMissiles.get(j).getMissileImage());
						alienMissiles.remove(j);
						gameFrame.getContentPane().remove(playerMissiles.get(i).getMissileImage());
						playerMissiles.remove(i);
						gameFrame.repaint();

					}

				} catch (Exception error) {
				}

		for (int j = 0; j < aliens.size(); j++)
			try {

				Rectangle rPlayer = new Rectangle(lblShooter.getX(), lblShooter.getY(), lblShooter.getWidth(),
						lblShooter.getHeight());
				Rectangle rAlien = new Rectangle(aliens.get(j).getX(), aliens.get(j).getY(), aliens.get(j).getWidth(),
						aliens.get(j).getHeight());

				if (rAlien.intersects(rPlayer)) {
					if (aliens.get(j).getType() == "Large") {

						score = score + 10;
						lblScore.setText("Score: " + score);

					}

					if (aliens.get(j).getType() == "Medium") {

						score = score + 5;
						lblScore.setText("Score: " + score);
					}

					if (aliens.get(j).getType() == "Small") {

						score = score + 1;
						lblScore.setText("Score: " + score);

					}

					gameFrame.getContentPane().remove(aliens.get(j).getAlienImage());
					aliens.remove(j);
					playerLives--;
					lblPlayerLives.setText("Lives: " + playerLives);
					gameFrame.repaint();

				}

			} catch (Exception error) {
			}

		// If all of the aliens have been destroyed, the game is over, so stop
		// the Timer and remove any remaining missiles from the playing field
		if (playerLives == 0)
		{
			timer.stop();
			missileTimer.stop();
			gameFrame.getContentPane().removeAll();
			playerMissiles.removeAll(playerMissiles);
			alienMissiles.removeAll(alienMissiles);

			// Display the "Game Over" JLabel
			gameFrame.add(lblGameOver);
			lblGameOver.setText("Game Over. You have ran out of lives and have been exterminated.");
			lblGameOver.setVisible(true);

			gameFrame.repaint();
		}
		
		if (aliens.size() == 0)
		{
			timer.stop();
			missileTimer.stop();
			gameFrame.getContentPane().removeAll();
			playerMissiles.removeAll(playerMissiles);
			alienMissiles.removeAll(alienMissiles);

			// Display the "Game Over" JLabel
			gameFrame.add(lblGameOver);
			lblGameOver.setText("The Aliens Have Been Exterminated. Final Score: " + score);
			lblGameOver.setVisible(true);

			gameFrame.repaint();
		}
		for (int j = 0; j < aliens.size(); j++)
		{
			if (aliens.get(j).getY() > 460)
			{
				timer.stop();
				missileTimer.stop();
				gameFrame.getContentPane().removeAll();
				playerMissiles.removeAll(playerMissiles);
				alienMissiles.removeAll(alienMissiles);

				// Display the "Game Over" JLabel
				gameFrame.add(lblGameOver);
				lblGameOver.setText("Game Over. The Aliens Went Of the Screen.");
				lblGameOver.setVisible(true);

				gameFrame.repaint();
			}
	
		}
	}

	// See if the player has PRESSED a key
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();

		if (key == KeyEvent.VK_CONTROL) // CONTROL key
			controlKeyPressed = true;

		if (key == KeyEvent.VK_ESCAPE) // CONTROL key
		{
			if (timer.isRunning()) {
				missileTimer.stop();
				timer.stop();
			} else {
				missileTimer.start();
				timer.start();
			}
		}

		if ((key == 88) && (controlKeyPressed)) // CONTROL + X
		{
			gameFrame.dispose();
			System.exit(0);
		}

		if (key == KeyEvent.VK_LEFT) // LEFT arrow
			pressedLeft = true;
		if (key == KeyEvent.VK_RIGHT) // RIGHT arrow
			pressedRight = true;
		if (key == KeyEvent.VK_UP) // LEFT arrow
			pressedUp = true;
		if (key == KeyEvent.VK_DOWN) // RIGHT arrow
			pressedDown = true;

		if (key == KeyEvent.VK_SPACE) // SPACE bar
			pressedSpace = true;
	}

	// See if the player has RELEASED a key
	public void keyReleased(KeyEvent event) {
		int key = event.getKeyCode();

		if (key == KeyEvent.VK_CONTROL) // CONTROL key
			controlKeyPressed = false;

		if (key == KeyEvent.VK_LEFT) // LEFT arrow
			pressedLeft = false;
		if (key == KeyEvent.VK_UP) // LEFT arrow
			pressedUp = false;
		if (key == KeyEvent.VK_RIGHT) // RIGHT arrow
			pressedRight = false;
		if (key == KeyEvent.VK_DOWN) // LEFT arrow
			pressedDown = false;

		if (key == KeyEvent.VK_SPACE) // SPACE bar
		{
			pressedSpace = false;
			missileFired = false;
		}
	}

	public void keyTyped(KeyEvent event) {
	}

	public void SoundPlayLoopStopWithWAV() {
		try {
			audioClip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("theme_song.wav"));
			audioClip.open(inputStream);
			audioClip.start();
		}

		catch (Exception e) {
		}
	}
}