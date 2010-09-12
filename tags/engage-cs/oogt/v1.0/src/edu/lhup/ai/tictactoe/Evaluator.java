package edu.lhup.ai.tictactoe;

import java.util.*;
import java.util.logging.*;

import edu.lhup.ai.*;

/**
 * An evaluator that is capable of evaluating a {@link Board tic-tac-toe} game.
 * This is done by evaluating terminal and non-terminal states.  For example, 
 * this evaluator will return 0 if the current state is a tie, 1 if the current 
 * state is a victory, and -1 if the current state is a defeat.  If the current 
 * state is not an end state, the board is analyzed to see if anyone is about 
 * to win.  If the "maxPlayer" is about to win, 1 is returned, if the 
 * "maxPlayer" is about to loose, -1 is returned.  If no player is about to 
 * win, 0 is returned.  This evaluator works well even when a cutoff is 
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
	 * @param maxPlayer the player whos perspective will be used when 
	 * evaluating the specified game. 
	 *
	 * @return  0 if the current state is a tie, 1 if the current 
	 * state is a victory, and -1 if the current state is a defeat.  
	 * If the current state is not an end state, the board is analyzed to 
	 * see if anyone is about to win.  If the "maxPlayer" is about to win, 
	 * 1 is returned, if the "minPlayer" is about to win, -1 is returned.  
	 * If no player is about to win, 0 is returned.  
	 */
	public int evaluate(IBoard board, IPlayer maxPlayer)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Evaluator");
		logger.entering("edu.lhup.ai.tictactoe.Evaluator", "evaluate", 
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
		// nobody has one and nobody is about to win...
		else 																	
		{
			rating = 0;
		}

		logger.exiting("edu.lhup.ai.tictactoe.Evaluator", 
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
		IPiece maxPiece = (getPrevPiece(board, maxPlayer) == Board.xPiece()) ? 
			Board.oPiece() : Board.xPiece();
		return maxPiece;
	}

	private boolean pendingVictory(IPiece[][] grid, IPiece piece)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Evaluator");
		logger.entering("edu.lhup.ai.tictactoe.Evaluator", "pendingVictory", 
						"\n" + piece);						  

		boolean bYes = false;
		bYes = (bYes) || pendingRowVictory(grid, piece);
		bYes = (bYes) || pendingColVictory(grid, piece);
		bYes = (bYes) || pendingLeftDiagVictory(grid, piece);
		bYes = (bYes) || pendingRightDiagVictory(grid, piece);

		logger.exiting("edu.lhup.ai.tictactoe.Evaluator", 
					   "pendingVictory", 
					   new Boolean(bYes));
		return bYes;
	}

	private boolean pendingRowVictory(IPiece[][] grid, IPiece piece)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Evaluator");
		logger.entering("edu.lhup.ai.tictactoe.Evaluator", "pendingRowVictory", 
						"\n" + piece);						  

		boolean bYes = false;
		for (int row = 0; (row < grid[0].length) && !bYes; row++)
		{
			int emptyCount = 0;
			int pieceCount = 0;
			for (int col = 0; col < grid.length; col++)
			{
				if (grid[row][col] == piece) 
					pieceCount++;
				else if (grid[row][col] == Board.emptyPiece()) 
					emptyCount++;
			}
			logger.finest("pieceCount: " + pieceCount);
			logger.finest("emptyCount: " + emptyCount);
			bYes = ((emptyCount == 1) && (pieceCount == 2));
		}

		logger.exiting("edu.lhup.ai.tictactoe.Evaluator", 
					   "pendingRowVictory", 
					   new Boolean(bYes));
		return bYes; 
	}

	private boolean pendingColVictory(IPiece[][] grid, IPiece piece)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Evaluator");
		logger.entering("edu.lhup.ai.tictactoe.Evaluator", "pendingColVictory", 
						"\n" + piece);						  

		boolean bYes = false;
		for (int col = 0; (col < grid[0].length) && !bYes; col++)
		{
			int emptyCount = 0;
			int pieceCount = 0;
			for (int row = 0; row < grid.length; row++)
			{
				if (grid[row][col] == piece) 
					pieceCount++;
				else if (grid[row][col] == Board.emptyPiece()) 
					emptyCount++;
			}
			logger.finest("pieceCount: " + pieceCount);
			logger.finest("emptyCount: " + emptyCount);
			bYes = ((emptyCount == 1) && (pieceCount == 2));
		}

		logger.exiting("edu.lhup.ai.tictactoe.Evaluator", 
					   "pendingColVictory", 
					   new Boolean(bYes));
		return bYes; 
	}

	private boolean pendingLeftDiagVictory(IPiece[][] grid, IPiece piece)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Evaluator");
		logger.entering("edu.lhup.ai.tictactoe.Evaluator", 
						"pendinLeftDiagVictory", 
						"\n" + piece);						  

		int emptyCount = 0;
		int pieceCount = 0;
		for (int i = 0; i < grid[0].length; i++)
		{
			if (grid[i][i] == piece) 
				pieceCount++;
			else if (grid[i][i] == Board.emptyPiece()) 
				emptyCount++;
		}
		logger.finest("pieceCount: " + pieceCount);
		logger.finest("emptyCount: " + emptyCount);
		boolean bYes = ((emptyCount == 1) && (pieceCount == 2));

		logger.exiting("edu.lhup.ai.tictactoe.Evaluator", 
					   "pendingLeftDiagVictory", 
					   new Boolean(bYes));
		return bYes; 
	}

	private boolean pendingRightDiagVictory(IPiece[][] grid, IPiece piece)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Evaluator");
		logger.entering("edu.lhup.ai.tictactoe.Evaluator", 
						"pendinRightDiagVictory", 
						"\n" + piece);						  

		int emptyCount = 0;
		int pieceCount = 0;
		for (int i = 0; i < grid[0].length; i++)
		{
			int j = (grid[0].length - 1) - i;
			if (grid[i][j] == piece) 
				pieceCount++;
			else if (grid[i][j] == Board.emptyPiece()) 
				emptyCount++;
		}
		logger.finest("pieceCount: " + pieceCount);
		logger.finest("emptyCount: " + emptyCount);
		boolean bYes = ((emptyCount == 1) && (pieceCount == 2));

		logger.exiting("edu.lhup.ai.tictactoe.Evaluator", 
					   "pendingLeftDiagVictory", 
					   new Boolean(bYes));
		return bYes; 
	}
}
