package add;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Timer 
{
	public static void runTimer()
	{
		System.out.println("Press ENTER to stop timer");
		Thread timerThread = new Thread(() -> {
			long startTime = System.currentTimeMillis();
			
			while(!Thread.interrupted())
			{
				System.out.print("\rElapsed time " + millisToString(System.currentTimeMillis() - startTime));
			}
			
		}, "Timer thread");
		timerThread.start();
		try 
		{
			System.in.read();
		} 
		catch (IOException e) {}
		timerThread.interrupt();
	}
	public static String millisToString(long givenMillis)
	{
		long millis = givenMillis % 1000;
		long seconds = (givenMillis / 1000) % 60;
		long mins = (givenMillis / (1000 * 60)) % 60;
		long hours = (givenMillis / (1000 * 60 * 60)) % 24;
		
		return (hours > 0 ? hours + "h " : "") + (mins > 0 ? mins + "min " : "") + (seconds > 0 ? seconds + "sec " : "") + millis + "ms" ;
	}
}
