package edu.lhup.ai.othello;


import edu.lhup.ai.*;
import java.util.*;

import javax.print.attribute.standard.MediaSize.Other;

// TODO: General issue is that, unlike tic-tac-toe, a player may have to pass a turn
// if they can't move, the game ends if both players have to pass their turns...
// how to implement this??

/**
 * A concrete board implementation that implements the rules of othello.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
abstract public class Board implements IBoard
{
	public static final int MAX_DIMENSION = 6;
	public static final int BWINS = 1;
	public static final int WWINS = 2;
	public static final int TIE = 3;
	public static final int BTURN = 4;
	public static final int WTURN = 5;
	public static final String SKIP = "-99";

	private static final int THINKING = 6;

	/**
	 * Creates an empty Board object w/o players
	 */
	public Board()
	{
		emptyTheArray();
	}

	/**
	 * Creates an empty Board object.
	 */
	public Board(IPlayer[] players) throws StateException
	{
		setPlayers(players);
		emptyTheArray();
	}

	/**
	 * Creates a new Board object from an array of moves.
	 */
	public Board(IMove[] moves, IPlayer[] players) throws StateException
	{
		this(players);
		for (int move = 0; move < moves.length; move++)
		{
			pushMove(moves[move]);
		}
	}

	/**
	 * Creates a new Board object based on an existing Board
	 * object.  The new object is an exact copy of the existing object.
	 */
	public Board(Board other) throws StateException
	{
		for (int row = 0; row < MAX_DIMENSION; row++)
		{
			for (int col = 0; col < MAX_DIMENSION; col++)
			{
				m_board[row][col] = other.m_board[row][col];
			}
		}

		setPlayers(other.m_players);
		m_moveStack.clear();
		m_moveStack.addAll(other.m_moveStack);
		m_state = other.m_state;
	}

	public void setPlayers(IPlayer[] players) throws StateException
	{
		if (players.length != 2)
		{
			throw new StateException("Othello involves only  two players"); 
		}

		m_players[0] = players[0];
		m_players[1] = players[1];
	}

	public IPlayer[] getPlayers()
	{
		return m_players;
	}

	public Iterator playerIterator()
	{
		return new PlayerIterator(this);
	}

	public IPlayer getWinner()
	{
		IPlayer winner = null;
		if (getState() == BWINS)
		{
			winner = m_players[0];
		}
		else if (getState() == WWINS)
		{
			winner = m_players[1];
		}
		return winner;
	}

	public IMove peekMove()
	{
		return (m_moveStack.size() == 0) ? null : (IMove)m_moveStack.getFirst();
	}

	/**
	 * Adds the specified {@link IMove move} to this board's stack.
	 * The move is specified using a string representation
	 * of the move.  For example the move "2,1" would place a piece at
	 * row 2, column 1.  The piece placed on the board will be the piece 
	 * that is associated with the player who's turn is next. For example,
	 * if it is the "B player's" turn then an B piece will be placed on the 
	 * board.
	 *
	 * <p>
	 * The first move must be made by the "B player".  Moves
	 * must alternate between the "B player" and the "W player".  Finally,
	 * moves can only be made on an space that surrounds at least one piece of 
	 * the opposite color (e.g. -WWWWWB).  When a move is made, all pieces that
	 * are surrounded by the new piece will flip from the opposing color to
	 * the current player's color.  If opponent pieces are surrounded in more
	 * than one direction, then all pieces in all those directions will flip.  
	 *
	 * @param strMove the {@link IMove move} that will be added to the stack.
	 *
	 * @return an {@link IMove object} representation of the move.
	 *
	 * @throws StateException if the specified move is illegal.
	 */
	public IMove pushMove(String strMove) throws StateException
	{
		IMove move = null;

		try
		{
			StringTokenizer tokenizer = new StringTokenizer(strMove, ",");
			String row = SKIP;
			String col = SKIP;

			// see if the player wants to skip the turn, in othello this can be required
			// if there is no possible move, skipped moves are represented by a move 
			// with SKIP as the value for row and col, in addition skipped moves do 
			// not cause any action on the game board...
			if (!strMove.toUpperCase().equals("SKIP"))
			{
				row = tokenizer.nextToken();
				col = tokenizer.nextToken();
			}
			
			Move lastMove = (Move)peekMove();
			IPiece piece = (lastMove != null && 
						    lastMove.getPiece() == bPiece()) ? wPiece() : bPiece();

			move = new Move(piece,
				 		    Integer.parseInt(row), 
				 		    Integer.parseInt(col));

			pushMove(move);
		}
		catch (NumberFormatException e)
		{
			throw new StateException("Illegal move (" 
						 + strMove + 
						 "), please use the following format: row,col");
		}
		catch (NoSuchElementException e)
		{
			throw new StateException("Illegal move (" 
						 + strMove + 
						 "), please use the following format: row,col");
		}
		return move;
	}

	/**
	 * Adds the specified {@link IMove move} to this board's stack.
	 * The first move must be made by the "B player".  Moves
	 * must alternate between the "B player" and the "W player".  Finally,
	 * moves can only be made on an space that surrounds at least one piece of
	 * the opposite color (e.g. -WWWWWB).  When a move is made, all pieces that
	 * are surrounded by the new piece will flip from the opposing color to
	 * the current player's color.  If opponent pieces are surrounded in more
	 * than one direction, then all pieces in all those directions will flip.
	 *
	 * @param strMove the {@link IMove move} that will be added to the stack.
	 *
	 * @throws StateException if the specified move is illegal.
	 */
	public void pushMove(IMove move) throws StateException
	{
		Move othelloMove = (Move)move;

		// if min max has no valid move he will pass null, this should be 
		// converted to a request to skip the turn...
	    if (othelloMove == null)
	    {
			Move lastMove = (Move)peekMove();
			IPiece piece = (lastMove != null && 
						    lastMove.getPiece() == bPiece()) ? wPiece() : bPiece();
	    	othelloMove = new Move(piece, Integer.parseInt(SKIP), Integer.parseInt(SKIP));
		}

		// see if the player wants to skip the turn, in othello this can be required
		// if there is no possible move, skipped moves are represented by a move 
		// with SKIP as the value for row and col, in addition skipped moves do 
		// not cause any action on the game board...
		if (othelloMove.getRow() != Integer.parseInt(SKIP))
		{
			// throws an exception if move is illegal... 
			legalMove(othelloMove);
	
			m_board[othelloMove.getRow()][othelloMove.getCol()] = 
				othelloMove.getPiece();
	
			flipInAllDirections(move);
		}
		
		m_moveStack.addFirst(move);
		updateState(othelloMove);
	}

	public IMove popMove()
	{
		Move othelloMove = 
			(Move)m_moveStack.removeFirst();
		if (m_board[othelloMove.getRow()][othelloMove.getCol()] != 
			emptyPiece())
		{
			// see if we are popping a skipped move, skipped moves do 
			// not cause any action on the game board, so no action should take
			// place when the move is popped...
			if (othelloMove.getRow() != Integer.parseInt(SKIP))
			{
				unflipInAllDirections(othelloMove);
				
				m_board[othelloMove.getRow()][othelloMove.getCol()] = 
					emptyPiece();
			}
			
			System.out.println("After POP " + othelloMove);
			System.out.println(this);
			
			updateState(othelloMove);
		}
		else
		{
			throw new IllegalStateException
				("Attempt to undo a move, but the square specified by " + 
				 "the move is empty!");
		}
		return null;
	}

	public IPiece[][] getBoard()
	{ 
		IPiece[][] copy = new IPiece[MAX_DIMENSION][MAX_DIMENSION];
		for (int iRow = 0; iRow < MAX_DIMENSION; iRow++)
		{
			for (int iCol = 0; iCol < MAX_DIMENSION; iCol++)
			{
				copy[iRow][iCol] = m_board[iRow][iCol];
			}
		}
		return copy; 
	}

	public int getState()
	{ return m_state; }

	public void resetState()
	{
		emptyTheArray();
		m_state = BTURN;
		m_moveStack.clear();
	}

	public Iterator moveIterator()
	{
		return new MoveIterator(this);
	}

	public void moves(Collection col)
	{
		col.clear();
		Iterator moveIterator = moveIterator();
		while (moveIterator.hasNext())
		{
			col.add(moveIterator.next());
		}
	}

	public String getDescription()
	{
		return "Othello";
	}

	public String getShortDescription()
	{
		return "Othello";
	}

	public String toString()
	{
		StringBuffer out = createStringBuffer();
		for (int row = 0; row < MAX_DIMENSION; row++)
		{
			for (int col = 0; col < MAX_DIMENSION; col++)
			{
				out.append(m_board[row][col].toString() +"\t");
			}
			out.append(System.getProperty("line.separator")+"\n");
		}
		return out.toString();
	}

	public boolean equals(Object other)
	{
		// same object?
		if (this == other)
			return true;

		// different type of object?
		if (!(other instanceof Board))
			return false;

		boolean bEquals = true;
		Board otherBoard = (Board)other;
		for (int iRow = 0; iRow < MAX_DIMENSION && bEquals; iRow++)
		{
			for (int iCol = 0; iCol < MAX_DIMENSION && bEquals; iCol++)
			{													   
				bEquals = (m_board[iRow][iCol] == 
						   otherBoard.m_board[iRow][iCol]);
			}
		}
		
		return bEquals;
	}

	public int hashCode()
	{
		int result = 17;
		result = 37 * result + m_state;
		for (int iRow = 0; iRow < MAX_DIMENSION; iRow++)
		{
			for (int iCol = 0; iCol < MAX_DIMENSION; iCol++)
			{													   
				result = 37 * result + m_board[iRow][iCol].hashCode();
			}
		}
		return result;
	}

	// throws an exception if, for some reason, the specified move
	// is not legal for the the current player...
	public void legalMove(IMove move) throws StateException
	{
		Move othelloMove = (Move)move;

		if ( (m_moveStack.size() == 0) && 
				 (othelloMove.getPiece() != bPiece()) )
		{
			throw new StateException("Illegal move, " + 
									 "first move must be made by B");
		}

		if ( (othelloMove.getRow() < 0) || (othelloMove.getRow() > MAX_DIMENSION-1) || 
			 (othelloMove.getCol() < 0) || (othelloMove.getCol() > MAX_DIMENSION-1) )
		{
			throw new StateException("Illegal move, move is off the board");
		}

		if (m_moveStack.size() > 0)
		{
			Move lastMove = (Move)peekMove();
			if (lastMove != null && lastMove.getPiece() == othelloMove.getPiece())
			{
				throw new StateException
					("Illegal move, same player cannot make two moves " + 
					 "in a row");
			}
		}

		if (m_board[othelloMove.getRow()][othelloMove.getCol()] != 
			emptyPiece())
		{
			throw new StateException("Illegal move " + move + 
									 ", the square is already occupied");
		}

		boolean bLegal = isLegalMove(othelloMove);
		if (!bLegal)
		{
			throw new StateException("Illegal move " + move + 
									 ", you must surround the oposing player's pieces");
		}
	}
	
	// counts the number of pieces on the board that are 
	// similar to the specified piece...   		
	int countPieces(IPiece p)
	{
		int iRet = 0;
		for (int row = 0; row < MAX_DIMENSION; row++)
		{
			for (int col = 0; col < MAX_DIMENSION; col++)
			{
				if (m_board[row][col] == p)
					iRet++;
			}
		}
		return iRet;
	}
	
//---------------

	public static IPiece emptyPiece()
	{ return m_emptyPiece; }

	public static IPiece bPiece()
	{ return m_bPiece; }

	public static IPiece wPiece()
	{ return m_wPiece; }

	static StringBuffer createStringBuffer()
	{ 
		m_buffer.setLength(0);
		return m_buffer;
	}

//---------------

	private void updateState(Move move)
	{
		// if there are no pieces, we are in the starting state...
		m_state = (m_moveStack.size() == 0) ? BTURN : THINKING;

		// game is over when there are no more legal moves for anyone,
		// not just the current player...
		if (m_state == THINKING)
		{
			Move m = null;
			boolean bSuccess = false;
			for (int row = 0; row < MAX_DIMENSION && !bSuccess; row++)
			{
				for (int col = 0; col < MAX_DIMENSION && !bSuccess; col++)
				{
					if (m_board[row][col] == emptyPiece())
					{
						m = new Move(m_bPiece, row, col);
						bSuccess = isLegalMove(m);
						if (!bSuccess)
						{
							m = new Move(m_wPiece, row, col);
							bSuccess = isLegalMove(m);
						}
					}
				}
			}
			
			// we found a legal move for one of the players so allow
			// the game to continue
			if (bSuccess)
			{
				m_state = THINKING;
			}
			else
			{
				// no legal moves for either player, so count the pieces and
				// and determine who wins!...
				int bcnt = countPieces(m_bPiece);
				int wcnt = countPieces(m_wPiece);
				if (bcnt == wcnt)
					m_state = TIE;
				else if (bcnt > wcnt)
					m_state = BWINS;
				else 
					m_state = WWINS;
			}
		}

		// if nobody has won, and it is not a tie, determine who goes next...
		if (m_state == THINKING)
		{
			m_state = (move.getPiece() == bPiece()) ? WTURN : BTURN;
		}
	}

	private void emptyTheArray()
	{
		int loc = (MAX_DIMENSION/2) - 1;
		
		for (int iRow = 0; iRow < MAX_DIMENSION; iRow++)
		{
			for (int iCol = 0; iCol < MAX_DIMENSION; iCol++)
			{
				m_board[iRow][iCol] = emptyPiece();
			}
		}
		m_board[loc][loc] = Board.bPiece();
		m_board[loc][loc+1] = Board.wPiece();
		m_board[loc+1][loc] = Board.wPiece();
		m_board[loc+1][loc+1] = Board.bPiece();
	}

	// this method flips the pieces surrounded by the specified move, 
	// flips take place in all directions that have surrounded pieces...
	private void flipInAllDirections(IMove move)
	{
		System.out.println("Before Flip " + move);
		System.out.println(this);
		
		Move othelloMove = (Move)move;

		// flip right...
		flip(m_zero, m_right, othelloMove);
		
		// flip left...
		flip(m_zero, m_left, othelloMove);

		// flip down...
		flip(m_down, m_zero, othelloMove);
		
		// flip up ...
		flip(m_up, m_zero, othelloMove);

		// flip down left ...
		flip(m_down, m_left, othelloMove);

		// flip down right ...
		flip(m_down, m_right, othelloMove);

		// flip up left ...
		flip(m_up, m_left, othelloMove);

		// flip up right ...
		flip(m_up, m_right, othelloMove);
		
		System.out.println("After Flip " + move);
		System.out.println(this);
		
	}

	// this method unflips pieces that were flipped by the specified move, 
	// flips are recorded in the move so they are easy to undo...
	private void unflipInAllDirections(IMove move)
	{
		System.out.println("Before UnFlip " + move);
		System.out.println(this);
		
		Move othelloMove = (Move)move;
		
		// just unflip the pieces that were recorded as flipped 
		// when the the move was enacted...
		for (Move m : othelloMove.getFlipped())
		{
			System.out.println("Flipping row " + m.getRow() + " col " + m.getCol());
			m_board[m.getRow()][m.getCol()] = move.getPiece() == m_bPiece ? m_wPiece : m_bPiece;
		}
		
		System.out.println("After UnFlip " + move);
		System.out.println(this);
	}

	// flips pieces from a move along a specified axis...
	private void flip(IDelta dRow, IDelta dCol, Move move)
	{
		int row = dRow.delta(move.getRow());
		int col = dCol.delta(move.getCol());		
		
		while (row != IDelta.LIMIT -1 && col != IDelta.LIMIT && 
			   shouldFlip(dRow, dCol,(Move)move) && 
			   !isFlipTerminalPiece(row, col, move.getPiece())) 
		{
			System.out.println("Flipping row " + row + " col " + col);
			move.addFlipped(row, col, move.getPiece());
			m_board[row][col] = move.getPiece();
			col = dCol.delta(col);
			row = dRow.delta(row);
		}
	}

	// determines if a flip can take place along a specified axis
	// from the specified move, flips can only take place if the move
	// surrounds opponent pieces...
	private boolean shouldFlip(IDelta dRow, IDelta dCol, Move move)
	{
		boolean bRet = false;
		int col = dCol.delta(move.getCol());
		int row = dRow.delta(move.getRow());

		while (row != IDelta.LIMIT && col != IDelta.LIMIT)
		{
			// found similar piece without finding any spaces...
			if (m_board[row][col] == move.getPiece())
			{
				bRet = true;
				break;
			}
			// found a space...
			else if (m_board[row][col] == m_emptyPiece)
			{
				bRet = false;
				break;
			}
			
			col = dCol.delta(col);
			row = dRow.delta(row);
		}
		
		return bRet;
	}

	// this method returns when to terminate when checking along an axis
	// to see if we can flip, empty pieces or similar pieces terminate 
	// the search... 
	private boolean isFlipTerminalPiece(int row, int col, IPiece piece)
	{
		if (m_board[row][col] == m_emptyPiece)
			return true;
		else if (m_board[row][col] == piece)
			return true;
		else
			return false;
	}

	abstract protected boolean isLegalMove(Move othelloMove);

	private IDelta m_left = new DeltaLeft(0, MAX_DIMENSION-1);
	private IDelta m_right = new DeltaRight(0, MAX_DIMENSION-1);
	private IDelta m_up = new DeltaUp(0, MAX_DIMENSION-1);
	private IDelta m_down = new DeltaDown(0, MAX_DIMENSION-1);
	private IDelta m_zero = new DeltaZero(0, MAX_DIMENSION-1);	
	
	private int m_state = BTURN;
	private final LinkedList m_moveStack = new LinkedList();
	private final IPiece[][] m_board = new IPiece[MAX_DIMENSION][MAX_DIMENSION];
	private final IPlayer[] m_players = new IPlayer[2];

	private static final StringBuffer m_buffer = new StringBuffer();
	private static final IPiece m_bPiece = new BPiece();
	private static final IPiece m_wPiece = new WPiece();
	private static final IPiece m_emptyPiece = new EmptyPiece();
	private static final HashMap m_moveMap = new HashMap();
}
