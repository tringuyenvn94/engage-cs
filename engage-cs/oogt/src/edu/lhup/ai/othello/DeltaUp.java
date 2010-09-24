package edu.lhup.ai.othello;

public class DeltaUp implements IDelta 
{
	private int m_min = Integer.MIN_VALUE;
	private int m_max = Integer.MAX_VALUE;
	
	public DeltaUp(int min, int max)
	{
		m_min = min;
		m_max = max;
	}
	
	public int delta(int i) 
	{
		int newI = i - 1;
		if (newI < m_min || newI > m_max)
			return LIMIT;
		else
			return newI;
	}

}
