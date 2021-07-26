package add;
import java.util.ArrayList;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;



public class ConsoleList<T> 
{
	ArrayList<T> options = new ArrayList<T>();
	public String separator = "";
	
	public void addOption(T option, int place)
	{
		options.add(place,option);
	}
	public void addOption(T option)
	{
		options.add(option);
	}
	public void removeOption(T option)
	{
		options.remove(option);
	}
	public void removeOption(int place)
	{
		options.remove(place);
	}
	public ArrayList<T> getOptions()
	{
		return options;
	}
	protected String makeSelection(String toSelect)
	{
		 AttributedStringBuilder builder = new AttributedStringBuilder()
	                .style(AttributedStyle.DEFAULT.inverse())
	                .append(toSelect);
		return builder.toAnsi();
	}
	protected void clearScreen()
	{
		ConsoleUtills.clearScreen();
	}
	public Pair<Integer,T> show() throws Exception
	{
		return show("Choose your option with arrows or WASD and press ENTER:");
	}
	public Pair<Integer,T> show(String prompt) throws Exception
	{
		jline.console.ConsoleReader console = new jline.console.ConsoleReader();
		
		//Terminal terminal = TerminalBuilder.builder()
			    //.jna(true)
			   // .system(true)
			   // .build();
		//terminal.enterRawMode();
		//var reader = terminal.reader();
		boolean controlFlag = false;
		int checked = 0;
		
		if(options.size() == 0)
		{
			console.close();
			return null;
		}
		clearScreen();
		while(true)
		{
			System.out.println(prompt);
			System.out.println(separator);
			
			for(int i = 0; i<options.size(); i++)
			{
				String check = "[" + (i==checked ? "*" : " ") + "] ";
				if(i==checked)
				{
					System.out.println(makeSelection(check + options.get(i)));
				}
				else
				{
					System.out.println(check + options.get(i));
				}
				
				
			}
			int key = console.readCharacter(true);
			clearScreen();
			//int key = reader.read();
			if(key == 1091)
			{
				controlFlag = true;
				continue;
			}
			else if((key == 65 && controlFlag) || key == 119)
			{
				checked -= 1;
			}
			else if((key == 66 && controlFlag) || key == 115)
			{
				checked += 1;
			}
			else if(key == 27)
			{
				console.close();
				clearScreen();
				return null;
			}
			else if(key == 13 || key == 32)
			{
				break;
			}
			if(checked >= options.size())
			{
				checked = 0;
			}
			else if(checked < 0)
			{
				checked = options.size() - 1;
			}
			controlFlag = false;
			
			
		}
		ConsoleUtills.finalizeConsole(console);
		clearScreen();
		return new Pair<>(checked, options.get(checked));
		
	}
	
}
