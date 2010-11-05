package ga;


public class PopulationPrinter<Type> implements IPopulationDisplay<Type>
{
    public void display(IPopulation<Type> pop, IFitness<Type> fit, int generation)
    {
        System.out.println("************************");
        System.out.println(" Generation: " + generation);
        System.out.println(" Size: " + pop.getSize());
        System.out.println(" Top Ten: ");
        System.out.println("************************");
        for (int i = 0; i < 10; i++)
        {
        	IChromosome<Type> c = pop.getIndividuals().get(i);
            System.out.println(String.format("%s (%f) ", c, fit.fitness(c)));
        }
        System.out.println("************************");
        System.out.println("");
    }

}
