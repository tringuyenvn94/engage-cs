package edu.lhup.ai.tictactoe;

import edu.lhup.ai.*;
import java.util.*;

/**
 * A concrete board implementation that implements the rules of tic-tac-toe.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class Board implements IBoard
{
	static final int XWINS = 1;
	static final int OWINS = 2;
	static final int TIE = 3;
	static final int XTURN = 4;
	static final int OTURN = 5;

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
		for (int row = 0; row < 3; row++)
		{
			for (int col = 0; col < 3; col++)
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
			throw new StateException("Tic-Tac-Toe involves only  two players"); 
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
		if (getState() == XWINS)
		{
			winner = m_players[0];
		}
		else if (getState() == OWINS)
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
	 * that is assocated with the player whos turn is next. For example,
	 * if it is the "X player's" turn then an X piece will be placed on the 
	 * board.
	 *
	 * <p>
	 * The first move must be made by the "X player".  Moves
	 * must alternate between the "X player" and the "O player".  Finally,
	 * moves can only be made on an empty space.  
	 *
	 * @param strMove the {@link IMove move} that will be added to the stack.
	 *
	 * @throws StateException if the specified move is illegal.
	 */
	public void pushMove(String strMove) throws StateException
	{
		try
		{
			StringTokenizer tokenizer = new StringTokenizer(strMove, ",");
			String row = tokenizer.nextToken();
			String col = tokenizer.nextToken();

			Move lastMove = (Move)peekMove();
			IPiece piece = (lastMove != null && 
						    lastMove.getPiece() == xPiece()) ? oPiece() : xPiece();

			IMove move = getMove(piece, 
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
	}

	/**
	 * Adds the specified {@link IMove move} to this board's stack.
	 * The first move must be made by the "X player".  Moves
	 * must alternate between the "X player" and the "O player".  Finally,
	 * moves can only be made on an empty space.  
	 *
	 * @param strMove the {@link IMove move} that will be added to the stack.
	 *
	 * @throws StateException if the specified move is illegal.
	 */
	public void pushMove(IMove move) throws StateException
	{
		Move ticTacToeMove = (Move)move;
		if ( (m_moveStack.size() == 0) && 
			 (ticTacToeMove.getPiece() != xPiece()) )
		{
			throw new StateException("Illegal move, " + 
									 "first move must be made by X");
		}

		if ( (ticTacToeMove.getRow() < 0) || (ticTacToeMove.getRow() > 2) || 
			 (ticTacToeMove.getCol() < 0) || (ticTacToeMove.getCol() > 2) )
		{
			throw new StateException("Illegal move, move is off the board");
		}

		if (m_moveStack.size() > 0)
		{
			Move lastMove = (Move)peekMove();
			if (lastMove.getPiece() == ticTacToeMove.getPiece())
			{
				throw new StateException
					("Illegal move, same player cannot make two moves " + 
					 "in a row");
			}
		}

		if (m_board[ticTacToeMove.getRow()][ticTacToeMove.getCol()] == 
			emptyPiece())
		{
			m_board[ticTacToeMove.getRow()][ticTacToeMove.getCol()] = 
				ticTacToeMove.getPiece();

			m_moveStack.addFirst(move);

			updateState(ticTacToeMove);
		}
		else
		{
			throw new StateException("Illegal move " + move + 
									 ", the square is already occupied");
		}
	}

	public IMove popMove()
	{
		Move ticTacToeMove = 
			(Move)m_moveStack.removeFirst();
		if (m_board[ticTacToeMove.getRow()][ticTacToeMove.getCol()] != 
			emptyPiece())
		{
			m_board[ticTacToeMove.getRow()][ticTacToeMove.getCol()] = 
				emptyPiece();

			updateState(ticTacToeMove);
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
		IPiece[][] copy = new IPiece[3][3];
		for (int iRow = 0; iRow < 3; iRow++)
		{
			for (int iCol = 0; iCol < 3; iCol++)
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
		m_state = XTURN;
		m_moveStack.clear();

		//juggle who goes first...
		if (m_random.nextInt(2) == 0)
		{
			IPlayer temp = m_players[0];
			m_players[0] = m_players[1];
			m_players[1] = temp;
		}
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
		return "Tic-Tac-Toe";
	}

	public String getShortDescription()
	{
		return "Tic-Tac-Toe";
	}

	public String toString()
	{
		StringBuffer out = createStringBuffer();
		for (int row = 0; row < 3; row++)
		{
			for (int col = 0; col < 3; col++)
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
		for (int iRow = 0; iRow < 3 && bEquals; iRow++)
		{
			for (int iCol = 0; iCol < 3 && bEquals; iCol++)
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
		for (int iRow = 0; iRow < 3; iRow++)
		{
			for (int iCol = 0; iCol < 3; iCol++)
			{													   
				result = 37 * result + m_board[iRow][iCol].hashCode();
			}
		}
		return result;
	}

//---------------

	static IPiece emptyPiece()
	{ return m_emptyPiece; }

	static IPiece xPiece()
	{ return m_xPiece; }

	static IPiece oPiece()
	{ return m_oPiece; }

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
		m_state = (m_moveStack.size() == 0) ? XTURN : THINKING;

		if (m_state == THINKING)
		{
			// horizontal victory...
			if (m_board[0][0]==m_board[0][1] && m_board[0][1] == m_board[0][2]
			    && m_board[0][0] != emptyPiece())
				m_state = (m_board[0][0] == xPiece()) ? XWINS : OWINS;
			else
			if (m_board[1][0]==m_board[1][1] && m_board[1][1] == m_board[1][2]
			    && m_board[1][0] != emptyPiece())
				m_state = (m_board[1][0] == xPiece()) ? XWINS : OWINS;
			else
			if (m_board[2][0]==m_board[2][1] && m_board[2][1] == m_board[2][2]
			    && m_board[2][0] != emptyPiece())
				m_state = (m_board[2][0] == xPiece()) ? XWINS : OWINS;
			else
			// vertical victory
			if (m_board[0][0]==m_board[1][0] && m_board[1][0] == m_board[2][0]
			    && m_board[0][0] != emptyPiece())
				m_state = (m_board[0][0] == xPiece()) ? XWINS : OWINS;
			else
			if (m_board[0][1]==m_board[1][1] && m_board[1][1] == m_board[2][1]
			    && m_board[0][1] != emptyPiece())
				m_state = (m_board[0][1] == xPiece()) ? XWINS : OWINS;
			else
			if (m_board[0][2]==m_board[1][2] && m_board[1][2] == m_board[2][2]
			    && m_board[0][2] != emptyPiece())
				m_state = (m_board[0][2] == xPiece()) ? XWINS : OWINS;
			// diagonal victory
			else
			if (m_board[0][0]==m_board[1][1] && m_board[1][1] == m_board[2][2]
			    && m_board[0][0] != emptyPiece())
				m_state = (m_board[0][0] == xPiece()) ? XWINS : OWINS;
			else
			if (m_board[0][2]==m_board[1][1] && m_board[1][1] == m_board[2][0]
			    && m_board[0][2] != emptyPiece())
				m_state = (m_board[0][2] == xPiece()) ? XWINS : OWINS;
			else
				m_state = (m_moveStack.size() == 9) ? TIE : THINKING;
		}

		// if nobody has one, and it is not a tie, determine who goes next...
		if (m_state == THINKING)
		{
			m_state = (move.getPiece() == xPiece()) ? OTURN : XTURN;
		}
	}

	private void emptyTheArray()
	{
		for (int iRow = 0; iRow < 3; iRow++)
		{
			for (int iCol = 0; iCol < 3; iCol++)
			{
				m_board[iRow][iCol] = emptyPiece();
			}
		}
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

	private int m_state = XTURN;
	private final LinkedList m_moveStack = new LinkedList();
	private final IPiece[][] m_board = new IPiece[3][3];
	private final IPlayer[] m_players = new IPlayer[2];

	private static final StringBuffer m_buffer = new StringBuffer();
	private static final IPiece m_xPiece = new XPiece();
	private static final IPiece m_oPiece = new OPiece();
	private static final IPiece m_emptyPiece = new EmptyPiece();
	private static final HashMap m_moveMap = new HashMap();
	private static final Random m_random = new Random(12);		
}
