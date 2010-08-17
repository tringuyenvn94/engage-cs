import edu.lhup.vacuum.BaseJavaAgent;
import edu.lhup.vacuum.CheatException;

public class SimpleJavaVacuum extends BaseJavaAgent 
{

	protected void go() throws CheatException 
	{
		dumpState(System.out);
	}

}
