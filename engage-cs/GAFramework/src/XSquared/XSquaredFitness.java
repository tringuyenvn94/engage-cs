package XSquared;
import ga.IChromosome;
import ga.IFitness;

public class XSquaredFitness implements IFitness<Boolean>
{
    public double fitness(IChromosome<Boolean> ind)
    {
        double value = 0.0; 
        for (int i = 0; i < ind.getGeneCount(); i++)
        {
            int mult = ind.getGene(i) == Boolean.TRUE ? 1 : 0; 
            value = value + (mult * Math.pow(2, i));
        }
        return value * value;
    }
}
