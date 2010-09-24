package edu.lhup.ai.othello;

import edu.lhup.ai.*;

/**
 * Represents a location on the {@link Board othello} board that contains 
 * a W. 
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class WPiece implements IPiece
{
	WPiece() {}

	public String toString()
	{
		return "W";
	}
}
