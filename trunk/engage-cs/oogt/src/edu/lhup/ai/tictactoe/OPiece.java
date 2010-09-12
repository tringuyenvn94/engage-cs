package edu.lhup.ai.tictactoe;

import edu.lhup.ai.*;

/**
 * Represents a location on the {@link Board tic-tac-toe} board that contains 
 * an O. 
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class OPiece implements IPiece
{
	OPiece() {}

	public String toString()
	{
		return "O";
	}
}
