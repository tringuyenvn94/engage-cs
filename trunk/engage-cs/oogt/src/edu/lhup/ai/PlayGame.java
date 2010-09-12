package edu.lhup.ai;
import java.util.*;
import java.util.logging.*;
import java.io.*;

/**
 * Run this class in order to play a game!  You will need a 
 * {@link Factory configuration file} that contains the information specifying 
 * the game you want to play, and the player's that you want to play.  Once
 * you have created a configuration file you can play the game as follows:
 * 
 * <pre>
 *   java -cp ai.jar edu.lhup.ai.PlayGame PlayGameConfig.xml
 * </pre>
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
class PlayGame
{
	/**
	 * The main method that launches a game.
	 * 
	 * @param args a String array that contains the command line arguments.
	 * This method expects a single command line argument that contains the 
	 * complete path and filename for a {@link Factory configuration file}. 
	 *
	 * @throws IOException if the configuration file cannot be found.
	 * @throws StateException if the configuration file specifies an invalid
	 * game or players or an invalid turn is made by either of the players
	 */
	public static void main(String[] args) 
		throws StateException, IOException, StateException
	{
		if (args.length != 1)
		{
			System.out.println("Please specify a configuration file.");
			System.exit(0);
		}

		IFactory factory = new Factory();
		IBoard board = factory.getBoardInstance(args[0]);
		IGameInterface gameInterface = factory.getGameInterface(args[0]);
		
		gameInterface.init(board);
		gameInterface.playGame();
	}
}