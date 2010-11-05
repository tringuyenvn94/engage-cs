package ga.concrete;

import ga.IChromosome;
import ga.IConvergenceTest;
import ga.IFitness;
import ga.IPopulation;

import java.util.Collections;

public class TopNTheSame<Type> implements IConvergenceTest<Type> 
{
	private int m_theashold = 10;
	
	public void setThreshold(int t)
	{ m_theashold = t; }
	
	public boolean hasConverged(IPopulation<Type> pop,
			IFitness<Type> fit, 
			ChromosomeComparator<Type> comparator)
    {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        
        Collections.sort(pop.getIndividuals(), comparator);
        
        int count = 0;
        for (IChromosome<Type> c : pop.getIndividuals())
        {
        	count++;
        	if (count > m_theashold)
        		break;
        	
            double f = fit.fitness(c);
            if (f > max)
                max = f;
            if (f < min)
                min = f;
        }
        return (max-min) == 0;
    }

}
