package edu.lhup.ai;

import java.util.*;
import edu.lhup.ai.*;

/**
 * A concrete player that plays any game by selecting a random move from the set
 * of currently legal moves.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class RandomPlayer implements IPlayer
{
	public String getDescription()
	{
		return "Computer based player.  Moves are selected randomly.";
	}

	public String getShortDescription()
	{
		return "Computer";
	}

	public void setEvaluator(IEvaluator evaluator)
	{
	}

	public void setCutoff(int cutoff)
	{
	}

	/**
	 * Selects a random move from the set of currently legal moves.
	 * 
	 * @param board the {@link IBoard} object that this player will make a move
	 * on.
	 * 
	 * @return the {@link IMove move} taken by the player.
	 * 
	 * @throws StateException if for some strange reason the selected move
	 * turns out not to be legal.
	 */
	public IMove takeTurn(IBoard board) throws StateException
	{
		LinkedList moves = new LinkedList();
		board.moves(moves);

		int i = m_random.nextInt(moves.size());

		board.pushMove((IMove)moves.get(i));
		
		return (IMove)moves.get(i);
	}

	public String toString()
	{
		return getShortDescription();
	}

	private static final Random m_random = new Random();
}