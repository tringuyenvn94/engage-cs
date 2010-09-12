package edu.lhup.ai.tictactoe;

import edu.lhup.ai.IMove;
import edu.lhup.ai.IPiece;

/**
 * A concrete implementation of a {@link IMove move} in a tic-tac-toe game.  
 * Tic-tac-toe moves contains peices, and a row and column.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class Move implements IMove
{
	public Move(IPiece piece, int row, int col)
	{
		m_piece = piece;
		m_iRow = row;
		m_iCol = col;
	}

	public int getRow()
	{ return m_iRow; }

	public int getCol()
	{ return m_iCol; }

	public IPiece getPiece()
	{ return m_piece; }

	public String toString()
	{
		return m_piece + "(" + m_iRow + ", " + m_iCol + ")";
	}

	private final IPiece m_piece;
	private int m_iRow = 0;	
	private int m_iCol = 0;	
}