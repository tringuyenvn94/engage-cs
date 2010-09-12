package edu.lhup.ai;

/**
 * The abstract representation of a factory that is capable of creating a
 * {@link IBoard game}.  Implementations of this interface will be responsible 
 * for creating a game using the parameters specified in a configuration file.
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public interface IFactory
{
	/**
	 * @param strConfigFile a resource that contains all the configuration 
	 * parameters needed to create a game.
	 *
	 * @return a game that is ready to be played.
	 *
	 * @throws StateException if the configuration files specifies an 
	 * an invalid game.
	 */
	public IBoard getBoardInstance(String strConfigFile) throws StateException;
}
























												   