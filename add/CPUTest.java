package add;

public class CPUTest 
{
	public static void singleCoreTest(int tests)
	{
		new CenteredTextDialog("CPU single core benchmark on 1 thread").show();
		System.out.println(Shell.horizontalSeparator("="));
		long sum = 0, averge = 0;	
		for(int i=0; i<tests; i++)
		{
			System.out.print("\rTesting CPU "+ (i+1) +"/" + tests + " tests");
			long x = System.currentTimeMillis();
			Shell.stress();		
			long y = System.currentTimeMillis();			
			sum += y-x;
		}
		averge = sum / tests;
		
		System.out.println("\rYour CPU bench result: " + averge + " points (the fewer the better)");
	}
	public static void multiCoreTest(int tests, int threads)
	{
		new CenteredTextDialog("CPU multi core benchmark on 64 threads").show();
		System.out.println(Shell.horizontalSeparator("="));
		long sum = 0, averge = 0;	
		Thread[] testing_thread = new Thread[threads];
		for(int i=0; i<threads; i++)
		{
			testing_thread[i] = new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					for(int i=0; i<tests; i++)
					{
						Shell.stress();				
					}
				}
			});
		}
		long x = System.currentTimeMillis();
		for(int i=0; i<threads; i++)
		{
			testing_thread[i].start();
		}
		for(int i=0; i<threads; i++)
		{
			try {
				testing_thread[i].join();
			} catch (InterruptedException e) 
			{
				
			}
		}
		long y = System.currentTimeMillis();
		sum = y-x;
		averge = sum / (threads*tests);
		
		System.out.println("\rYour CPU bench result: " + averge + " points (the fewer the better)");
	}
	public static void makeTest(String argue)
	{
		int tests = 5;
		int threads = 64;
		
		if(!argue.isBlank())
		{
			try
			{
				tests = Integer.valueOf(argue);
				
			}
			catch(Exception e)
			{
				ConsoleUtills.printError("In the parameter you specify the number of tests you want to run");
			}
		}
		ConsoleUtills.clearScreen();
		if(tests > 40)
		{
			System.out.println("YOU GIVE LARGE NUMBER OF TESTS");
			System.out.println("For maximum efficency of the test please close all open apps");
			System.out.println("It will take long time about "+calcTestTime(tests) + " for " + tests + " single core test");
			System.out.println("And long time too "+calcTestTimeMulticore(tests,threads) + " for multi core test");
			System.out.println(Shell.horizontalSeparator("="));
			if(!YesNoDialog.printSimpleYN("Do you want to start the test (not recommended)?"))
			{
				return;
			}
			
		}
		else
		{
			System.out.println("For maximum efficency of the test please close all open apps");
			System.out.println("It should took about "+calcTestTime(tests) + " for " + tests + " single core test");
			System.out.println("And "+calcTestTimeMulticore(tests,threads) + " for multi core test");
			System.out.println(Shell.horizontalSeparator("="));
			if(!YesNoDialog.printSimpleYN("Do you want to start the test?",'y'))
			{
				return;
			}
		}
		ConsoleUtills.clearScreen();
		singleCoreTest(tests);
		System.out.println(Shell.horizontalSeparator("="));
		multiCoreTest(tests,threads);
		
	}
	static String calcTestTime(int tests)
	{
		int timeInSec = tests*3;
		if(timeInSec >= 60)
		{
			return ((int)timeInSec/60) + " minutes";
		}
		else
		{
			return timeInSec + " seconds";
		}
	}
	static String calcTestTimeMulticore(int tests, int threads)
	{
		int timeInSec = (tests*3) * (threads/Runtime.getRuntime().availableProcessors());
		if(timeInSec >= 60)
		{
			return ((int)timeInSec/60) + " minutes";
		}
		else
		{
			return timeInSec/60 + " seconds";
		}
	}
}
