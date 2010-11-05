package ga;

import ga.concrete.ChromosomeComparator;

public interface IConvergenceTest<Type> 
{
	public boolean hasConverged(IPopulation<Type> pop,
								IFitness<Type> fit, 
								ChromosomeComparator<Type> comparator);
}
