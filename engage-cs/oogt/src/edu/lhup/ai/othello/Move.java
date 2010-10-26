package edu.lhup.ai.othello;

import edu.lhup.ai.IMove;
import edu.lhup.ai.IPiece;
import java.util.*;

/**
 * A concrete implementation of a {@link IMove move} in an othello game.  
 * Othello moves contains pieces, and a row and column.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class Move implements IMove
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

	public List<Move> getFlipped()
	{ return m_flipped; }
	
	public void addFlipped(int row, int col, IPiece piece)
	{ m_flipped.add(new Move(piece, row, col)); }
	
	private List<Move> m_flipped = new ArrayList<Move>();
	private final IPiece m_piece;
	private int m_iRow = 0;	
	private int m_iCol = 0;	
}