package edu.lhup.ai;

import java.util.*;

/**
 * The abstract representation of a game.  A board keeps track of a stack of 
 * {@link IMove moves} and changes its internal state as moves are pushed and 
 * popped from the stack.  The board is also responsible for keeping track of
 * the players that are playing the game and the order in which they take turns.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public interface IBoard
{
	/**
	 * @return an array of pieces currently on this board.
	 */
	public IPiece[][] getBoard();

	/**
	 * Translates the specified String into a {@link IMove move} and then 
	 * adds that move onto this board's stack.
	 *
	 * @param strMove a string representation of a move.  A move is created
	 * based on this string, and that move will be added to the stack.
	 *
	 * @return an {@link IMove object} representation of the move.
	 *
	 * @throws StateException if the specified move string could not be 
	 * translated into an {@link IMove} object, or the move is illegal.
	 */
	public IMove pushMove(String strMove) throws StateException;

	/**
	 * Adds the specified {@link IMove move} to this board's stack.
	 *
	 * @param strMove the {@link IMove move} that will be added to the stack.
	 *
	 * @throws StateException if the specified move is illegal.
	 */
	public void pushMove(IMove move) throws StateException;

	/**
	 * Removes the last {@link IMove move} from this board's stack.
	 *
	 * @return the move that was removed from the stack.
	 *
	 * @throws StateException if the stack is empty.
	 */
	public IMove popMove() throws StateException;

	/**
	 * @return a reference to the last move that was added to this
	 * board's stack,
	 *
	 * @throws StateException if the stack is empty.
	 */
	public IMove peekMove() throws StateException;

	/**
	 * Specifies the {@link IPlayer players} that will be playing this game.
	 *
	 * @param players an array of {@link IPlayer players} that will be playing
	 * the game.
	 *
	 * @throws StateException if the specified number of type of players cannot
	 * play this game.
	 */
	public void setPlayers(IPlayer[] players) throws StateException;

	/**
	 * @return the players currently playing this game.
	 */
	public IPlayer[] getPlayers();

	/**
	 * @return an {@link java.util.Iterator Iterator} of the players currently 
	 * playing this game.  The players will be iterated in the order in which 
	 * they are allowed to take their turns.  The iterator will be empty if 
	 * this game has ended.
	 */
	public Iterator playerIterator();

	/**
	 * @return the {@link IPlayer player} that has won this game, or 
	 * <code>null</code> if there is currently no winner, or if this game
	 * has ended in a tie.
	 */
	public IPlayer getWinner();

	/**
	 * @return a integer representation of this games current state.
	 */
	public int getState();

	/**
	 * Returns this board to its initial state so it will be ready for a new
	 * game.
	 */
	public void resetState();

	/**
	 * @return an {@link java.util.Iterator Iterator} of the set of all 
	 * currently legal {@link IMove moves}.
	 */
	public Iterator moveIterator();

	/**
	 * Populates the specified {@link Collection} with the set of all 
	 * currently legal {@link IMove moves}.
	 *
	 * @param col the {@link Collection} that will be populated with the set of
	 * all currently legal {@link IMove moves}.
	 */
	public void moves(Collection col);

	/**
	 * @return a description of this board.
	 */
	public String getDescription();

	/**
	 * @return a short description of this player.
	 */
	public String getShortDescription();
}
