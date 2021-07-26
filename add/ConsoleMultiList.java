package add;
import java.util.ArrayList;





public class ConsoleMultiList<T> extends ConsoleList<T>
{
	
	public ArrayList<Pair<Integer,T>> showMultiple() throws Exception
	{
		return showMultiple("Choose your option with arrows or WASD, press SPACE/TAB for selection, ENTER for accept:");
	}
	
	public ArrayList<Pair<Integer,T>> showMultiple(String prompt) throws Exception
	{
		jline.console.ConsoleReader console = new jline.console.ConsoleReader();
		
		//Terminal terminal = TerminalBuilder.builder()
			    //.jna(true)
			   // .system(true)
			   // .build();
		//terminal.enterRawMode();
		//var reader = terminal.reader();
		boolean selections[] = new boolean[options.size()+1];
		boolean controlFlag = false;
		int checked = 0;
		
		if(options.size() == 0)
		{
			console.close();
			return null;
		}
		clearScreen();
		console.setCursorPosition(1);
		while(true)
		{
			System.out.println(prompt);
			System.out.println(separator);
			
			for(int i = 0; i<options.size(); i++)
			{
				String check = "[" + (selections[i] ? "*" : " ") + "] ";
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
				ConsoleUtills.finalizeConsole(console);
				return null;
			}
			else if(key == 32)
			{
				selections[checked] = !selections[checked];
			}
			else if(key == 9)
			{		
				selections[checked] = !selections[checked];
				checked += 1;
			}
			else if(key == 13)
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
		
		ArrayList<Pair<Integer,T>> returnPairs = new ArrayList<>();
		for(int i=0; i<options.size(); i++)
		{
			if(selections[i])
			{
				returnPairs.add(new Pair<Integer, T>(i, options.get(i)));
			}
		}
		return returnPairs;
		
	}
	
}
