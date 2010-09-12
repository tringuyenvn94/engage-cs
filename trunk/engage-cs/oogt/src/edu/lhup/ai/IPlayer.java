package edu.lhup.ai;

/**
 * The abstract representation of a player.  A player is capable of playing any
 * game that implements the {@link IBoard} interface.  
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public interface IPlayer
{
	/**
	 * @return a description of this player.
	 */
	public String getDescription();

	/**
	 * @return a short description of this player.
	 */
	public String getShortDescription();

	/**
	 * Provides a mechanism for specifying a player's evaluation strategy.  
	 * By implementing the {@link IEvaluator} interface programmers can create
	 * their own custom evaluators and request that a player use the evaluator
	 * by calling this method.
	 * 
	 * @param evaluator the strategy used by this player in order to 
	 * evaluate the current game state. 
	 */
	public void setEvaluator(IEvaluator evaluator);

	/**
	 * Provides a mechanism for specifying the point at which a player's 
	 * abandons the search for the best move.
	 * 
	 * @param cutoff an integer limiting the duration of a player's search for
	 * the best move
	 */
	public void setCutoff(int cutoff);

	/**
	 * Asks this player to push a move onto the specified game's stack.  The 
	 * game must be an implementation of {@link IBoard}.
	 * 
	 * @param board the {@link IBoard} object that this player will make a move
	 * on.
	 *
	 * @throws TurnException if the player attemts to push an invalid
	 * move onto the games stack.
	 */
	public void takeTurn(IBoard board) throws TurnException;
}