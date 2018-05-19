import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MediumAlien implements Alien
{
	// Constant
	private final int ALIEN_SPEED = 3;

	private int alienWidth;
	private int alienHeight;
	private int alienXPos;
	private int alienYPos;
	private int alienMoveDirection = 1; // 1 = move right, 2 = move left

	private ImageIcon alienImage = new ImageIcon(getClass().getResource("medium_alien.gif"));
	private JLabel alienLabel = new JLabel(alienImage);

	// Constructor
	public MediumAlien(int xPos, int yPos)
	{
		alienWidth = alienImage.getIconWidth();
		alienHeight = alienImage.getIconHeight();
		alienXPos = xPos;
		alienYPos = yPos;
	}

	public String getType()
	{
		return "Medium";
	}

	// Move the alien 'ALIEN_SPEED' pixels in the appropriate direction; if the
	//  alien has reached the left or right edge of the field, reverse the
	//  direction of the alien
	public void moveAlien()
	{
		if ((alienXPos <= 0) || (alienXPos + alienWidth + 6 >= Game.FIELD_WIDTH))
			{
			alienMoveDirection = 3 - alienMoveDirection;
			alienYPos = alienYPos + alienHeight;
			}

		if (alienMoveDirection == 1)
			alienXPos += ALIEN_SPEED;
		else
			alienXPos -= ALIEN_SPEED;
	}

	public JLabel getAlienImage()
	{
		return alienLabel;
	}

	public int getWidth()
	{
		return alienWidth;
	}

	public int getHeight()
	{
		return alienHeight;
	}

	public int getX()
	{
		return alienXPos;
	}

	public int getY()
	{
		return alienYPos;
	}
}