import XSquared.XSquaredFitness;
import ga.*;
import ga.concrete.*;

public class Main
{
    public static void main(String[] args)
    {
    	// what problem shall we solve ?
    	IFitness fit = new XSquaredFitness();

    	// what chromosome should we use?
    	IChromosome<Boolean> proto = new BooleanChromosome(8);
        
    	// how should we sort the chromosomes?
    	ChromosomeComparator<Boolean> comp = new ChromosomeComparator<Boolean>(fit);        

        // how shall we display the population>
        IPopulationDisplay<Boolean> disp = new PopulationPrinter<Boolean>();

        // how shall we represent the population?
        IPopulation<Boolean> pop = new BasicPopulation<Boolean>();
        
        // what natural selection algorithm should we use?
        ISelection<Boolean> sel =  new RouletteWheelSelection<Boolean>();
        
        // what crossover method should we use?        
        ICrossover<Boolean> cross = new SinglePointCrossover<Boolean>();
        
        // what mutation method should we use?
        IMutation<Boolean> mut = new BasicMutation<Boolean>();
        
        // who do we know we are done?
        IConvergenceTest<Boolean> conv = new TopNTheSame<Boolean>();

        // set some parameters....
        cross.setCrossoverRate(0.80);
        mut.setMutationRate(0.01);
        
        // create the top-level algorithm
        GeneticAlgorithm<Boolean> ga = new GeneticAlgorithm<Boolean>();
        
        // run the algorithm...
        pop = ga.run(disp, fit, proto, pop, sel,cross, mut, comp, conv);
        
        // print the best fit individual -- the solution!
        System.out.println(pop.getIndividuals().get(0));
    }

}
