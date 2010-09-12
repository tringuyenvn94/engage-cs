package edu.lhup.ai.tictactoe;

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
 * Provides a GUI interface for a tic-tac-toe game.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class TicTacToeGUIInterface extends JFrame 
	implements IGameInterface, ActionListener
{
	public TicTacToeGUIInterface()
	{
		super("Tic-Tac-Toe");
		setSize(350,350);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		m_status = new JPanel(new FlowLayout());
		m_gameBoard = new JPanel(new GridLayout(3,3));

		resetGameBoard();
		
		m_statusLabel = new JLabel("Click on a square to play!");
		m_status.add(m_statusLabel);
		
		add(m_gameBoard, BorderLayout.CENTER);
		add(m_status, BorderLayout.SOUTH);
	}
	
	public void init(IBoard board) 
	{
		m_board = board;
		GUIPlayer player = (GUIPlayer)m_board.getPlayers()[0];
		player.setInterface(this);
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
			
			if (m_board.playerIterator().hasNext())
			{
				IPlayer currentPlayer = (IPlayer)m_board.playerIterator().next();
				String txt = m_board.getState() == Board.OTURN ? "O" : "X";			
				Move move = (Move) currentPlayer.takeTurn(m_board);
				m_squares[move.getRow()][move.getCol()].setText(txt);
			}

			if (m_board.playerIterator().hasNext())
			{
				IPlayer currentPlayer = (IPlayer)m_board.playerIterator().next();
				String txt = m_board.getState() == Board.OTURN ? "O" : "X";
				Move move = (Move) currentPlayer.takeTurn(m_board);
				m_squares[move.getRow()][move.getCol()].setText(txt);
			}

			if (!m_board.playerIterator().hasNext())
			{
				if (m_board.getWinner() != null)
				{
					m_statusLabel.setText("Winner: " + 
									      m_board.getWinner().getShortDescription() + 
								          ". Click on a square to play again."); 
					 
				}			
				else			
				{				
					m_statusLabel.setText("Tie! Click on a square to play again.");
				}				
			}
		}
		catch (StateException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void resetGameBoard()
	{
		for (int row = 0; row < 3; row++)
		{
			for (int col = 0; col < 3; col++)
			{
				m_squares[row][col].setText("-");
				m_squares[row][col].removeActionListener(this);
				m_squares[row][col].addActionListener(this);
				m_gameBoard.add(m_squares[row][col]);
			}
		}		
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
	    { {new JButton("-"), new JButton("-"), new JButton("-")}, 
	      {new JButton("-"), new JButton("-"), new JButton("-")},
	      {new JButton("-"), new JButton("-"), new JButton("-")} } ;
}
