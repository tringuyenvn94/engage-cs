package edu.lhup.ai.othello;

import edu.lhup.ai.*;
						   
/**
 * Represents a location on the {@link Board othello} board that contains 
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
