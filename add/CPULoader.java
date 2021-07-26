package add;

public class CPULoader extends Thread
{
	public boolean isStop = false;
	
	@Override
	public void run() 
	{
		while(true)
		{
			if(interrupted() || isStop)
			{
				return;
			}
			int z = 5*8-3;
			int a = z*2;
			
		}
	}

}
