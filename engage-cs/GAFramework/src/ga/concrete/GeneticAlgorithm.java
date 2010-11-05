package ga.concrete;

import ga.IChromosome;
import ga.IConvergenceTest;
import ga.ICrossover;
import ga.IFitness;
import ga.IMutation;
import ga.IPopulation;
import ga.IPopulationDisplay;
import ga.ISelection;

public class GeneticAlgorithm<Type>
{
    public GeneticAlgorithm()
    {}
    
    public IPopulation<Type> run(
            IPopulationDisplay<Type> disp,
            IFitness<Type> fit,
            IChromosome<Type> proto,
            IPopulation<Type> pop,
            ISelection<Type> sel,
            ICrossover<Type> cross,
            IMutation<Type> mut, 
            ChromosomeComparator<Type> comp,
            IConvergenceTest<Type> conv
            )
    {
    	int generationCount = 0;
    	
        pop.intialize(100, proto);
        
        disp.display(pop, fit, generationCount);

        while (!conv.hasConverged(pop, fit, comp))
        {
            pop = sel.select(fit, pop);
            pop = cross.crossover(pop);
            mut.mutate(pop);

            generationCount++;
            disp.display(pop, fit, generationCount);
            
        }
System.out.println("Final population");       
        disp.display(pop, fit, generationCount);
        
		return pop;
    }
    
}
