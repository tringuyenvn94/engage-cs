package edu.lhup.ai;

/**
 * A custom exception object that will be thrown anytime an attempt
 * is made to place a {@link IBoard board} into an invalid state.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class StateException extends Exception
{
	public StateException(String str)
	{
		super(str);
	}

	public StateException(String str, Throwable t)
	{
		super(str, t);
	}
}						   