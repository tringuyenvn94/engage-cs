package edu.lhup.ai;

import java.util.*;
import java.text.*;
import java.util.logging.*;

/**
 * A concrete player that plays any {@link IBoard game} using the minimax
 * algorithm with alpha-beta pruning and a cutoff.  The code used by this 
 * player is based on the psuedo code presented in Russell and Norvig's book,
 * "Artificial Intelligence: A Modern Approach".  The 
 * {@link IEvaluator evaluator} used by this player (see 
 * {@link #setEvaluator(IEvaluator)}) is important because it determines how the 
 * end states are evaluated during the minimax search.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class MinMaxAlphaBetaPlayer implements IPlayer
{
	public MinMaxAlphaBetaPlayer()
	{
		m_format = NumberFormat.getNumberInstance();
		m_format.setMaximumFractionDigits(3);
		m_format.setMinimumFractionDigits(3);
	}

	public void setCutoff(int cutoff)
	{
		m_cutoff = cutoff;
	}

	public void setEvaluator(IEvaluator evaluator)
	{
		m_evaluator = evaluator;
	}

	public String getDescription()
	{
		return "Computer based player.  Moves are selected using the minimax " + 
			   "algorithm with alpha-beta pruning.  Evaluator: " + m_evaluator;
	}

	public String getShortDescription()
	{
		return "Computer";
	}

	public String toString()
	{
		return getShortDescription();
	}

	/**
	 * This player makes moves using the minimax algorithm with alpha-beta
	 * pruning and a cutoff.  The {@link IEvaluator evaluator} used by this
	 * player determines how end states will be evaluated during the minimax
	 * search.
	 * 
	 * @param board the {@link IBoard} object that this player will make a move
	 * on.
	 * 
	 * @return the {@link IMove move} taken by the player.
	 * 
	 * @throws StateException if the player attempts to push an invalid
	 * move onto the games stack.
	 */
	public IMove takeTurn(IBoard board)
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.MinMaxAlphaBetaPlayer");
		logger.entering("edu.lhup.ai.MinMaxAlphaBetaPlayer", "takeTurn", 
						"\n" + board.toString());
		
		System.out.println("MINMAX Start\n" + board);
		
		long start = System.currentTimeMillis();
		
		int bestRating = Integer.MIN_VALUE;
		IMove bestMove = null;
		List moves = new LinkedList();
		board.moves(moves);
		logger.finest("Testing " + moves.size() + " moves");

		for (int i = 0; i < moves.size(); i++)
		{
			m_totalMoves++;

			IMove move = (IMove)moves.get(i);
			try
			{
				logger.finest("Pushing move " + board);
				System.out.println("Pushing move \n" + board);
				board.pushMove(move);
				System.out.println("Board After push\n" + board);
				int currentRating = 
					getRating(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
				logger.finest("Rating for move " + 
							  move + " is " + currentRating);
				logger.finest("Poping move " + move);
				System.out.println("Poping move\n" + board);
				board.popMove();
				System.out.println("Board After pop\n" + board);
				logger.finest("Best rating so far is " + bestRating);
				if (currentRating > bestRating)
				{
					bestMove = move;
					bestRating = currentRating;
					logger.finest("Better rating found, new best move is " + 
								 bestMove);
				}
			}
			catch (StateException e)
			{
				System.out.println("Move failed in MinMaxPlayer");
				System.out.println("Move = " + move);
				System.out.println("Board \n" + board);
			}
		}
		logger.finest("Best move found, board is\n" + board);
		logger.finest("Best move is " + bestMove);
		try
		{
			board.pushMove(bestMove);
		}
		catch (StateException e)
		{
			System.out.println("Best Move failed in MinMaxPlayer");
			System.out.println("Best Move = " + bestMove);
			System.out.println("Board \n" + board);
		}
		
		long stop = System.currentTimeMillis();
		String totalMins = m_format.format(((stop-start)/1000.0/60.0));

		logger.fine("MIN/MAX Search Statistics: ");
		logger.fine("\t" + m_totalMoves + " moves examined");
		logger.fine("\t" + (m_deepestLevel+1) + " levels examined");
		logger.fine("\t" + "Search time: " + totalMins + " minutes");
		m_totalMoves = 0;
		m_deepestLevel = 0;

		logger.exiting("edu.lhup.ai.MinMaxAlphaBetaPlayer", "takeTurn", 
					   "\n" + board.toString());
		
		System.out.println("MINMAX Stop\n" + board);
		System.out.println("MINMAX Move " + bestMove);
		
		return bestMove;
	}

	protected boolean cutoff(IBoard board, int iLevel)
	{
		return (iLevel >= m_cutoff);
	}

	protected int getRating(IBoard board, int level, int alpha, int beta) 
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.MinMaxAlphaBetaPlayer");
		logger.entering("edu.lhup.ai.MinMaxAlphaBetaPlayer", "getRating", 
						"Level " + level + 
						" alpha " + alpha + 
						" beta " + beta + 
						"\n" + board.toString());

		int rating = Integer.MIN_VALUE;
		Iterator players = board.playerIterator();

		if (!cutoff(board, level) && players.hasNext())
		{
			m_deepestLevel = level;
			IPlayer player = (IPlayer)players.next();
			if (player == this)
			{
				logger.finest("MAX's move");
				int bestRating = Integer.MIN_VALUE;
				List moves = new LinkedList();
				board.moves(moves);
				
				System.out.println("Testing Moves on board \n" + board);
				for (int i = 0; i < moves.size(); i++)
					System.out.println("Move : " + moves.get(i));


				logger.finest("Testing " + moves.size() + " moves");
				for (int i = 0; i < moves.size(); i++)
				{
					m_totalMoves++;
					IMove move = (IMove)moves.get(i);
					try
					{
						logger.finest("Pushing move " + move);
						board.pushMove(move);
						int currentRating = getRating(board, level+1, alpha, beta);
						logger.finest("Rating for move " + 
									  move + " is " + currentRating);
						logger.finest("Poping move " + move);
						board.popMove();
						logger.finest("Best rating so far is " + bestRating);
	
						if (currentRating > bestRating)
						{
							bestRating = currentRating;
							logger.finest("Better rating found, new rating is " + 
										  bestRating);
						}
	
						if (currentRating >= beta)
						{
							logger.finest("Pruning MAX, current rating is: " + 
										  currentRating + " beta: " + beta);
							break;
						}
						
						alpha = bestRating;
						logger.finest("New alpha rating is " + alpha);
					}
					catch (StateException e)
					{
						System.out.println("Move failed in MinMaxPlayer");
						System.out.println("Move = " + move);
						System.out.println("Board \n" + board);
					}
				}
				logger.finest("Best rating " + bestRating);
				rating = bestRating;
			}
			else
			{
				logger.finest("MIN's move");
				int leastRating = Integer.MAX_VALUE;
				List moves = new LinkedList();
				board.moves(moves);

				logger.finest("Testing " + moves.size() + " moves");
				for (int i = 0; i < moves.size(); i++)
				{
					m_totalMoves++;

					IMove move = (IMove)moves.get(i);
					try
					{
						logger.finest("Pushing move " + move);
						board.pushMove(move);
						int currentRating = getRating(board, level+1, alpha, beta);
						logger.finest("Rating for move " + 
									  move + " is " + currentRating);
						logger.finest("Poping move " + move);
						board.popMove();
						logger.finest("Least rating so far is " + leastRating);
	
						if (currentRating < leastRating)
						{
							leastRating = currentRating;
							logger.finest("Lesser rating found, new rating is " + 
										 leastRating);
						}
	
						if (currentRating <= alpha)
						{
							logger.finest("Pruning MIN, current rating is: " + 
										  currentRating + " alpha: " + alpha);
							break;
						}

						beta = leastRating;
						logger.finest("New beta rating is " + beta);
					}
					catch (StateException e)
					{
						System.out.println("Move failed in MinMaxPlayer");
						System.out.println("Move = " + move);
						System.out.println("Board \n" + board);
					}
				}
				logger.finest("Least rating " + leastRating);
				rating = leastRating;
			}
		}
		else
		{
			logger.finest("Terminal state reached, evaluate");
			rating = m_evaluator.evaluate(board, this);
			logger.finest("Evaluation returned " + rating);
		}

		logger.exiting("edu.lhup.ai.MinMaxAlphaBetaPlayer", "getRating", 
					   new Integer(rating));
		return rating;
	}

	protected int m_cutoff = Integer.MAX_VALUE;
	protected IEvaluator m_evaluator;

	private NumberFormat m_format;
	private long m_totalMoves = 0;
	private int m_deepestLevel = 0;
}