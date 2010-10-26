package edu.lhup.ai.othello;

import edu.lhup.ai.IBoard;
import edu.lhup.ai.IEvaluator;
import edu.lhup.ai.IMove;
import edu.lhup.ai.IPlayer;
import edu.lhup.ai.StateException;

public class GUIPlayer implements IPlayer 
{
	public void setInterface(OthelloGUIInterface gui)
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
		
		if (m_gui.getButtonClicked().getText().equals("Skip"))
			board.pushMove((IMove)null);
		else
		{
			for (int row = 0; row < OthelloGUIInterface.BOARD_SIZE && !bValidMove; row++)
			{
				for (int col = 0; col < OthelloGUIInterface.BOARD_SIZE && !bValidMove; col++)
				{
					if (m_gui.getSquares()[row][col] == m_gui.getButtonClicked())
					{
						if (board.getState() == Board.WTURN)
						{
							move = new Move(Board.wPiece(), row, col);
							board.pushMove(move);
						}
						else
						{
							move = new Move(Board.bPiece(), row, col);
							board.pushMove(move);
						}
						bValidMove = true; 						
					}
				}
			}
		}
		return move;
	}
	
	private OthelloGUIInterface m_gui;
}
