package swarm;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

public class Main extends JFrame implements IModelListener, WindowListener
{
	private boolean m_stop = false;
	private AntBase[] m_ants = new AntBase[1000];
	private Model m_model = null;
	private JPanel m_mainPanel = null;
	
	public static void main(String[] args) throws Exception 
	{
		String file = "world.txt";
		String antClass = "MyAnt";
		if (args.length == 1)
		{
			file = args[0];
		}
		else if (args.length == 2)
		{
			file = args[0];
			antClass = args[1];
		}

		new Main(file, antClass);
	}
	
	public Main(String file, String antClass) throws Exception
	{
		setTitle("Ant Foraging App");
		m_model = new Model(file);
		m_model.addListener(this);
		
		setLayout(new BorderLayout());
		
		GridLayout grid = new GridLayout(m_model.getH(), m_model.getW());
		m_mainPanel = new JPanel(grid);
		
		populate();
		
		add(m_mainPanel, BorderLayout.CENTER);
		
		pack();
		
		setVisible(true);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		addWindowListener(this);
		
		for (int i = 0 ; i < m_ants.length && !m_stop; i++)
		{
			Class<?> clsAnt = Class.forName(antClass);
			AntBase ant = (AntBase)clsAnt.newInstance();
			
			m_ants[i] = ant;
			m_ants[i].setModel(m_model);
			Thread t = new Thread(m_ants[i]);
			t.start();
			
			Thread.sleep(500);
		}
	}
	
	private void populate()
	{
		for (int row = 0; row < m_model.getH(); row++)
		{
			for (int col = 0; col < m_model.getW(); col++)
			{
				m_model.add(m_mainPanel,row, col);
			}
		}
	}

	@Override
	public void modelChange() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) 
	{
		m_stop = true;
		for (int i = 0; i < m_ants.length; i++)
		{
			if (m_ants[i] != null)
				m_ants[i].stopThisCrazyAnt();
		}
		try
		{
			Thread.sleep(3000);	
		}
		catch(Exception ex)
		{
		}
		System.exit(0);
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) 
	{
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
