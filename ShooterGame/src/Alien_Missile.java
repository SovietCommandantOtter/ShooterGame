

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Alien_Missile implements Missile
{
	// Constant
	private final int MISSILE_SPEED = 1;

	private int missileWidth;
	private int missileHeight;
	private int missileXPos;
	private int missileYPos;

	private ImageIcon missileImage = new ImageIcon(getClass().getResource("bomb.gif"));
	private JLabel missileLabel = new JLabel(missileImage);

	// Constructor
	public Alien_Missile(int xPos, int yPos)
	{
		missileWidth = missileImage.getIconWidth();
		missileHeight = missileImage.getIconHeight();
		missileXPos = xPos;
		missileYPos = yPos;
	}

	// Move the missile 'MISSILE_SPEED' pixels up the playing field
	public void moveMissile()
	{
		missileYPos += MISSILE_SPEED;
	}

	public JLabel getMissileImage()
	{
		return missileLabel;
	}

	public int getWidth()
	{
		return missileWidth;
	}

	public int getHeight()
	{
		return missileHeight;
	}

	public int getX()
	{
		return missileXPos;
	}

	public int getY()
	{
		return missileYPos;
	}

	public String getType()
	{
		return "Alien_Missile";
	}
}