package swarm;

import java.util.*;
import java.io.*;
import java.util.StringTokenizer;

import javax.swing.JPanel;

public class Model 
{
	private Location m_nest = null;
	private Location m_food = null;
	
	private List<List<Square>> m_world = new ArrayList<List<Square>>();
	private List<IModelListener> m_listeners = new ArrayList<IModelListener>();
	
	public Model(String file) throws IOException
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(file));

			int row = 0;
			int col = 0;
			String line = in.readLine();;
			while (line != null)
			{
				List<Square> list = new ArrayList<Square>();
				StringTokenizer tokenizer = new StringTokenizer(line, "\t");
				while (tokenizer.hasMoreTokens())
				{
					String token = tokenizer.nextToken();
					if (token.equalsIgnoreCase("N"))
					{
						m_nest = new Location(row, col);
					}
					else if (token.equalsIgnoreCase("F"))
					{
						m_food = new Location(row, col);
					}
					Square p = newPiece(token);
					list.add(p);
					col++;
				}
				m_world.add(list);
				row++;
				col = 0;
				line = in.readLine();				
			} 
		}
		finally
		{
			if (in != null)
			{
				in.close();
			}
		}
	}
	
	synchronized public int getH()
	{ return m_world.size(); }

	synchronized public int getW()
	{ return m_world.get(0).size(); }
	
	synchronized public int getPheromone(Location loc)
	{ return getSquare(loc).getPheromone();	}

	synchronized public boolean isNest(Location loc)
	{ return getSquare(loc) instanceof Nest; }

	synchronized public boolean isFood(Location loc)
	{ return getSquare(loc) instanceof Food; }

	synchronized public boolean isWall(Location loc)
	{ return getSquare(loc) instanceof Wall; }

	synchronized public boolean isPath(Location loc)
	{ return getSquare(loc) instanceof Path; }

	synchronized public void leave(Location loc)
	{ getSquare(loc).leave();}
	
	synchronized public void visit(Location loc)
	{ getSquare(loc).visit();}
	
	synchronized public void add(JPanel p ,int row, int col)
	{ p.add(getSquare(row, col)); }
	
	synchronized  public Location getNestLocation()
	{ return m_nest; }

	synchronized public Location getFoodLocation()
	{ return m_food; }
	
	synchronized public void addListener(IModelListener l)
	{
		m_listeners.add(l);
	}

	private Square getSquare(Location loc)
	{ return getSquare(loc.getRow(), loc.getCol());	}

	private Square getSquare(int row, int col)
	{ return m_world.get(row).get(col);	}
	
	private Square newPiece(String type)
	{
		Square s = null;
		if (type.equalsIgnoreCase("W"))
			s = new Wall();
		else if (type.equalsIgnoreCase("-"))
			s = new Path();
		else if (type.equalsIgnoreCase("N"))
			s = new Nest();
		else if (type.equalsIgnoreCase("F"))
			s = new Food();
		return s;
	}
}
