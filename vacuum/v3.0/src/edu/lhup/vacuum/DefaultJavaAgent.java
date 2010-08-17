package edu.lhup.vacuum;

public class DefaultJavaAgent extends BaseJavaAgent
{
	public DefaultJavaAgent()
	{
	}	

	protected void go()
	{
		dumpState(System.out);
	}
}
