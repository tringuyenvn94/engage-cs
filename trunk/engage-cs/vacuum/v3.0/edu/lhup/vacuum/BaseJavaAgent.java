package edu.lhup.vacuum;

import javax.swing.*;

import java.io.PrintStream;
import java.util.Random;

public abstract class BaseJavaAgent implements IView
{
	private Model m_model;
	private Random m_rand = new Random();
	private boolean m_bMoved = false;

	public BaseJavaAgent()
	{
	}	

	public void setModel(Model m)
	{
		m_model = m;
	}

	protected boolean isCurrentSquareDirty()
	{
		return m_model.isDirty();
	}

	protected String radar(String dir)
	{
		dir = dir.toUpperCase();
		String ret = "CLEAN";
		try
		{
			int mdir = -100;
			if (dir == "UP")
				mdir = Model.RADAR_UP;
			else if (dir == "DOWN")
				mdir = Model.RADAR_DOWN;				
			else if (dir == "LEFT")
				mdir = Model.RADAR_LEFT;
			else 
				mdir = Model.RADAR_RIGHT;				
			
			if (m_model.radar(mdir) == Model.DIRTY)
				ret = "DIRTY";
			else if (m_model.radar(mdir) == Model.EMPTY)
				ret = "CLEAN";
			else 
				ret = "WALL";
		}
		catch (CheatException e)
		{
			JOptionPane.showMessageDialog(Main.MAIN_WIN, "Error: Your agent cannot access radar!");
		}
		return ret;
	}

	protected int stink(String dir)
	{
		dir = dir.toUpperCase();
		int stink = 0;

		int mdir = -100;
		if (dir == "UP")
			mdir = Model.RADAR_UP;
		else if (dir == "DOWN")
			mdir = Model.RADAR_DOWN;				
		else if (dir == "LEFT")
			mdir = Model.RADAR_LEFT;
		else 
			mdir = Model.RADAR_RIGHT;				
		
		stink = m_model.calculateStink(mdir);
		return stink;
	}
	
	protected void cleanCurrentSquare()
	{
		if (!m_bMoved)
			m_model.suck();
		m_bMoved = true;
	}

	protected void moveUp()
	{
		if (!m_bMoved)
			m_model.up();
		m_bMoved = true;
	}
	
	protected void moveDown()
	{
		if (!m_bMoved)
			m_model.down();
		m_bMoved = true;
	}
	
	protected void moveLeft()
	{
		if (!m_bMoved)
			m_model.left();
		m_bMoved = true;
	}

	protected void moveRight()
	{
		if (!m_bMoved)
			m_model.right();
		m_bMoved = true;
	}
	
	protected int pickRandomNumber(int fromInclusive, int toInclusive)
	{
		return m_rand.nextInt((toInclusive-fromInclusive)+1) + fromInclusive;
	}

	protected int getX()
	{
		return m_model.agentX();
	}
	
	protected int getY()
	{
		return m_model.agentY();
	}
	
	protected void dumpState(PrintStream out)
	{
		out.println("x: " + getX());
		out.println("y: " + getY());
		
		out.println("");
		
		out.println("Radar right: " + radar("RIGHT"));
		out.println("Radar left: " + radar("LEFT"));
		out.println("Radar up: " + radar("UP"));
		out.println("Radar down: " + radar("DOWN"));

		out.println("");
		
		out.println("Stink right: " + stink("RIGHT"));
		out.println("Stink left: " + stink("LEFT"));
		out.println("Stink up: " + stink("UP"));
		out.println("Stink down: " + stink("DOWN"));
		
		out.println("");
	}
	
	public void init()
	{
		m_bMoved = false;
		m_model.resetStepCount();
		m_model.resetScore();
		m_model.haltAgent();
	}

	public void run()
	{
		m_model.startAgent();
		try
		{
			while((m_model.getStepCount() > 0) && !m_model.isAgentStopped()) 
			{
				preStep();

				go();
				
				postStep();

				try 
				{ Thread.sleep(m_model.getDelay()); }
				catch (InterruptedException ue)
				{ System.out.println(ue); }
			}

			if (!m_model.isAgentStopped())
			{ 
				JOptionPane.showMessageDialog(Main.MAIN_WIN, 
					"Final Score: " + m_model.getScore(),
					"Score", 
					JOptionPane.INFORMATION_MESSAGE);

				init(); 
			}
		}
		catch (CheatException e)
		{
			JOptionPane.showMessageDialog(Main.MAIN_WIN, "Error: Your agent cannot access radar!");
		}
		finally
		{
			m_model.haltAgent();
		}
	}

	protected void preStep()
	{
		m_model.tallyScore();
		System.out.println("Step #" + m_model.getStepCount());
		m_model.decStepCount();
		m_bMoved = false;
	}

	protected void postStep()
	{
		m_bMoved = false;
	}

	protected abstract void go() throws CheatException; 
}
