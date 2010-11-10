package swarm;

public class Location 
{
	private int m_row = 0;
	private int m_col = 0;
	
	public Location()
	{}

	public Location(int row, int col)
	{
		m_row = row;
		m_col = col;
	}
	
	public void setRow(int row)
	{ m_row = row; }

	public void setCol(int col)
	{ m_col = col; }
	
	public int getRow()
	{ return m_row; }
	
	public int getCol()
	{ return m_col; } 
	
	public void setLocation(Location l)
	{
		m_row = l.getRow();
		m_col = l.getCol();
	}
	
	public Location left()
	{ 
		Location l = new Location(m_row, m_col-1);
		return l;
	}
	
	public Location right()
	{ 
		Location l = new Location(m_row, m_col+1);
		return l;
	}

	public Location up()
	{ 
		Location l = new Location(m_row-1, m_col);
		return l;
	}

	public Location down()
	{ 
		Location l = new Location(m_row+1, m_col);
		return l;
	}

	public String toString()
	{ return "(" + m_row + "," + m_col + ")"; }
	
	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		else if (!(other instanceof Location))
			return false;
		else 
		{
			Location otherL = (Location)other;
			return otherL.getRow() == getRow() && otherL.getCol() == getCol();
		}
	}
}
