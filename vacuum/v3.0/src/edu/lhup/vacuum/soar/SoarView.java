package edu.lhup.vacuum.soar;

import java.util.*;

import javax.swing.JOptionPane;

import edu.lhup.vacuum.CheatException;
import edu.lhup.vacuum.IView;
import edu.lhup.vacuum.Main;
import edu.lhup.vacuum.Model;

import sml.*;


public class SoarView implements IView
{
	private static Random m_rand = new Random();
	
	private static final String SPOT_FACT_NAME = "vacuum.types.spot";
	private static final String POS_FACT_NAME = "vacuum.types.position";
	private static final String ACTION_FACT_NAME = "vacuum.types.action";
	private static final String RADAR_FACT_NAME = "vacuum.types.radar";
	private static final String STINK_FACT_NAME = "vacuum.types.stink";
	
	private Identifier m_spotFact;
	private Identifier m_posFact;
	private Identifier m_leftRadarFact;		
	private Identifier m_rightRadarFact;
	private Identifier m_upRadarFact;
	private Identifier m_downRadarFact;	

	private Identifier m_leftStinkFact;		
	private Identifier m_rightStinkFact;
	private Identifier m_upStinkFact;
	private Identifier m_downStinkFact;	
	
	private Model m_model;
	
    private Kernel m_kernel;
    private Agent m_agent;
	
	public SoarView()
	{
		
	}	
	
	public void setModel(Model m)
	{
		m_model = m;
	}

	public void init()
	{
		m_model.resetStepCount();
		m_model.resetScore();
		m_model.haltAgent();
	}

	public void run()
	{
		m_model.startAgent();
		if (m_model.getAllowState() == false)
		{
			JOptionPane.showMessageDialog(Main.MAIN_WIN, 
					"Soar agents require stae.  Be sure that Model Based Reflex Agent is checked in the options menu!", 
					"Error!", 
					JOptionPane.ERROR_MESSAGE);
			m_model.haltAgent();			
			return;
		}
		
		try
		{
	        try 
	        {
	            m_kernel = Kernel.CreateKernelInNewThread("SoarKernelSML");
	        }
	        catch (Exception e)
	        {
				JOptionPane.showMessageDialog(Main.MAIN_WIN, 
						"Error loading the Soar engine!\n" + 
						"Please restart the environment with ElementXML.dll, SoarKernelSML.dll, and Java_sml_ClientInterface.dll\n" + 
						"included in current directory or the system path.\n" + 
						"(NOTE: these files are included in the Soar distribution)",
						"Error!", 
						JOptionPane.ERROR_MESSAGE);
				return;
	        }
	        
	        if (m_kernel.HadError())
	        {
	        	System.out.println("Error creating kernel: " + m_kernel.GetLastErrorDescription()) ;
	            System.exit(1);
	        }
	        
	        m_kernel.AddRhsFunction("rand", this, "rand", this);
	        m_agent = m_kernel.CreateAgent("vacuumAgent");
	        boolean load = m_agent.LoadProductions(m_model.getAgentFile());
	        if (!load || m_agent.HadError()) 
	        {
	            throw new IllegalStateException("Error loading productions: "
	                    + m_agent.GetLastErrorDescription());
	        }

	        // Register for the event we'll use to update the world
	        registerForUpdateWorldEvent() ;

	        initFacts();
			
			m_kernel.RunAllAgentsForever();
			
			JOptionPane.showMessageDialog(Main.MAIN_WIN, 
				"Final Score: " + m_model.getScore(),
				"Score", 
				JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(Main.MAIN_WIN, 
					e.toString(),
					"Soar Error", 
					JOptionPane.ERROR_MESSAGE);
			System.out.println(e);
		}
		finally
		{
			// clean up no matter what happens...
			init(); 
			
			if (m_kernel != null)
			{
				m_kernel.DestroyAgent(m_agent);
	    		m_kernel.Shutdown();
	    		m_kernel.delete();
			}
	        m_agent = null;
	        m_kernel = null;
	        m_spotFact = null;
	        m_posFact = null;
	        m_leftRadarFact = null;
	        m_rightRadarFact = null;
	        m_upRadarFact = null;
	        m_downRadarFact = null;
	        m_leftStinkFact = null;
	        m_rightStinkFact = null;
	        m_upStinkFact = null;
	        m_downStinkFact = null;

		}
	}

	protected void preStep()
	{
		m_model.tallyScore();
//		System.out.println("Step #" + m_model.getStepCount());
		m_model.decStepCount();
	}

	protected void handleAction()
	{
		if (m_agent.Commands())
		{
	        Identifier command = m_agent.GetCommand(0);
	        if (command.GetCommandName().equals(ACTION_FACT_NAME)) 
	        {
        		//boolean oldDirt = m_model.isDirty();
        		//int oldX = m_model.agentX();
        		//int oldY = m_model.agentY();
	        	
	        	String action = command.GetParameterValue("move");
	        	if (action != null && action.equals("suck"))
	        		m_model.suck();
	        	else if (action.equals("up"))
	        		m_model.up();
        		else if (action.equals("down"))
        			m_model.down();
        		else if (action.equals("left"))
        			m_model.left();
        		else if (action.equals("right"))
        			m_model.right();

        		//boolean newDirt = m_model.isDirty();
        		//int newX = m_model.agentX();
        		//int newY = m_model.agentY();
        		
		        command.AddStatusComplete();
		        modifyFacts();
	        }
	        m_agent.ClearOutputLinkChanges();
		}
	}

	protected void postStep() 
	{
	}
	
	protected void initFacts()
	{
		modifyFacts();
	}
	
	protected void modifyFacts()
	{
    	if (m_spotFact != null)
    		m_agent.DestroyWME(m_spotFact);
        m_spotFact = m_agent.CreateIdWME(m_agent.GetInputLink(), SPOT_FACT_NAME);
       	m_agent.CreateStringWME(m_spotFact, "status", m_model.isDirty() ? "dirty" : "clean");

       	if (m_posFact != null)
    		m_agent.DestroyWME(m_posFact);
        m_posFact = m_agent.CreateIdWME(m_agent.GetInputLink(), POS_FACT_NAME);
       	m_agent.CreateIntWME(m_posFact, "x", m_model.agentX());
       	m_agent.CreateIntWME(m_posFact, "y", m_model.agentY());

		if (m_model.getRadarSensor())
		{
			try
			{
				if (m_leftRadarFact != null)
				{
					m_agent.DestroyWME(m_leftRadarFact);
					m_agent.DestroyWME(m_rightRadarFact);
					m_agent.DestroyWME(m_upRadarFact);
					m_agent.DestroyWME(m_downRadarFact);

					m_agent.DestroyWME(m_leftStinkFact);
					m_agent.DestroyWME(m_rightStinkFact);
					m_agent.DestroyWME(m_upStinkFact);
					m_agent.DestroyWME(m_downStinkFact);
				}
				
		        m_leftRadarFact = m_agent.CreateIdWME(m_agent.GetInputLink(), RADAR_FACT_NAME);
		        m_rightRadarFact = m_agent.CreateIdWME(m_agent.GetInputLink(), RADAR_FACT_NAME);
		        m_upRadarFact = m_agent.CreateIdWME(m_agent.GetInputLink(), RADAR_FACT_NAME);
		        m_downRadarFact = m_agent.CreateIdWME(m_agent.GetInputLink(), RADAR_FACT_NAME);        

		        m_leftStinkFact = m_agent.CreateIdWME(m_agent.GetInputLink(), STINK_FACT_NAME);
		        m_rightStinkFact = m_agent.CreateIdWME(m_agent.GetInputLink(), STINK_FACT_NAME);
		        m_upStinkFact = m_agent.CreateIdWME(m_agent.GetInputLink(), STINK_FACT_NAME);
		        m_downStinkFact = m_agent.CreateIdWME(m_agent.GetInputLink(), STINK_FACT_NAME);        
		        
		        m_agent.CreateStringWME(m_leftRadarFact, "dir", "left");
		        m_agent.CreateStringWME(m_rightRadarFact, "dir", "right");
		        m_agent.CreateStringWME(m_upRadarFact, "dir", "up");
		        m_agent.CreateStringWME(m_downRadarFact, "dir", "down");

		        m_agent.CreateStringWME(m_leftStinkFact, "dir", "left");
		        m_agent.CreateStringWME(m_rightStinkFact, "dir", "right");
		        m_agent.CreateStringWME(m_upStinkFact, "dir", "up");
		        m_agent.CreateStringWME(m_downStinkFact, "dir", "down");
		        
			    int x = m_model.agentX() - 1;
			    if (x >= 0)
			        m_agent.CreateStringWME(m_leftRadarFact, "reading", (m_model.radar(Model.RADAR_LEFT) == Model.DIRTY) ? "dirty" : "clean");
			    else
			    	m_agent.CreateStringWME(m_leftRadarFact, "reading", "wall");	    	
	
			    x = m_model.agentX() + 1;
			    if (x < m_model.getWidth())
			        m_agent.CreateStringWME(m_rightRadarFact, "reading", (m_model.radar(Model.RADAR_RIGHT) == Model.DIRTY) ? "dirty" : "clean");
			    else
			    	m_agent.CreateStringWME(m_rightRadarFact, "reading", "wall");	    	
			    	
			    int y = m_model.agentY() - 1;
			    if (y >= 0)
			        m_agent.CreateStringWME(m_upRadarFact, "reading", (m_model.radar(Model.RADAR_UP) == Model.DIRTY) ? "dirty" : "clean");
			    else
			    	m_agent.CreateStringWME(m_upRadarFact, "reading", "wall");	    	
	
			    y = m_model.agentY() + 1;
			    if (y < m_model.getHeight())
			        m_agent.CreateStringWME(m_downRadarFact, "reading", (m_model.radar(Model.RADAR_DOWN) == Model.DIRTY) ? "dirty" : "clean");
			    else
			    	m_agent.CreateStringWME(m_downRadarFact, "reading", "wall");
			    
			    int stinkLeft = m_model.calculateStink(Model.RADAR_LEFT);
			    int stinkRight = m_model.calculateStink(Model.RADAR_RIGHT);
			    int stinkUp  = m_model.calculateStink(Model.RADAR_UP);
			    int stinkDown  = m_model.calculateStink(Model.RADAR_DOWN);
			    
			    m_agent.CreateIntWME(m_leftStinkFact, "reading", stinkLeft);
			    m_agent.CreateIntWME(m_rightStinkFact, "reading", stinkRight);
			    m_agent.CreateIntWME(m_upStinkFact, "reading", stinkUp);
			    m_agent.CreateIntWME(m_downStinkFact, "reading", stinkDown);
			}
			catch (CheatException e)
			{
				JOptionPane.showMessageDialog(Main.MAIN_WIN, "Error: Your agent cannot access radar!");
			}
		}
		
		m_agent.Commit();
	}
	
    public void registerForUpdateWorldEvent()
    {
    	m_agent.RegisterForRunEvent(smlRunEventId.smlEVENT_AFTER_DECISION_CYCLE, this, "afterDecisionHandler", null) ;
		m_agent.RegisterForPrintEvent(smlPrintEventId.smlEVENT_PRINT, this, "printEventHandler", this) ;		    	
    }

	public void printEventHandler(int eventID, Object data, Agent agent, String message)
	{
		System.out.println(message);
	}

	public String rand(int id, Object data, String agentName, String functionName, String arguments)
	{
		List<String> choices = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(arguments, ",");
		while (tokenizer.hasMoreTokens())
		{
			String tk = tokenizer.nextToken();
			if (tk.trim().length() > 0)
				choices.add(tk.trim());
		}
		
		int i = 0;
		if (choices.size() > 0)
			i = m_rand.nextInt(choices.size());

		return choices.get(i);
	}
	
	public void afterDecisionHandler(int eventID, Object data, Agent agent, int phase)
	{
		try
		{
			if ((m_model.getStepCount() > 0) && !m_model.isAgentStopped()) 
			{
				preStep();
				
				//dump working memory here...
		        String res = 
		        	m_kernel.ExecuteCommandLine("print s1 -d 10", m_agent.GetAgentName());
		        System.out.println("* " + res);
		        res = 
		        	m_kernel.ExecuteCommandLine("print s2 -d 10", m_agent.GetAgentName());
		        //System.out.println("* " + res);
				
				handleAction();
				postStep();

				try 
				{ Thread.sleep(m_model.getDelay()); }
				catch (InterruptedException ue)
				{ System.out.println(ue); }
			}
			else
			{
				m_kernel.StopAllAgents() ;
				m_model.haltAgent();
			}
		}
		catch (Throwable t)
		{
			System.out.println("Caught a throwable event" + t.toString()) ;			
		}
	}

}
