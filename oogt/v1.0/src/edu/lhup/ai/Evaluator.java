package edu.lhup.ai;

import java.util.*;
import java.util.logging.*;

/**
 * A default evaluator that is capable of evaluating any game.  This is done
 * by only evaluating end states.  For example, this evaluator will return 
 * 0 if the current state is a tie, 1 if the current state is a victory, 
 * and -1 if the current state is a defeat.  If the current state is not an
 * end state, <code>Integer.MIN_VALUE</code> is returned.  This evaluator is
 * subject to the horizon problem if a cutoff is used.  This is because
 * the evaluator pays no attention to what is about to happen if the state is
 * non-terminal.  
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
	 * @return if the current state is a tie, 0 is returned; if the current 
	 * state is a victory for the "maxPlayer" 1 is returned; if the current 
	 * state is a loss for the "maxPalyer" -1 is returned; finally, if the 
	 * current state is not an end state, <code>Integer.MIN_VALUE</code> is 
	 * returned.
	 */
	public int evaluate(IBoard board, IPlayer maxPlayer)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.Evaluator");
		logger.entering("edu.lhup.ai.Evaluator", "evaluate", 
						"\n" + board.toString());

		int rating = Integer.MIN_VALUE;
		if (board.getWinner() == null)
		{
			rating = 0;
			logger.finest("Tie, rating is " + rating);			
		}
		else if (board.getWinner() == maxPlayer)
		{
			rating = 1;
			logger.finest("Winner, rating is " + rating);			
		}
		else
		{
			rating = -1;
			logger.finest("Loser, rating is " + rating);			
		}

		logger.exiting("edu.lhup.ai.Evaluator", "evaluate", new Integer(rating));
		return rating;
	}

	public String toString()
	{
		return "1:win, -1:loss, 0:tie, 0:other";
	}
}
