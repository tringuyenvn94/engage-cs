package swarm;

import java.awt.*;
import javax.swing.*;

abstract public class Square extends JPanel
{
	private int m_pheromone = 0;
	private int m_ants = 0;
	
	protected static String m_mutext = "MUTEXT";
	
	public Square()
	{
		setPreferredSize(new Dimension(22,22));
	}

	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
	}
	
	public void visit()
	{
		synchronized (this)
		{
			m_pheromone++;
			m_ants++;
		}
		update();
	}
	
	public void leave()
	{
		synchronized (this)
		{
			m_ants--;
		}
		update();
	}
	
	public int getPheromone()
	{ 
		return m_pheromone; 
	}
	
	public int getAnts()
	{ return m_ants; } 
	
	abstract public void update();
}
