package edu.lhup.vacuum;

import java.awt.*;
import javax.swing.*;

public class EnvSquare extends JPanel
{
	public static final long serialVersionUID = 1;
	
	
	private ImageIcon m_icon; 
	
	private static final int AGENT_W = 40;
	private static final int AGENT_H = 40;
	private static final Dimension m_dim = new Dimension(100,100);

	private boolean m_bAgent = false;

	public EnvSquare(boolean dirty)
	{
		if (getClass().getResource("/vacuum.gif") != null)
		{
			m_icon = new ImageIcon(getClass().getResource("/vacuum.gif"));			
		}
		else
		{
			m_icon = new ImageIcon("vacuum.gif");			
		}

		setDirty(dirty);
	}

	public void setDirty(boolean dirty)
	{
		if (dirty)
			setBackground(Color.GRAY);
		else
			setBackground(Color.WHITE);
	}

	public void drawAgent(boolean bAgent)
	{
		m_bAgent = bAgent;		
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawRect(0, 0, getWidth(), getHeight());

		if (m_bAgent)
		{
			Image img = m_icon.getImage();
			g.drawImage(img, (int) ((getWidth()/2.0)-(AGENT_W/2.0)), (int) ((getHeight()/2.0)-(AGENT_H/2.0)), null);
		}
/*			g.drawRect((int) ((getWidth()/2.0)-(AGENT_W/2.0)), 
					   (int) ((getHeight()/2.0)-(AGENT_H/2.0)), 
					   AGENT_W, 
					   AGENT_H); */			
	}
	public Dimension getPreferredSize()
	{
		return m_dim;
	}
}
		
