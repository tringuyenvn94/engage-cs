package edu.lhup.ai.tictactoe;

import edu.lhup.ai.*;
						   
/**
 * Represents a location on the {@link Board tic-tac-toe} board that contains 
 * no piece at all. 
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class EmptyPiece implements IPiece
{
	EmptyPiece() {}

	public String toString()
	{
		return ".";
	}			 
}
