package edu.lhup.ai.tictactoe;

import edu.lhup.ai.*;
import java.util.*;

/**
 * Iterates the two {@link IPlayer player's} currenly playing a game of 
 * {@link Board tic-tac-toe}.
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
		return ( (m_board.getState() == Board.XTURN) || 
			   	 (m_board.getState() == Board.OTURN) );
	}

	public Object next()
	{
		Object player = null;
		if (m_board.getState() == Board.XTURN)
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