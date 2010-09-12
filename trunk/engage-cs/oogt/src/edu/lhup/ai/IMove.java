package edu.lhup.ai;

/**
 * The abstract representation of a {@link IPlayer player's} move in a game.  
 * A move should be associated with a particular {@link IPiece piece}.  
 * All games are played by pushing moves onto the game stack.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public interface IMove
{
	/**
	 * @return the piece associated with this move.
	 */
	public IPiece getPiece();
}