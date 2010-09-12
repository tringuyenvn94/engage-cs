package edu.lhup.ai;

/**
 * A custom exception object that will be thrown anytime an attempt
 * is made by a {@link IPlayer player} to take an illegal turn.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class TurnException extends Exception
{
	public TurnException(String str)
	{
		super(str);
	}

	public TurnException(String str, Throwable t)
	{
		super(str, t);
	}
}
