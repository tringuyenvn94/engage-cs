package edu.lhup.vacuum;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

public class Main extends JFrame implements Observer, ActionListener
{
	public static final long serialVersionUID = 1;

	public static JFrame MAIN_WIN;

	private static final String TITLE = "Agent Enviroment (v 3.0)";

	private final JMenu m_file = new JMenu("File");
	private final JMenu m_options = new JMenu("Options");
	private final JMenuItem m_openJavaAgent = 
		new JMenuItem("Open Java Agent...");
	private final JMenuItem m_openJessAgent = 
		new JMenuItem("Open Jess Agent...");
	private final JMenuItem m_openSoarAgent = 
		new JMenuItem("Open Soar Agent...");
	private final JMenuItem m_exit = new JMenuItem("Exit");
	private final JMenuItem m_configureBoard = 
		new JMenuItem("Configure Board...");
	private final JMenuItem m_configureRun = 
		new JMenuItem("Configure Run...");
	private final JMenuItem m_loadBoard = new JMenuItem("Load Board...");
	private final JCheckBoxMenuItem m_simpleAgent = 
		new JCheckBoxMenuItem("Simple Reflex Agent");
	private final JCheckBoxMenuItem m_modelBasedAgent = 
		new JCheckBoxMenuItem("Model Based Reflex Agent");
	private final JCheckBoxMenuItem m_radarSensor = 
		new JCheckBoxMenuItem("Enable Radar Sensor");
	private final JCheckBoxMenuItem m_movePunish = 
		new JCheckBoxMenuItem("Penalize for Movement");

	private final JFileChooser m_chooser = new JFileChooser(System.getProperty("user.dir"));
	private final MyFileFilter m_fileFilter = new MyFileFilter();

	private final Model m_model = new Model();
	private final StatusModel m_statusModel = new StatusModel();
	private IView m_jess;
	private IView m_soar;
	private IView m_java;
	private String m_javaClass;
	private EnvSquare[][] m_grid = null;

	private JPanel m_board;
	private final JLabel m_statusBar = new JLabel();
	private JButton m_run = new JButton("Run");
	private JButton m_stop = new JButton("Stop");
	private JButton m_reset = new JButton("Reset");
	private final javax.swing.Timer m_statusTimer =
		new javax.swing.Timer(100, new StatusTimerListener());

	private final ConfigureBoardDlg m_configBoardDlg = 
		new ConfigureBoardDlg(this, m_model);

	private final ConfigureRunDlg m_configRunDlg = 
		new ConfigureRunDlg(this, m_model);

	public static void main(String[] args)
	{
		if (args.length == 1)
			new Main(args[0]);
		else
			new Main(null);
	}

	public Main(String strBoardFile)
	{
		super(TITLE);
		MAIN_WIN = this;

		System.out.println("Vacuum Started...");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new CloseHandler());

		configureMenu();

		if (strBoardFile != null)
		{
			try
			{ m_model.init(strBoardFile); }
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, 
					"Invalid board file!\n" + 
					e.getClass().toString() + ": " + e.getMessage(),
					"Error!", 
					JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{ m_model.init(); }

		System.out.println("Board Loaded...");
		
		m_model.addObserver(this);

		m_stop.setEnabled(false);
		m_run.setEnabled(false);
		m_reset.setEnabled(true);
		m_stop.addActionListener(this);
		m_run.addActionListener(this);
		m_reset.addActionListener(this);

		m_board = new JPanel(new GridLayout(m_model.getHeight(), 
											m_model.getWidth()));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(m_board, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(m_reset);
		buttonPanel.add(m_run);
		buttonPanel.add(m_stop);
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(m_statusBar, BorderLayout.CENTER);
		statusPanel.add(buttonPanel, BorderLayout.EAST);
		statusPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		getContentPane().add(statusPanel, BorderLayout.SOUTH);

		m_statusModel.pushPermStatus("Ready.");
		m_statusBar.setText("Ready.");
		m_statusBar.setPreferredSize
			(new Dimension(200, m_statusBar.getHeight()));
		m_statusTimer.start();

		m_chooser.setFileFilter(m_fileFilter);

		update(null, null);
		pack();
		setVisible(true);
		
		System.out.println("Frame Visible...");
	}

	public void actionPerformed(ActionEvent e)
	{
		m_model.setEventThread(Thread.currentThread());

		if (e.getSource() == m_run)
		{
			m_model.startAgent();
			Thread agentThread = null;
			if (m_model.getType() == Model.JESSFILE)
			{
				agentThread = new Thread(getJessView());
			}
			else if (m_model.getType() == Model.SOARFILE)
			{
				agentThread = new Thread(getSoarView());
			}
			else if (m_model.getType() == Model.JAVAFILE)
			{
				agentThread = new Thread(getJavaView());
			}
			
			agentThread.start();
		}
		else if (e.getSource() == m_stop)
		{
			m_model.haltAgent();
		}
		else if (e.getSource() == m_reset)
		{
			m_model.init(m_model.getWidth(), 
						 m_model.getHeight(),
						 m_model.getDirtyCount());
		}
		else if (e.getSource() == m_exit)
		{
			System.exit(0);
		}
		else if (e.getSource() == m_openJavaAgent)
		{
			int type = Model.JAVAFILE; 
			m_javaClass = JOptionPane.showInputDialog(this,"Enter Fully Qualified Java Class Name:", "edu.lhup.vacuum.DefaultJavaAgent");
			m_model.setAgentFile(m_javaClass, type);
			m_model.setAgentShortFile(m_javaClass);
			m_run.setEnabled(true);
		}
		else if (e.getSource() == m_openJessAgent || 
				 e.getSource() == m_openSoarAgent)
		{
			int type = Model.SOARFILE; 
			if (e.getSource() == m_openJessAgent)
			{
				m_fileFilter.set(".JESS", "Jess Files");
				type = Model.JESSFILE;
			}
			else
				m_fileFilter.set(".SOAR", "Soar Files");
			
			m_chooser.setVisible(true);
			int returnVal = m_chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
				try
				{
					String agentFile = 
						m_chooser.getSelectedFile().getCanonicalPath();
					agentFile = "\"" + agentFile.replace('\\', '/') + "\"";
					m_model.setAgentFile(agentFile, type);
					m_model.setAgentShortFile
						(m_chooser.getSelectedFile().getName());
					m_run.setEnabled(true);
				}
				catch (IOException fe)
				{
					System.out.println(fe);
				}
			}			
		}
		else if (e.getSource() == m_loadBoard)
		{
			m_fileFilter.set(".TXT", "Board Files");
			m_chooser.setVisible(true);
			int returnVal = m_chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
				try
				{
					File boardFile = m_chooser.getSelectedFile();
					m_model.init(boardFile.getAbsolutePath());
				}
				catch (Exception ex)
				{
				JOptionPane.showMessageDialog(this, 
					"Invalid board file!\n" + 
					ex.getClass().toString() + ": " + ex.getMessage(),
					"Error!", 
					JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					refreshViews();
				}
			}			
		}
		else if (e.getSource() == m_configureBoard)
		{
			m_configBoardDlg.setVisible(true);
			if (m_configBoardDlg.isOk())
			{
				refreshViews();
			}
		}
		else if (e.getSource() == m_configureRun)
		{
			m_configRunDlg.setVisible(true);
			if (m_configRunDlg.isOk())
			{
				refreshViews();
			}
		}
		else if ( (e.getSource() == m_simpleAgent) || 
  				  (e.getSource() == m_modelBasedAgent) )
		{
			m_model.setAllowState(m_modelBasedAgent.isSelected());
			refreshViews();			
		}
		else if (e.getSource() == m_radarSensor)
		{
			m_model.setRadarSensor(m_radarSensor.isSelected());
			refreshViews();			
		}
		else if (e.getSource() == m_movePunish)
		{
			m_model.setPenalizeForMovement(m_movePunish.isSelected());
			refreshViews();			
		}
	}

	public void update(Observable o, Object arg)
	{
		if (m_model.isAgentStopped())
		{
			m_file.setEnabled(true);
			m_options.setEnabled(true);
			m_statusModel.pushPermStatus("Ready.");

			if (m_model.getAgentFile() != null)
			{
				m_run.setEnabled(true);
				m_reset.setEnabled(true);
				m_stop.setEnabled(false);
				setTitle(TITLE + " - " + m_model.getAgentShortFile());
			}
		}
		else
		{
			m_file.setEnabled(false);
			m_options.setEnabled(false);
			m_run.setEnabled(false);
			m_reset.setEnabled(false);
			m_stop.setEnabled(true);

			m_statusModel.
				pushPermStatus("Step #" + (m_model.getStepCount()+1) + 
							   ":  SCORE: " + m_model.getScore());
		}

		drawGrid();
		getContentPane().validate();
		repaint();
	}

	private void drawGrid()
	{
		resizeGrid();
		for (int y = 0; y < m_model.getHeight(); y++)
		{
			for (int x = 0; x < m_model.getWidth(); x++)
			{
				m_grid[y][x].setDirty(m_model.isDirty(x,y));
				m_grid[y][x].drawAgent( (m_model.agentX() == x) && 
										(m_model.agentY() == y) );
			}
		}
	}

	private void resizeGrid()
	{
		if (m_grid == null || 
			m_grid.length != m_model.getHeight() ||
			m_grid[0].length != m_model.getWidth())
		{
			m_board.removeAll();
			m_board.setLayout(new GridLayout(m_model.getHeight(), 
											 m_model.getWidth()));
			m_grid = new EnvSquare[m_model.getHeight()]
								  [m_model.getWidth()];

			for (int y = 0; y < m_model.getHeight(); y++)
			{
				for (int x = 0; x < m_model.getWidth(); x++)
				{
					m_grid[y][x] = new EnvSquare(false);
					m_board.add(m_grid[y][x]);
				}
			}
		}
	}

	private void configureMenu()
	{
		// mnemonics...
		m_file.setMnemonic(KeyEvent.VK_F);
		m_options.setMnemonic(KeyEvent.VK_O);
		m_openJavaAgent.setMnemonic(KeyEvent.VK_V);		
		m_openJessAgent.setMnemonic(KeyEvent.VK_J);
		m_openSoarAgent.setMnemonic(KeyEvent.VK_A);
		m_exit.setMnemonic(KeyEvent.VK_X);
		m_configureBoard.setMnemonic(KeyEvent.VK_B);
		m_configureRun.setMnemonic(KeyEvent.VK_R);
		m_loadBoard.setMnemonic(KeyEvent.VK_L);
		m_simpleAgent.setMnemonic(KeyEvent.VK_S);
		m_modelBasedAgent.setMnemonic(KeyEvent.VK_M);
		m_radarSensor.setMnemonic(KeyEvent.VK_E);
		m_movePunish.setMnemonic(KeyEvent.VK_P);

		// file menu...
		m_file.add(m_openJavaAgent);
		m_file.add(m_openJessAgent);
		m_file.add(m_openSoarAgent);
		m_file.add(m_exit);
		m_openJavaAgent.addActionListener(this);
		m_openJessAgent.addActionListener(this);
		m_openSoarAgent.addActionListener(this);
		m_exit.addActionListener(this);

		// options menu...
		m_options.add(m_configureRun);
		m_options.add(m_configureBoard);
		m_options.add(m_loadBoard);
		m_options.addSeparator();
		ButtonGroup group = new ButtonGroup();
		m_options.add(m_simpleAgent);
		m_options.add(m_modelBasedAgent);
		group.add(m_simpleAgent);
		group.add(m_modelBasedAgent);
		m_simpleAgent.addActionListener(this);
		m_modelBasedAgent.addActionListener(this);
		m_modelBasedAgent.setSelected(m_model.getAllowState());
		m_simpleAgent.setSelected(!m_model.getAllowState());
		m_loadBoard.addActionListener(this);
		m_configureBoard.addActionListener(this);
		m_configureRun.addActionListener(this);
		m_options.addSeparator();
		m_options.add(m_radarSensor);
		m_options.addSeparator();
		m_options.add(m_movePunish);
		m_radarSensor.setSelected(m_model.getRadarSensor());
		m_radarSensor.addActionListener(this);
		m_movePunish.setSelected(m_model.getPenalizeForMovement());
		m_movePunish.addActionListener(this);

		// menu bar...
		JMenuBar bar = new JMenuBar();
		bar.add(m_file);
		bar.add(m_options);
		setJMenuBar(bar);
	}

	private class StatusTimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			m_statusBar.setText(m_statusModel.getNextStatus());
		}
	}

	private class CloseHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{ m_statusTimer.stop(); }
	}
	
	private IView getJessView()
	{ return getJessView(false); }
	
	private IView getJessView(boolean ignoreError) 
	{
		try
		{
			if (m_jess == null)
			{
				Class cls = Class.forName("edu.lhup.vacuum.jess.JessView");
				m_jess = (IView)cls.newInstance();
				m_jess.setModel(m_model);
			}
		}
		catch (Exception e)
		{
			if (!ignoreError)
			{
			JOptionPane.showMessageDialog(this, 
					"Error loading the Jess engine!\n" + 
					"Please restart the environment with jess.jar included in the classpath.",
					"Error!", 
					JOptionPane.ERROR_MESSAGE);
			}
		}
		return m_jess;
	}

	private IView getSoarView()
	{ return getSoarView(false); }
	
	private IView getSoarView(boolean ignoreError) 
	{
		try
		{
			if (m_soar == null)
			{
				Class<?> cls = Class.forName("edu.lhup.vacuum.soar.SoarView");
				m_soar = (IView)cls.newInstance();
				m_soar.setModel(m_model);
			}
		}
		catch (Exception e)
		{
			if (!ignoreError)
			{
			JOptionPane.showMessageDialog(this, 
					"Error loading the Soar engine!\n" + 
					"Please restart the environment with sml.jar included in the classpath.", 
					"Error!", 
					JOptionPane.ERROR_MESSAGE);
			}
		}
		return m_soar;
	}

	private IView getJavaView()
	{ return getJavaView(false); }
	
	private IView getJavaView(boolean ignoreError)  
	{
		try
		{
			if (m_java== null)
			{
				Class cls = Class.forName(m_javaClass);
				m_java = (IView)cls.newInstance();
				m_java.setModel(m_model);
			}
		}
		catch (Exception e)
		{
			if (!ignoreError)
			{
			JOptionPane.showMessageDialog(this, 
					"Error loading the Java class!\n" + 
					"Please make sure you entered the fully qualified name of your Java class\n" + 
					"and that the class is in the classpath.", 
					"Error!", 
					JOptionPane.ERROR_MESSAGE);
			}
		}
		return m_java;
	}
	
	private void refreshViews()
	{
		IView v = null;
		
		v = getJessView(true);
		if (v != null)
			v.init();
		
		v = getSoarView(true);
		if (v != null)
			v.init();
		
		v = getJavaView(true);
		if (v != null)
			v.init();
		
	}
	
}
