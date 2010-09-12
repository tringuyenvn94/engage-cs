package edu.lhup.ai.tictactoe;

import edu.lhup.ai.IBoard;
import edu.lhup.ai.IEvaluator;
import edu.lhup.ai.IMove;
import edu.lhup.ai.IPlayer;
import edu.lhup.ai.StateException;

public class GUIPlayer implements IPlayer 
{
	public void setInterface(TicTacToeGUIInterface gui)
	{
		m_gui = gui;
	}
	
	public String getDescription() 
	{
		return "Human player.  Moves are entered via the GUI interface.";
	}

	public String getShortDescription() 
	{
		return "Human";
	}
	
	public void setEvaluator(IEvaluator evaluator) 
	{}
	
	public void setCutoff(int cutoff) 
	{}

	public IMove takeTurn(IBoard board) throws StateException 
	{
		Move move = null;
		boolean bValidMove = false;
		for (int row = 0; row < 3 && !bValidMove; row++)
		{
			for (int col = 0; col < 3 && !bValidMove; col++)
			{
				if (m_gui.getSquares()[row][col] == m_gui.getButtonClicked())
				{
					if (board.getState() == Board.OTURN)
					{
						move = new Move(Board.oPiece(), row, col);
						board.pushMove(move);
					}
					else
					{
						move = new Move(Board.xPiece(), row, col);
						board.pushMove(move);
					}
					bValidMove = true; 						
				}
			}
		}
		return move;
	}
	
	private TicTacToeGUIInterface m_gui;
}
