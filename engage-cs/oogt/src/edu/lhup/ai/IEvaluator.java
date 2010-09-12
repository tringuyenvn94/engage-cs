package edu.lhup.ai;

/**
 * An abstract representation of an evaluator that encapsulates the strategy 
 * used by a {@link IPlayer player} to evaluate the state of a game.  
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public interface IEvaluator
{
	/**
	 * Provides a mechanism for evaluating the current state of a game.
	 * 
	 * @param board the game that will be evaluated.
	 * @param maxPlayer the player whos perspective will be used when 
	 * evaluating the specified game. 
	 *
	 * @return the score of the current state of the specified game from 
	 * the specified player's perspective.
	 */
	public int evaluate(IBoard board, IPlayer maxPlayer);
}