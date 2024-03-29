package edu.lhup.ai.tictactoe;

import edu.lhup.ai.*;

import java.util.*;
import java.util.logging.*;

/**
 * Iterates all of the currently legal moves in a 
 * {@link Board tic-tac-toe game} 
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
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.MoveIterator");
		logger.entering("edu.lhup.ai.tictactoe.MoveIterator", "findNextMove", 
					    new Boolean(bRetain) );

		Move nextMove = null;

		if ( (m_board.getState() == Board.XTURN) ||
		     (m_board.getState() == Board.OTURN) )
		{
			IPiece nextPiece = Board.xPiece();
			Move lastMove = (Move)m_board.peekMove();

			if (lastMove != null)
			{
				nextPiece = (lastMove.getPiece() == Board.xPiece()) ? 
					Board.oPiece() : Board.xPiece();
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
						try
						{
							m_board.legalMove(nextMove);
							bFoundOne = true;
						}
						catch (StateException e)
						{
							nextMove = null;
						}
					}

					if (iCol < 2)
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
		logger.exiting("edu.lhup.ai.tictactoe.MoveIterator", "nextMove", 
					   nextMove);
		return nextMove;
	}

	private int m_iCurRow = 0;
	private int m_iCurCol = 0;
	private final Board m_board;
}