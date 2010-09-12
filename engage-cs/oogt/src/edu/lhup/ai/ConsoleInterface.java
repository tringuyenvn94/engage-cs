package edu.lhup.ai;

import java.util.Iterator;
import java.util.Scanner;


/**
 * Provides a console based interface for any game.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class ConsoleInterface implements IGameInterface 
{
	public void init(IBoard board) 
	{
		m_board = board;
	}
	
	public void playGame()
	{
		System.out.println("Get ready to play " + 
				   m_board.getShortDescription() + "!\n");
		System.out.println("The players are: ");
		IPlayer[] players = m_board.getPlayers();
		for (int i = 0; i < players.length; i++)
		{
			System.out.println((i+1) + ") " + players[i].getDescription());
		}
		
		boolean bContinue = true;
		while (bContinue)
		{
			m_board.resetState();
			System.out.println("\nStarting Board:\n" + m_board);
			
			for (Iterator i = m_board.playerIterator(); i.hasNext(); )
			{
				IPlayer player = (IPlayer)i.next();
				System.out.println(player + ": ");
				boolean bValidMove = true;
				do
				{
					bValidMove = true;
					try
					{
						player.takeTurn(m_board);
					}
					catch (StateException e)
					{
						bValidMove = false;
						System.out.println(e.getMessage());
					}
				} while (!bValidMove);
				System.out.println("\n" + m_board);
			}
			
			if (m_board.getWinner() != null)
			{
				System.out.println("Winner: " + 
								   m_board.getWinner().getDescription()); 
			}			
			else			
			{				
				System.out.println("Tie!");
			}
			
			System.out.println("Play again? (Y/N)");
			Scanner scanner = new Scanner(System.in);
			String s = scanner.nextLine().toUpperCase();
			bContinue = (s != null && s.equals("Y"));
		}
	}
		
	private IBoard m_board = null;
}
