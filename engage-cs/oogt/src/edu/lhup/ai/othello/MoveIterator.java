package edu.lhup.ai.othello;

import edu.lhup.ai.*;

import java.util.*;
import java.util.logging.*;

/**
 * Iterates all of the currently legal moves in a 
 * {@link Board othello game} 
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class MoveIterator implements Iterator
{
	public MoveIterator(Board board)
	{
		m_board = board;
	}

	public boolean hasNext()
	{
		return (findNextMove(false) != null);
	}

	public Object next()
	{
		return findNextMove(true);		
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private IMove findNextMove(boolean bRetain)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.othello.MoveIterator");
		logger.entering("edu.lhup.ai.othello.MoveIterator", "findNextMove", 
					    new Boolean(bRetain) );

		Move nextMove = null;

		if ( (m_board.getState() == Board.BTURN) ||
		     (m_board.getState() == Board.WTURN) )
		{
			IPiece nextPiece = Board.bPiece();
			Move lastMove = (Move)m_board.peekMove();

			if (lastMove != null)
			{
				nextPiece = (lastMove.getPiece() == Board.bPiece()) ? 
					Board.wPiece() : Board.bPiece();
			}

			int iRowTrack = m_iCurRow;
			int iColTrack = m_iCurCol;
			boolean bFoundOne = false;
			IPiece[][] board = m_board.getBoard();
			

			for (int iRow = iRowTrack; iRow < board.length && !bFoundOne; iRow++)
			{
				for (int iCol = iColTrack; 
					 iCol < board[0].length && !bFoundOne; 
					 iCol++)
				{
					logger.finest("board["+iRow+"]"+"["+iCol+"] " + board[iRow][iCol]);

					if (board[iRow][iCol] == 
						Board.emptyPiece())
					{
						nextMove = 
							Board.getMove(nextPiece, iRow, iCol);
						logger.finer("trying move " + nextMove);
						logger.finer("on board\n" + m_board.toString());						
						try
						{
							m_board.legalMove(nextMove);
							logger.finer("move worked");
							bFoundOne = true;
						}
						catch (StateException e)
						{
							logger.finer("move failed");
							nextMove = null;
						}
					}

					if (iCol < Board.MAX_DIMENSION-1)
					{
						iColTrack = iCol+1;
					}
					else
					{
						iRowTrack = iRow+1;
						iColTrack = 0;
					}
				}
			}
			if (bRetain)
			{
				m_iCurRow = iRowTrack;
				m_iCurCol = iColTrack;
			}
		}

		logger.exiting("edu.lhup.ai.othello.MoveIterator", "nextMove", 
					   nextMove);
		return nextMove;
	}

	private int m_iCurRow = 0;
	private int m_iCurCol = 0;
	private final Board m_board;
}