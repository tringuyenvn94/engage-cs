package edu.lhup.ai;

import java.util.*;
import java.io.*;
import edu.lhup.ai.*;

/**
 * This can be considered a "human" player because it accepts moves 
 * from the command line.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class ConsolePlayer implements IPlayer
{
	public String getDescription()
	{
		return "Human player.  Moves are entered via the command line.";
	}

	public String getShortDescription()
	{
		return "Human";
	}

	public void setEvaluator(IEvaluator evaluator)
	{
	}

	public void setCutoff(int cutoff)
	{
	}

	/**
	 * Prompts the user for the next move via the command line.
	 * 
	 * @param board the {@link IBoard} object that this player will make a move
	 * on.
	 * 
	 * @return an {@link IMove object} representation of the move.
	 * 
	 * @throws StateException if the player attempts to push an invalid
	 * move onto the games stack.
	 */
	public IMove takeTurn(IBoard board) throws StateException
	{
		IMove move = null;
		Scanner scanner = new Scanner(System.in);
		String in = scanner.nextLine();
		if (in != null)
		{
			move = board.pushMove(in);
		}
		return move;
	}

	public String toString()
	{
		return getShortDescription();
	}
}