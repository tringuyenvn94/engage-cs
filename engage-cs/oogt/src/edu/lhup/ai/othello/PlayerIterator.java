package edu.lhup.ai.othello;

import edu.lhup.ai.*;
import java.util.*;

/**
 * Iterates the two {@link IPlayer player's} currently playing a game of 
 * {@link Board othello}.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class PlayerIterator implements Iterator
{
	PlayerIterator(Board board)
	{
		m_board = board;
	}

	public boolean hasNext()
	{
		return ( (m_board.getState() == Board.BTURN) || 
			   	 (m_board.getState() == Board.WTURN) );
	}

	public Object next()
	{
		Object player = null;
		if (m_board.getState() == Board.BTURN)
		{
			player = m_board.getPlayers()[0];
		}
		else
		{
			player = m_board.getPlayers()[1];
		}
		return player;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private final Board m_board;
}