package swarm;

import java.util.Random;

abstract public class AntBase implements Runnable 
{
	private Location m_loc;
	private Model m_model;
	private boolean m_stop = false;
	
	private static Random m_rand = new Random();
	
	public AntBase()
	{ }
	
	public void setModel(Model m)
	{
		m_model = m;
		m_loc = m_model.getNestLocation();
	}
	
	public void stopThisCrazyAnt()
	{ m_stop = true; }
	
	public Location getLocation()
	{ return m_loc; }

	public void setLocation(Location l)
	{ m_loc = l; }
	
	public Model getModel()
	{ return m_model; }
	
	public Random getRand()
	{ return m_rand; }
	
	public void run() 
	{
		while (!m_stop)
		{
			makeChoice();
			
			try
			{
				Thread.sleep(m_rand.nextInt(800) + 200);
				Thread.yield();
			}
			catch (InterruptedException e)
			{
				
			}
		}
		System.out.println("Dead ant");
	}

	abstract protected void makeChoice();
}
