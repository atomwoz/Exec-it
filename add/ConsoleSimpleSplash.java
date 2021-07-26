package add;

import java.io.IOException;

public class ConsoleSimpleSplash extends ConsoleDialog 
{
	String[] textToShow;
	
	public ConsoleSimpleSplash(String str) throws IOException 
	{
		super();
		textToShow = str.split("\\n+");
	}
	public ConsoleSimpleSplash(String[] str) throws IOException 
	{
		super();
		textToShow = str;
	}

	@Override
	public int show() 
	{
		printArray(printCenteredSplash(textToShow, 1));
		return 0;
	}


}
