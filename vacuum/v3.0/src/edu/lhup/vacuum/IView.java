package edu.lhup.vacuum;

public interface IView extends Runnable 
{ 
	public void setModel(Model m);
	public void init();
}
