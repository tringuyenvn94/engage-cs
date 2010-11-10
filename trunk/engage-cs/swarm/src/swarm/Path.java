package swarm;

import java.awt.*;
import javax.swing.SwingUtilities;

public class Path extends Square 
{
	private Updater m_updater = new Updater();
	
	private Color m_color = Color.white;
	public Path()
	{
		setBackground(m_color);
		setOpaque(true);
	}
	
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawString(getAnts() + "", 5, 20);
	}
	
	public void update() 
	{
		try
		{
			int p = 0;
			synchronized (this)
			{
				p = (getPheromone() < 510 ? getPheromone(): 510);
			}
			m_updater.m_p = p;			
			SwingUtilities.invokeAndWait(m_updater);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public class Updater implements Runnable
	{
		public int m_p = 0;
		
		public void run()
		{
			m_color	= new Color(255, 255-(m_p/2), 255-(m_p/2));
			setBackground(m_color);

			invalidate();		
			repaint();
		}
	}
}
