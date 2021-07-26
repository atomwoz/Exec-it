package add;

public class Garbage 
{
	byte supcio[];
	
	public Garbage(long alloc)
	{
		supcio = new byte[(int) alloc];
	}
	public void useGarbage()
	{
		supcio[0] = 1;
	}
}
