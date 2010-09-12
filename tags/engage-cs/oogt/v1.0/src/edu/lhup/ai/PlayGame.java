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
	 * game or players.
	 * @throws TurnException if an invalid turn is made by either of the 
	 * players	
	 */
	public static void main(String[] args) 
		throws StateException, IOException, TurnException
	{
		if (args.length != 1)
		{
			System.out.println("Please specify a configuration file.");
			System.exit(0);
		}

		IFactory factory = new Factory();
		IBoard board = factory.getBoardInstance(args[0]);
		System.out.println("Get ready to play " + 
						   board.getShortDescription() + "!\n");
		System.out.println("The players are: ");
		IPlayer[] players = board.getPlayers();
		for (int i = 0; i < players.length; i++)
		{
			System.out.println((i+1) + ") " + players[i].getDescription());
		}
		
		boolean bContinue = true;
		while (bContinue)
		{
			board.resetState();
			System.out.println("\nStarting Board:\n" + board);
			
			for (Iterator i = board.playerIterator(); i.hasNext(); )
			{
				IPlayer player = (IPlayer)i.next();
				System.out.println(player + ": ");
				player.takeTurn(board);
				System.out.println("\n" + board);
			}
			
			if (board.getWinner() != null)
			{
				System.out.println("Winner: " + 
								   board.getWinner().getDescription()); 
			}			
			else			
			{				
				System.out.println("Tie!");
			}
			
			System.out.println("Play again? (Y/N)");
			BufferedReader read =
					new BufferedReader(new InputStreamReader(System.in));
			String s = read.readLine().toUpperCase();
			bContinue = (s != null && s.equals("Y"));
		}
	}
}