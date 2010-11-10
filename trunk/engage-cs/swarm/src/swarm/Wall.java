package swarm;

import java.awt.*;

public class Wall extends Square 
{
	public Wall()
	{
		setBackground(Color.black);
		setOpaque(true);
	}
	
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
	}
	
	public void update() {};
}
