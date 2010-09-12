package edu.lhup.ai;

import java.io.*;
import java.util.logging.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import org.w3c.dom.traversal.*;

import edu.lhup.ai.*;

/**
 * A concrete factory that is responsible for creating a {@link IBoard game}
 * based on the settings and class names specified in an XML file.  The 
 * following is a sample XML file that creates a game of tic-tac-toe between 
 * a {@link RandomPlayer} and a {@link MinMaxAlphaBetaPlayer} with a cutoff 
 * value of 1000 and a default {@link Evaluator}:
 *
 *	<pre>
 *	&lt;game&gt;
 *		&lt;gameFactory&gt;
 *			&lt;boardClass&gt;edu.lhup.ai.tictactoe.Board&lt;/boardClass&gt;
 *			&lt;player&gt;
 *				&lt;playerClass>edu.lhup.ai.RandomPlayer&lt;/playerClass&gt;
 *			&lt;/player&gt;
 *			&lt;player&gt;
 *	 		    &lt;playerClass>edu.lhup.ai.MinMaxAlphaBetaPlayer&lt;/playerClass&gt;
 *				&lt;cutoff>1000&lt;/cutoff&gt;
 *				&lt;evaluator&gt;
 *					&lt;evaluatorClass>edu.lhup.ai.Evaluator&lt;/evaluatorClass&gt;
 *				&lt;/evaluator&gt;
 *			&lt;/player&gt;
 *		&lt;/gameFactory&gt;
 *	&lt;/game&gt;
 *	</pre>
 *
 * <p>
 * This software is for educational purposes only.
 * @author Mark Cohen 
 */
public class Factory implements IFactory
{
	public IBoard getBoardInstance(String configFile) throws StateException
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Factory");
		logger.entering("edu.lhup.ai.tictactoe.Factory", "getBoardInstance", 
						configFile);

		Document doc = parseXML(configFile);

		Node root = doc.getDocumentElement();
		String strBoard = getText(findChild(root, "boardClass"));
		IBoard board = (IBoard)createInstance(strBoard);

		NodeList playerNodes = doc.getElementsByTagName("player");
		logger.finest("Player count: " + playerNodes.getLength());
		if (playerNodes.getLength() !=2)
		{
			throw new StateException("Invalid number of players specified");
		}

		IPlayer[] players = new IPlayer[2];
		players[0] = createPlayer(playerNodes.item(0));
		players[1] = createPlayer(playerNodes.item(1));

		board.setPlayers(players);

		logger.exiting("edu.lhup.ai.tictactoe.Factory", "getBoardInstance",
					   "Players: \n" + players[0].toString() + ", " + 
					   				   players[1].toString());		
		return board;
	}
	
	public IGameInterface getGameInterface(String configFile) throws StateException
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Factory");
		logger.entering("edu.lhup.ai.tictactoe.Factory", "getGameInterface", 
						configFile);

		Document doc = parseXML(configFile);

		Node root = doc.getDocumentElement();
		String strBoard = getText(findChild(root, "interfaceClass"));
		return (IGameInterface)createInstance(strBoard);
	}	
	
	private IPlayer createPlayer(Node playerNode) throws StateException
	{
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Factory");
		logger.entering("edu.lhup.ai.tictactoe.Factory", "createPlayer", 
						playerNode.getNodeName());

		String strPlayerClass = getText(findChild(playerNode, "playerClass"));
		logger.finest("Player classname: " + strPlayerClass);
		IPlayer player = (IPlayer)createInstance(strPlayerClass);

		String cutoff = getText(findChild(playerNode, "cutoff"));
		if (cutoff.length() > 0)
		{
			logger.finest("cutoff: " + cutoff);
			player.setCutoff(Integer.parseInt(cutoff));
		}

		String strPlayerEvaluator = 
			getText(findChild(playerNode, "evaluatorClass"));
		logger.finest("Evaluator classname: " + strPlayerEvaluator);
		if (strPlayerEvaluator.length() > 0)
		{
			IEvaluator eval = (IEvaluator)createInstance(strPlayerEvaluator);
			player.setEvaluator(eval);
		}
		return player;
	}

	private Document parseXML(String file) throws StateException
	{
		try
		{
			DocumentBuilderFactory domFactory = 
				DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = domFactory.newDocumentBuilder();
	        return builder.parse(file);
		}
		catch (ParserConfigurationException e)
		{
			throw new StateException("Error parsing config file", e);
		}	
		catch (SAXException e)
		{
			throw new StateException("Error parsing config file", e);
		}	
		catch (IOException e)
		{
			throw new StateException("Error parsing config file", e);
		}	
	}

	private static Object createInstance(String strClass) throws StateException
	{
		Object obj;
		try
		{
			Class cls = Class.forName(strClass);
			obj = cls.newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw new StateException("Creation error during configuration", e);
		}
		catch (InstantiationException e)
		{
			throw new StateException("Creation error during configuration", e);
		}
		catch (IllegalAccessException e)
		{
			throw new StateException("Creation error during configuration", e);
		}
		return obj;
		
	}

	private static Node findChild(Node node, String name)
	{ 
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Factory");
		logger.entering("edu.lhup.ai.tictactoe.Factory", "findChild", name);

		Node returnNode = null;
		if (node.getNodeName().equals(name))
		{
			returnNode = node;
		}
		else
		{
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength() && returnNode == null; i++)
			{
				returnNode = findChild(children.item(i), name);
			}
		}

		logger.exiting("edu.lhup.ai.tictactoe.Factory", "findChild",
					   returnNode);		
		return returnNode;
	}

	private static String getText(Node n)
	{ 
		Logger logger = Logger.getLogger("edu.lhup.ai.tictactoe.Factory");
		logger.entering("edu.lhup.ai.tictactoe.Factory", "getText");

		String text = "";
		if (n != null)
		{
			NodeList list = n.getChildNodes();
			if (list.getLength() == 1 &&
				list.item(0).getNodeType() == Node.TEXT_NODE)
			{
				text = list.item(0).getNodeValue();
			}
		}

		logger.exiting("edu.lhup.ai.tictactoe.Factory", "getText", text);		
		return text;
	}
}