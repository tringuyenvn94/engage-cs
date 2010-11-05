package ga;

public interface IPopulationDisplay<Type>
{
	public void display(IPopulation<Type> pop, IFitness<Type> fit, int generation);
}
