package edu.lhup.ai;

/**
 * The abstract representation of a game interface that provides
 * a means for input/output for the players.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public interface IGameInterface
{
	/**
	 * Allows the interface to perform any initialization before the 
	 * game begins.
	 * 
	 * @param board the game board that this interface will interact
	 * with.
	 */
	public void init(IBoard board);

	/**
	 * Displays the interface and starts the game.  This method should not return until the 
	 * game is complete.
	 */
	public void playGame();	
}
