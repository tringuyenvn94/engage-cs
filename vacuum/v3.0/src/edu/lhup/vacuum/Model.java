package edu.lhup.vacuum;

import java.util.*;
import java.io.*;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

public class Model extends Observable
{
	public static final int STINK_RANGE = 3;
	
	public static final int EMPTY = 0;
	public static final int DIRTY = 1;
	public static final int WALL = 2;
	public static final int RADAR_UP = 3;
	public static final int RADAR_DOWN = 4;
	public static final int RADAR_LEFT = 5;
	public static final int RADAR_RIGHT = 6;
	public static final int JESSFILE = 7;
	public static final int SOARFILE = 8;
	public static final int JAVAFILE = 9;

	private static final int INIT_W = 4;
	private static final int INIT_H = 4;
	private static final int INIT_X = 0;
	private static final int INIT_Y = 0;
	private static final int INIT_DIRTY = 10;
	private static final int INIT_STEPS = 100;
	private static final int INIT_DELAY = 500;
	
	private int m_w = INIT_W;
	private int m_h = INIT_H;
	private int m_x = INIT_X;
	private int m_y = INIT_Y;
	private int m_dirtyCount = INIT_DIRTY;
	private int m_totalSteps = INIT_STEPS;
	private int m_delay = INIT_DELAY;

	private int[][] m_dirty;

	private String m_file;
	private String m_shortFile;

	private boolean m_halt = true;
	private int m_stepCount = m_totalSteps;
	private int m_score = 0;

	private boolean m_bPenalizeForMovement = true;

	private static final Random m_rand = new Random();

	private final Runnable m_thread = new NotifyThread();
	private Runnable m_mainThread;
	private Runnable m_eventThread;
	
	private int m_fileType = JESSFILE;
	
	private boolean m_bAllowState = true;
	private boolean m_bRadarSensor = true;

	public Model()
	{
		m_mainThread = Thread.currentThread();
	}

	public void init()
	{
		m_dirty = new int[m_w][m_h];
		m_score = 0;
		m_stepCount = m_totalSteps;
		m_halt = true;

		for (int i = 0; i < m_dirtyCount; i++)
		{
			int dx = m_rand.nextInt(m_w);
			int dy = m_rand.nextInt(m_h);
			while (m_dirty[dx][dy] == DIRTY)
			{
				dx = m_rand.nextInt(m_w);
				dy = m_rand.nextInt(m_h);
			}
			m_dirty[dx][dy] = DIRTY;
		}

		setChanged();
		notifyObservers2();
	}

	public void init(String f) throws Exception
	{
		int prevW = m_w;
		int prevH = m_h;
		int prevDirtyCount = m_dirtyCount;
		int prevX = m_x;
		int prevY = m_y;

		m_x = -1;
		m_y = -1;
		m_dirtyCount = 0;

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(f));
			List<String> lines = new ArrayList<String>();
			String line = "";
			while ((line = br.readLine()) != null)
			{ 
				line = line.trim();
				if (line.length() != 0)
					lines.add(line); 
			}

			m_h = lines.size();
			m_w = (lines.get(0).toString().length() + 1) / 2;

			if (m_h <= 0 || m_w <= 0)
				throw new IllegalStateException("Invalid width or height!");

			m_dirty = new int[m_w][m_h];

			int y = 0;
			for (Iterator<?> i = lines.iterator(); i.hasNext(); )
			{
				line = (String)i.next();
				if (((line.length() + 1) / 2) != m_w)
					throw new 
						IllegalStateException("Width must be consistent!");

				String[] square = line.split(" ");

				for (int x = 0; x < m_w; x++)
				{
					if (square[x].equals("*"))
					{
						m_dirty[x][y] = DIRTY;
						m_dirtyCount++;
					}
					else if (square[x].equalsIgnoreCase("v"))
					{
						m_x = x;
						m_y = y;	
					}
					else if (square[x].equalsIgnoreCase("x"))
					{
						m_dirty[x][y] = DIRTY;
						m_dirtyCount++;
						m_x = x;
						m_y = y;	
					}
				}
				y++;
			}

			if (m_x == -1 || m_y == -1)
				throw new 
					IllegalStateException("Agent location not specified!");

			setChanged();
			notifyObservers2();
		}
		catch (Exception e)
		{
			m_w = prevW;
			m_h = prevH;
			m_dirtyCount = prevDirtyCount;
			m_x = prevX;
			m_y = prevY;
			init();
			throw e;
		}
		finally
		{
			try { br.close(); } catch (Exception e) {}
		}
	}


	public void init(int w, int h, int dirtyCount)
	{
		if (w < 0)
			throw new IllegalArgumentException("Invalid starting W");
		if (h < 0)
			throw new IllegalArgumentException("Invalid starting h");
		if ( (dirtyCount > (w*h)) || (dirtyCount < 0) )
			throw new IllegalArgumentException
				("Invalid initial number of dirty squares");

		m_w = w;
		m_h = h;
		m_x = m_rand.nextInt(w);
		m_y = m_rand.nextInt(h);
		m_dirtyCount = dirtyCount;
		init();
	}

	public void setEventThread(Runnable t)
	{ m_eventThread = t; }

	public boolean isDirty()
	{ return isDirty(m_x, m_y);	}

	public boolean isDirty(int x, int y)
	{ return m_dirty[x][y] == DIRTY; }
	
	public int radar(int dir) throws CheatException
	{ 
		if (getRadarSensor() == false)
			throw new CheatException("Radar is not enabled!");
		
		int ret = WALL;
		
		int x = m_x;
		int y = m_y;
		if (dir == RADAR_DOWN)
			y = y + 1;
		else if (dir == RADAR_UP)
			y = y - 1;
		else if (dir == RADAR_RIGHT)
			x = x + 1;
		else 
			x = x - 1;
			
		if (x >= 0 && x < m_w && y >= 0 && y < m_h)
		{
			ret = m_dirty[x][y];
		}

		return ret;
	}
	
	
	public String getAgentFile()
	{ return m_file; }

	public String getAgentShortFile()
	{ return m_shortFile; }

	public int getDirtyCount()
	{ return m_dirtyCount; }

	public boolean isAgentStopped()
	{ return m_halt; }

	public int getStepCount()
	{ return m_stepCount; }

	public int getDelay()
	{ return m_delay; }

	public int agentX()
	{ return m_x; }

	public int agentY()
	{ return m_y; }

	public int getWidth()
	{ return m_w; }

	public int getHeight()
	{ return m_h; }

	public int getScore()
	{ return m_score; }

	public int getTotalSteps()
	{ return m_totalSteps; }

	public int getCleanSquareCount()
	{
		int count = 0;
		for (int y = 0; y < m_h; y++)
		{
			for (int x = 0; x < m_w; x++)
			{
				if (m_dirty[x][y] != DIRTY)
					count++;
			}
		}
		return count;
	}

	public int getType()
	{
		return m_fileType;
	}

	public boolean getAllowState()
	{
		return m_bAllowState;
	}
	
	public boolean getRadarSensor()
	{
		return m_bRadarSensor;
	}
	
// mutables 

	public void setAllowState(boolean b)
	{
		m_bAllowState = b;
	}

	public void setRadarSensor(boolean b)
	{
		m_bRadarSensor = b;
	}
	
	public void suck()
	{
		m_dirty[m_x][m_y] = EMPTY;
		setChanged();
		notifyObservers2();
	}

	public boolean left()
	{
		boolean bump = true;
		if (m_x > 0)
		{
			m_x--;
			bump = false;
		}

		if (m_bPenalizeForMovement)
		{
			m_score--;
		}

		setChanged();
		notifyObservers2();

		return bump;
	}

	public boolean right()
	{
		boolean bump = true;
		if (m_x < (m_w-1))
		{
			m_x++;
			bump = false;
		}

		if (m_bPenalizeForMovement)
		{
			m_score--;
		}

		setChanged();
		notifyObservers2();

		return bump;
	}

	public boolean up()
	{
		boolean bump = true;
		if (m_y > 0)
		{
			m_y--;
			bump = false;
		}

		if (m_bPenalizeForMovement)
		{
			m_score--;
		}

		setChanged();
		notifyObservers2();

		return bump;
	}

	public boolean down()
	{
		boolean bump = true;	
		if (m_y < (m_h-1))
		{
			m_y++;
			bump = false;
		}

		if (m_bPenalizeForMovement)
		{
			m_score--;
		}

		setChanged();
		notifyObservers2();

		return bump;
	}

	public void setAgentFile(String file, int type)
	{ 
		m_fileType = type;
		m_file = file; 
		setChanged();
		notifyObservers2();
	}

	public void setAgentShortFile(String file)
	{ 
		m_shortFile = file; 
		setChanged();
		notifyObservers2();
	}

	public void setPenalizeForMovement(boolean bPenalize)
	{
		m_bPenalizeForMovement = bPenalize;
		setChanged();
		notifyObservers2();
	}
	
	public boolean getPenalizeForMovement()
	{ 
		return m_bPenalizeForMovement;
	}

	public void haltAgent()
	{ 
		m_halt = true; 
		setChanged();
		notifyObservers2();
	}

	public void startAgent()
	{ 
		m_halt = false; 
		setChanged();
		notifyObservers2();
	}

	public void resetStepCount()
	{
		m_stepCount = m_totalSteps; 
		setChanged();
		notifyObservers2();
	}

	public void decStepCount()
	{ 
		m_stepCount--; 
		setChanged();
		notifyObservers2();
	}

	public void setTotalSteps(int steps)
	{ 
		m_totalSteps = steps; 
		m_stepCount = steps;
		setChanged();
		notifyObservers2();
	}

	public void setDelay(int delay)
	{ 
		m_delay = delay; 
		setChanged();
		notifyObservers2();
	}

	public void tallyScore()
	{ 
		m_score += getCleanSquareCount();
		setChanged();
		notifyObservers2();
	}

	public void resetScore()
	{
		m_score = 0;
		setChanged();
		notifyObservers2();
	}

	public int calculateStink(int dir)
	{
		int stink = 0;
		int mult = STINK_RANGE;
		if (dir == RADAR_LEFT)
		{
			for (int x = (m_x-1); x >= 0; x--)
			{
				stink += (mult * m_dirty[x][m_y]);
				mult = (mult == 0) ? 0 : mult-1;
			}
		}
		else if (dir == RADAR_RIGHT)
		{
			for (int x = (m_x+1); x < m_w; x++)
			{
				stink += (mult * m_dirty[x][m_y]);
				mult = (mult == 0) ? 0 : mult-1;
			}
		}
		else if (dir == RADAR_UP)
		{
			for (int y = (m_y-1); y >= 0; y--)
			{
				stink += (mult * m_dirty[m_x][y]);
				mult = (mult == 0) ? 0 : mult-1;
			}
		}
		else if (dir == RADAR_DOWN)
		{
			for (int y = (m_y+1); y < m_h; y++)
			{
				stink += (mult * m_dirty[m_x][y]);
				mult = (mult == 0) ? 0 : mult-1;
			}
		}
		return stink;
	}
	
	private void notifyObservers2()
	{
		try
		{
			if ( (m_mainThread != Thread.currentThread()) &&
	 			 (m_eventThread != Thread.currentThread())	)
			{
				SwingUtilities.invokeAndWait(m_thread);
			}
			else
			{
				m_thread.run();
			}
		}
		catch  (InvocationTargetException e)
		{
		  	throw new IllegalStateException("Error notifying observers!");
		}
		catch  (InterruptedException e)
		{
			throw new IllegalStateException("Error notifying view! (Deadlock?)");
		}
	}

	private class NotifyThread implements Runnable
	{
		public void run()
		{ 
			notifyObservers(); 
		}
	}

}