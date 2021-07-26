package add;
import java.util.ArrayList;
import org.mariuszgromada.math.mxparser.*;

public class Paraser 
{
	ArrayList<Constant> constants;
	
	public Paraser()
	{
		constants = new ArrayList<Constant>();
	}
	public double parase(String exp)
	{
		/*System.out.println(constants.size());
		for(Constant a : constants)
		{
			System.out.println(a.getConstantValue());
		}*/
		Expression expression = new Expression(exp);
		expression.addConstants(constants);
		return expression.calculate();
	}
	public static String simpleParase(String exp)
	{
		Expression expression = new Expression(exp);
		return String.valueOf(expression.calculate());
	}
	public void defineWithEquals(String exp)
	{
		Constant constant = new Constant(exp);
		constants.add(constant);
	}
	public void define(String exp)
	{
		String [] param = exp.split("\\s+");
		if(param.length >= 2)
		{
			try
			{
				Constant constant = new Constant(param[0],parase(param[1]));
				constants.add(constant);
			}
			catch(NumberFormatException e)
			{
				System.err.println("Constant value must be a number");
			}
		}
		else
		{
			System.err.println("To few arguments to define constant");
		}
	}
	

}
