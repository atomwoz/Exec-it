package add;

import java.io.File;
import java.util.TreeMap;

public class LocalProcesses 
{
	TreeMap<Long, Process> localProcess;
	
	public void push(Long pid, Process p) throws NullPointerException
	{
		localProcess.put(pid, p);
	}
	public Process getByPID(Long pid)
	{
		update();
		Process p = localProcess.get(pid);
		if(p == null)
		{
			NullPointerException e = new NullPointerException();
			throw e;
		}
		else
		{
			return p;
		}
		
	}
	public String getNameFromPID(Long pid)
	{
		update();
		return getNameFromProcess(localProcess.get(pid));
	}
	public String toString()
	{
		update();
		String result;
		result = "PID\t\tVisible name\n";
		for(Long procpid : localProcess.keySet())
		{
			Process proc = localProcess.get(procpid);
			result = result.concat(procpid + "\t\t" + getNameFromProcess(proc) + "\n");
		}
		return result;
	}
	private void update()
	{
		for(Long procpid : localProcess.keySet())
		{
			Process proc = localProcess.get(procpid);
			if(!proc.isAlive())
			{
				localProcess.remove(procpid);
			}
		}
	}
	public static String getNameFromProcess(Process p)
	{
		String cmd = p.info().command().get();
		cmd = cmd.substring(cmd.lastIndexOf(File.separator)+1);
		return cmd;
	}
	
	public LocalProcesses()
	{
		localProcess = new TreeMap<Long , Process>();
	}
}
