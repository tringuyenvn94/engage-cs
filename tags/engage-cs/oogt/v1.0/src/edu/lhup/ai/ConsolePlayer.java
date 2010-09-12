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
	 * @throws TurnException if the user enters an invalid move on the 
	 * command line.
	 */
	public void takeTurn(IBoard board) throws TurnException
	{
		boolean bValidMove = true;
		do
		{
			bValidMove = true;
			try
			{
				BufferedReader read = 
					new BufferedReader(new InputStreamReader(System.in));
				String in = read.readLine();
				if (in != null)
				{
					board.pushMove(in);
				}
			}
			catch (IOException e)
			{
				throw new TurnException("IOError whlie taking turn", e);
			}
			catch (StateException e)
			{
				bValidMove = false;
				System.out.println(e.getMessage());
			}
		} while (!bValidMove);
	}

	public String toString()
	{
		return getShortDescription();
	}
}