package swarm;

import java.awt.*;

public class Food extends Square 
{
	public Food()
	{
		setBackground(Color.green);
		setOpaque(true);
		//setBorder(BorderFactory.createLineBorder(Color.black, 1));
	}
	
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
	}
	
	public void update() {};	
}
