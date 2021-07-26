package add;

import java.io.IOException;
import java.util.ArrayList;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import jline.console.ConsoleReader;

public abstract class ConsoleDialog 
{
	int consoleWidth = 80;
	int consoleHeight = 50;
	String defaultSeparator;
	
	protected String makeSelection(String toSelect)
	{
		 AttributedStringBuilder builder = new AttributedStringBuilder()
	                .style(AttributedStyle.DEFAULT.inverse())
	                .append(toSelect);
		return builder.toAnsi();
	}
	protected String printSeparator(char sepChar)
	{
		String sep = "";
		for(int i=0; i<consoleWidth; i++)
		{
			sep = sep.concat(sepChar + "");
		}
		return sep;
	}
	protected String printCustomUpSeparator(int size)
	{
		String sep = "";
		sep= sep + '┌';
		for(int i=0; i<size; i++)
		{
			sep = sep + '─';
		}
		sep= sep + '┐';
		return sep;
	}
	protected String printCustomDownSeparator(int size)
	{
		String sep = "";
		sep= sep + '└';
		for(int i=0; i<size; i++)
		{
			sep = sep + '─';
		}
		sep= sep + '┘';
		return sep;
	}
	protected String printSpace(int size)
	{
		String sep = "";
		for(int i=0; i<size; i++)
		{
			sep = sep + " ";
		}
		return sep;
	}
	protected String printSeparator()
	{
		return printSeparator('=');
	}
	public static void printArray(String[] lines)
	{
		for(String s : lines)
		{
			System.out.println(s);
		}
	}
	protected String[] printAsCentered(String[] lines,int width)
	{
		ArrayList<String> out_lines = new ArrayList<String>();
		
		for(String s : lines)
		{
			int reqMargin = Math.round(width - s.length())/2;
			out_lines.add(printSpace(reqMargin) + s);
		}
		return Shell.arrayToString(out_lines);
	}
	protected String printAsCentered(String text)
	{
		int reqMargin = Math.round(consoleWidth - text.length())/2;
		return printSpace(reqMargin) + text;
	}
	protected int maxTextLength(String[] lines)
	{
		int out = 0;
		for(String s : lines)
		{
			out = Math.max(out, s.length());
		}
		return out;
	}
	protected String[] printCenteredTextInBox(String[] lines, int margin)
	{
		int width = maxTextLength(lines) + margin*2;
		ArrayList<String> out_lines = new ArrayList<String>();
		out_lines.add(printCustomUpSeparator(width) + "\n");
		for(String s : lines)
		{
			int aliginSpace = Math.round(width - s.length());
			out_lines.add('│' + printSpace(aliginSpace) + s + printSpace(aliginSpace) + '│');
		}
		out_lines.add(printCustomDownSeparator(width) + "\n");
		return Shell.arrayToString(out_lines);
		
	}
	protected String[] printCenteredSplash(String[] lines, int margin)
	{
		int width = maxTextLength(lines) + margin*2;
		ArrayList<String> out_lines = new ArrayList<String>();
		out_lines.add(printSeparator('─'));
		for(String s : lines)
		{
			int aliginSpace = Math.round((consoleWidth - s.length())/2);
			out_lines.add(printSpace(aliginSpace) + s + printSpace(aliginSpace));
		}
		out_lines.add(printSeparator('─') + "\n");
		return Shell.arrayToString(out_lines);
		
	}
	protected String[] printTextInBox(String[] lines)
	{
		return printCenteredTextInBox(lines, 2);
	}
	protected void clearScreen()
	{
		ConsoleUtills.clearScreen();
	}
	public abstract int show();
	
	public ConsoleDialog()
	{
		try
		{
			defaultSeparator = printSeparator();
			ConsoleReader cr = new ConsoleReader();
			jline.Terminal term = cr.getTerminal();
			consoleWidth = term.getWidth(); 
			consoleHeight = term.getHeight();
			ConsoleUtills.finalizeConsole(cr);
		}
		catch(Exception e)
		{
			
		}
		
	}
	
}
