package edu.lhup.ai.othello;

import edu.lhup.ai.*;
import java.util.*;

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
public class Board implements IBoard
{
	static final int MAX_DIMENSION = 4;
	static final int BWINS = 1;
	static final int WWINS = 2;
	static final int TIE = 3;
	static final int BTURN = 4;
	static final int WTURN = 5;

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
			String row = tokenizer.nextToken();
			String col = tokenizer.nextToken();

			Move lastMove = (Move)peekMove();
			IPiece piece = (lastMove != null && 
						    lastMove.getPiece() == bPiece()) ? wPiece() : bPiece();

			move = getMove(piece, 
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

		// throws an exception if move is illegal... 
		legalMove(othelloMove);
		
		m_board[othelloMove.getRow()][othelloMove.getCol()] = 
			othelloMove.getPiece();

		flipInAllDirections(move);
		
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
			unflipInAllDirections(othelloMove);
			
			m_board[othelloMove.getRow()][othelloMove.getCol()] = 
				emptyPiece();
			
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
				out.append(m_board[row][col].toString());
			}
			out.append(System.getProperty("line.separator"));
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
			if (lastMove.getPiece() == othelloMove.getPiece())
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

		boolean bLegal = false;

		if (!bLegal)
			bLegal = legalMove(m_zero, m_right, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_zero, m_left, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_up, m_zero, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_down, m_zero, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_up, m_left, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_down, m_left, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_up, m_right, othelloMove);
		if (!bLegal)
			bLegal = legalMove(m_down, m_right, othelloMove);
		
		if (!bLegal)
		{
			throw new StateException("Illegal move " + move + 
									 ", you must surround the oposing player's pieces");
		}
	}
	
//---------------

	static IPiece emptyPiece()
	{ return m_emptyPiece; }

	static IPiece bPiece()
	{ return m_bPiece; }

	static IPiece wPiece()
	{ return m_wPiece; }

	static StringBuffer createStringBuffer()
	{ 
		m_buffer.setLength(0);
		return m_buffer;
	}

	static Move getMove(IPiece piece, int row, int col)
	{ 
		Move move = null;
		String strKey = piece.toString() + row + col;
		if (m_moveMap.containsKey(strKey))
		{
			move = (Move)m_moveMap.get(strKey);
		}
		else
		{
			move = new Move(piece, row, col);
			m_moveMap.put(strKey, move);
		}
		return move;
	}

//---------------

	private void updateState(Move move)
	{
		// if there are no pieces, we are in the starting state...
		m_state = (m_moveStack.size() == 0) ? BTURN : THINKING;

		// game is over when there are no more legal moves...
		if (m_state == THINKING)
		{
			// TODO: That do I do in this state???
			/*
			for (int row = 0; row < MAX_DIMENSION; row++)
			{
				for (int col = 0; col < MAX_DIMENSION; col++)
				{
					if (m_board[row][col] == emptyPiece())
						break;
				}
			}
			*/
		}

		// if nobody has one, and it is not a tie, determine who goes next...
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

	private IPlayer createPlayer(String strPlayer) throws StateException
	{
		try
		{
			Class cls = Class.forName(strPlayer);
			return (IPlayer)cls.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new StateException("Illegal Player: " + strPlayer, e);
		}
		catch (IllegalAccessException e)
		{
			throw new StateException("Illegal Player: " + strPlayer, e);
		}
		catch (ClassNotFoundException e)
		{
			throw new StateException("Illegal Player: " + strPlayer, e);
		}
	}

	private boolean legalMove(IDelta dRow, IDelta dCol, Move move)
	{
		boolean bMiddleFound = false;
		boolean bTerminalFound = false;
		IPiece opposingPiece = 
			(move.getPiece() == Board.bPiece()) ? Board.wPiece() : Board.bPiece(); 
		
		int row = dRow.delta(move.getRow());
		int col = dCol.delta(move.getCol());
		
		while (row != IDelta.LIMIT && col != IDelta.LIMIT && !bTerminalFound)
		{
			if (m_board[row][col] == emptyPiece())
				break;
			
			if (m_board[row][col] == opposingPiece)
				bMiddleFound = true;
			
			if (m_board[row][col] == move.getPiece())
				bTerminalFound = true;
			
			row = dRow.delta(row);
			col = dCol.delta(col);
		}
		return (bMiddleFound && bTerminalFound);
	}
	
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

	private void unflipInAllDirections(IMove move)
	{
		// TODO: and unflip in all direction code... 
	}

	private void flip(IDelta dRow, IDelta dCol, Move move)
	{
		int row = dRow.delta(move.getRow());
		int col = dCol.delta(move.getCol());		
		
		while (row != IDelta.LIMIT -1 && col != IDelta.LIMIT && 
			   shuoldFlip(dRow, dCol,(Move)move) && 
			   !isFlipTerminalPiece(row, col, move.getPiece())) 
		{
			m_board[row][col] = move.getPiece();
			col = dCol.delta(col);
			row = dRow.delta(row);
		}
	}

	private boolean shuoldFlip(IDelta dRow, IDelta dCol, Move move)
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
	
	private boolean isFlipTerminalPiece(int row, int col, IPiece piece)
	{
		if (m_board[row][col] == m_emptyPiece)
			return true;
		else if (m_board[row][col] == piece)
			return true;
		else
			return false;
	}

	private boolean isNextPieceTerminal(int row, int col, IPiece piece)
	{
		if (col >= MAX_DIMENSION) 
			return true;
		else if (row >= MAX_DIMENSION)
			return true;
		else if (col < 0)
			return true;
		else if (row < 0)
			return true;
		else if (m_board[row][col] != piece)
			return true;
		else
			return false;
	}

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
