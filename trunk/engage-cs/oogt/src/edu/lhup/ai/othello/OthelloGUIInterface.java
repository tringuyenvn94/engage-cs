package edu.lhup.ai.othello;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.*;
import edu.lhup.ai.IBoard;
import edu.lhup.ai.IEvaluator;
import edu.lhup.ai.IGameInterface;
import edu.lhup.ai.IMove;
import edu.lhup.ai.IPlayer;
import edu.lhup.ai.StateException;


/**
 * Provides a GUI interface for a othello game.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class OthelloGUIInterface extends JFrame 
	implements IGameInterface, ActionListener
{
	public static final int BOARD_SIZE = Board.MAX_DIMENSION;
	
	public OthelloGUIInterface()
	{
		super("Othello");
		setSize(350,350);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		m_status = new JPanel(new FlowLayout());
		m_gameBoard = new JPanel(new GridLayout(BOARD_SIZE,BOARD_SIZE));

		resetGameBoard();
		
		m_statusLabel = new JLabel("Click on a square to play!");
		m_status.add(m_statusLabel);

		JButton btnSkip = new JButton("Skip");
		btnSkip.addActionListener(this);
		add(btnSkip, BorderLayout.NORTH);		
		add(m_gameBoard, BorderLayout.CENTER);
		add(m_status, BorderLayout.SOUTH);
	}
	
	public void init(IBoard board) 
	{
		m_board = board;
		GUIPlayer player = (GUIPlayer)m_board.getPlayers()[0];
		player.setInterface(this);
		copyBoard();		
	}

	public void playGame()
	{
		setVisible(true);
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		m_buttonClicked = (JButton)arg0.getSource();
		try
		{
			if (!m_board.playerIterator().hasNext())
			{
				m_board.resetState();
				resetGameBoard();
				return;
			}
			
			IMove m1 = null;
			IMove m2 = null;
			if (m_board.playerIterator().hasNext())
			{
				IPlayer currentPlayer = (IPlayer)m_board.playerIterator().next();
				m1 = currentPlayer.takeTurn(m_board);
				copyBoard();
			}

			if (m_board.playerIterator().hasNext())
			{
				IPlayer currentPlayer = (IPlayer)m_board.playerIterator().next();
				m2 = currentPlayer.takeTurn(m_board);
				copyBoard();
			}

			if ((m1 == null && m2 == null) || !m_board.playerIterator().hasNext())
			{
				if (m_board.getWinner() != null)
				{
					m_statusLabel.setText("Winner: " + 
									      m_board.getWinner().getShortDescription() + 
								          ". Click on a square to play again."); 
					 
				}			
				else			
				{				
					//m_statusLabel.setText("Tie! Click on a square to play again.");
					int bcnt = ((Board)m_board).countPieces(((Board)m_board).bPiece());
					int wcnt = ((Board)m_board).countPieces(((Board)m_board).wPiece());
					m_statusLabel.setText("Game Over! Black: " + bcnt + " White: " + wcnt + ". Click to play again.");
				}				
			}
		}
		catch (StateException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void copyBoard()
	{
		for (int row = 0; row < OthelloGUIInterface.BOARD_SIZE; row++)
		{
			for (int col = 0; col < OthelloGUIInterface.BOARD_SIZE; col++)
			{
				m_squares[row][col].setText(m_board.getBoard()[row][col].toString());
				setColor(m_squares[row][col]);
			}
		}
	}
	
	private void resetGameBoard()
	{
		for (int row = 0; row < BOARD_SIZE; row++)
		{
			for (int col = 0; col < BOARD_SIZE; col++)
			{
				if (row == 2 && col == 2)
					m_squares[row][col].setText("B");
				else if (row == 3 && col == 3)
					m_squares[row][col].setText("B");
				else if (row == 2 && col == 3)
					m_squares[row][col].setText("W");
				else if (row == 3 && col == 2)
					m_squares[row][col].setText("W");
				else 
					m_squares[row][col].setText("-");
				
				m_squares[row][col].removeActionListener(this);
				m_squares[row][col].addActionListener(this);
				m_gameBoard.add(m_squares[row][col]);
			}
		}		
	}
	
	private JButton setColor(JButton btn)
	{
		Color c = Color.green;
		if (btn.getText().equals("B"))
			c = Color.black;
		else if (btn.getText().equals("W"))
			c = Color.white;
			
		btn.setForeground(c);
		btn.setBackground(c);
		return btn;
	}
	
	public JButton[][] getSquares()
	{ return m_squares; }

	public JButton getButtonClicked()
	{ return m_buttonClicked; }
	
	private JPanel m_status = null;
	private JPanel m_gameBoard = null;
	private JLabel m_statusLabel = null;
	private IBoard m_board = null;
	private JButton m_buttonClicked = null;
	private JButton[][] m_squares = new JButton[][] 
	    { {new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-")}, 
		  {new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-")},
		  {new JButton("-"), new JButton("-"), new JButton("B"), new JButton("W"), new JButton("-"), new JButton("-")},
		  {new JButton("-"), new JButton("-"), new JButton("W"), new JButton("B"), new JButton("-"), new JButton("-")},
		  {new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-")},
		  {new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-"), new JButton("-")},		  
			} ;
}
