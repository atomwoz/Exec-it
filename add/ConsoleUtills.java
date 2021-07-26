package add;

import java.io.IOException;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;


import jline.Terminal;
import jline.console.ConsoleReader;
import jline.internal.NonBlockingInputStream;
import main.Main;


public class ConsoleUtills 
{
	private static ConsoleUtills instance;
	static boolean isRich = isAnsiTerminal();
	private static int consoleWidth = 80;
	String defaultShell = "sh";
	
	private ConsoleUtills()
	{
		boolean isWindows = false;
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			isWindows = true;
		}	
		try
		{
			ConsoleReader reader = new ConsoleReader();
			if(reader.getTerminal().isSupported())
			{
				consoleWidth = reader.getTerminal().getWidth();
			}
			
			finalizeConsole(reader);
		}
		catch(Exception e){}
		if(isWindows)
		{
			defaultShell = "cmd";
		}
		else
		{
			String shell = System.getProperty("SHELL");
			if(shell == null)
			{
				String defShell = checkCommands(new String[]{"bash","zsh","fish","ksh"});
				if(defShell != null)
				{
					defaultShell = defShell;
				}
			}
			else
			{
				defaultShell = shell;
			}
			
		}
	}
	public static ConsoleUtills getInstance() 
	{
		if (instance == null) 
		{
			instance = new ConsoleUtills();
	    }
	    return instance;
	}
	private String checkCommands(String[] commands)
	{
		for(String s : commands)
		{
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(s);
			try {
				Process p = pb.start();
				p.destroyForcibly();
				return s;
			} catch (IOException e) {
				continue;
			}
		}
		return null;
		
	}
	public String getDefaultShell()
	{
		return defaultShell;
	}
	public static int getTerminalWidth()
	{
		return consoleWidth;
	}
	public static void clearScreen(boolean isWindows)
	{
	    try {
	        if (isWindows)
	        { 
	        	new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	        }       
	        else
	        {
	        	new ProcessBuilder("clear").inheritIO().start().waitFor();
	        	//System.out.print("\033[H\033[2J");
	        	//System.out.flush();
	        }
	        	
	    } catch (IOException | InterruptedException ex) {}
	}
	public static void clearScreen()
	{
		boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
	    clearScreen(isWindows);
	}
	public static void finalizeConsole(ConsoleReader console) throws Exception
	{
		Terminal term = console.getTerminal();
		term.setEchoEnabled(true);
		term.init();
		term.reset(); 
		term.restore(); 
		console.close();
		console.delete();
		//clearScreen();
	}
	public static boolean isAnsiTerminal()
	{
		boolean isAnsii = false;
		try
		{
			ConsoleReader reader = new ConsoleReader();
			isAnsii = reader.getTerminal().isAnsiSupported();
			finalizeConsole(reader);
		}
		catch(Exception e){isAnsii = false;}
		String osType = System.getProperty("os.name").toLowerCase();
		boolean isWindows = osType.contains("windows");
		if(isWindows)
		{
			return osType.length() >= 9 && osType.substring("windows".length()+1).equals("10") && isAnsii;
		}
		else
		{
			return isAnsii;
		}
	
		
		
	}
	public static void testKeyboard() throws Exception
	{
		ConsoleReader console = new ConsoleReader();
		//ConsoleReader cr = new ConsoleReader();
		console.setKeyMap("UTF-8");
		
		System.out.println("Press ESC for end testing");
		int key = 0;
		while(key != 27) 
		{
			key = console.readCharacter(true);
			System.out.println(key + " : " + Character.getName(key));
		}
		
		finalizeConsole(console);
	}
	
	public static void printError(String error)
	{
		String err = "ERROR: " + error;
		AttributedStringBuilder builder = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                .append(err);
		System.out.println(isRich ? builder.toAnsi() : err);
	}
}
