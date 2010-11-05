package ga.concrete;

import ga.IChromosome;
import ga.IPopulation;

import java.util.ArrayList;
import java.util.List;

public class BasicPopulation<Type> implements IPopulation<Type>
{
    private List<IChromosome<Type>> m_people = 
        new ArrayList<IChromosome<Type>>(); 
    
    public int getSize()
    {
        return m_people.size();
    }

    public void intialize(int size, IChromosome<Type> proto)
    {
        for (int i = 0; i < size; i++)
        {
            addIndividual(proto.create());
        }
    }

    public List<IChromosome<Type>> getIndividuals()
    {
        return m_people;
    }

    public void addIndividual(IChromosome<Type> ind)
    {
        m_people.add(ind);        
    }

    public IPopulation<Type> create()
    {
        return new BasicPopulation<Type>();
    }

}
