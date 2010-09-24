package edu.lhup.ai.othello;

import java.util.*;
import java.util.logging.*;

import edu.lhup.ai.*;

/**
 * An evaluator that is capable of evaluating an {@link Board othello} game.
 * This is done by evaluating terminal and non-terminal states.  For example, 
 * this evaluator will return 0 if the current state is a tie, 1 if the current 
 * state is a victory, and -1 if the current state is a defeat.  If the current 
 * state is not an end state, the board is analyzed to see who has more pieces. 
 * If the "maxPlayer" has more pieces, 1 is returned, if the "maxPlayer" has 
 * fewer pieces, -1 is returned.  If the two players have the same number of  
 * pieces, 0 is returned.  This evaluator works well even when a cutoff is 
 * specified because it can evaluate non-terminal states.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class Evaluator implements IEvaluator
{
	/**
	 * Provides a mechanism for evaluating the current state of a game.  
	 * 
	 * @param board the game that will be evaluated.
	 * @param maxPlayer the player who's perspective will be used when 
	 * evaluating the specified game. 
	 *
	 * @return  0 if the current state is a tie, 1 if the current 
	 * state is a victory, and -1 if the current state is a defeat.  
	 * If the current state is not an end state, the board is analyzed to 
	 * see who has the most pieces.  If the "maxPlayer" has more pieces, 
	 * 1 is returned, if the "minPlayer" has more pieces, -1 is returned.  
	 * If players have the same number of pieces, 0 is returned.  
	 */
	public int evaluate(IBoard board, IPlayer maxPlayer)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.othello.Evaluator");
		logger.entering("edu.lhup.ai.othello.Evaluator", "evaluate", 
						"\n" + board.toString());						  

		int rating = Integer.MIN_VALUE;

		IPiece prevPiece = getPrevPiece(board, maxPlayer);
		IPiece nextPiece = getNextPiece(board, maxPlayer);

		logger.finest("MaxPlayer: " + maxPlayer);
		logger.finest("WinningPlayer: " + board.getWinner());
		logger.finest("PrevPiece: " + prevPiece);
		logger.finest("NextPiece: " + nextPiece);

	   	IPiece[][] grid = board.getBoard();

		// if max player has just won, who cares about the rest?
		if ( (board.getWinner() != null) && (board.getWinner() == maxPlayer) )
		{
			rating = 1;
		}
		// if min player has just won, who cares about the rest?
		else if ( (board.getWinner() != null) && (board.getWinner() != maxPlayer) )
		{
			rating = -1;
		}
		// if next player is about to win ...
		
		// TODO: probably don't want pending victory, instead return the 
		// (MAX PIECES - MIN PIECES) ...
		// This is not the best evaluation function because of the horizon effect,
		// MAX may get more pieces in this state, but just over the horizon MIN's next
		// move wins the game...
		else if (pendingVictory(grid, nextPiece))
		{
			// and if next player is max player
			if (maxPlayerNext(board, maxPlayer))
			{
				rating = 1;
			}
			else
			{
				rating = -1;
			}
		}
		// if previous player is about to win...
		else if (pendingVictory(grid, prevPiece))
		{
			// and if prev player is max player
			if (maxPlayerNext(board, maxPlayer))
			{
				rating = -1;
			}
			else
			{
				rating = 1;
			}
		}
		// nobody has won and nobody is about to win...
		else 																	
		{
			rating = 0;
		}

		logger.exiting("edu.lhup.ai.othello.Evaluator", 
					   "evaluate", 
					   new Integer(rating));
		return rating;
	}

	public String toString()
	{
		return "1:win, -1:loss, 0:tie, 0:other";
	}

	private boolean maxPlayerNext(IBoard board, IPlayer maxPlayer)
	{
		boolean bYes = false;
		Iterator i = board.playerIterator();
		if (i.hasNext())
		{
			IPlayer nextPlayer = (IPlayer)i.next();
			bYes = (nextPlayer == maxPlayer);
		}
		return bYes;
	}

	private IPiece getPrevPiece(IBoard board, IPlayer maxPlayer)
	{
		IPiece minPiece = null;
		try
		{
			minPiece = board.peekMove().getPiece();
		}
		catch (StateException e)
		{
			throw new IllegalStateException("Error Getting Min Piece");
		}
		return minPiece;
	}

	private IPiece getNextPiece(IBoard board, IPlayer maxPlayer)
	{
		IPiece maxPiece = (getPrevPiece(board, maxPlayer) == Board.bPiece()) ? 
			Board.wPiece() : Board.bPiece();
		return maxPiece;
	}

	private boolean pendingVictory(IPiece[][] grid, IPiece piece)
	{
		return false;
	}

	private boolean pendingRowVictory(IPiece[][] grid, IPiece piece)
	{
		return false; 
	}

	private boolean pendingColVictory(IPiece[][] grid, IPiece piece)
	{
		return false; 
	}

	private boolean pendingLeftDiagVictory(IPiece[][] grid, IPiece piece)
	{
		return false; 
	}

	private boolean pendingRightDiagVictory(IPiece[][] grid, IPiece piece)
	{
		return false; 
	}
}
