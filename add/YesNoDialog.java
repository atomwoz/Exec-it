package add;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YesNoDialog 
{
	/**
	 * It is a function which prints simple console dialog
	 * @param br - It is buffered reader from which is a answer readed
	 * @param question - It is a string with question without (y/n) on the end
	 * @param defaultOption - It is a default answer when the user answer was not recognized; it can be 'y' or 'n'
	 * @return True if answer is 'yes' or 'y' and returns false if answer is 'no' or 'n'
	 * @author atomwoz
	 */
	public static boolean printSimpleYN(BufferedReader br, String question, char defaultOption)
	{
		defaultOption = Character.toLowerCase(defaultOption);
		boolean isDefTrue = (defaultOption == 'y');	
		try
		{
			System.out.print(question + "  " + (isDefTrue ? "(Y/n)" : "(y/N)"));
			String line = br.readLine();
			line = line.toLowerCase();
			if(line.equals("y") || line.equals("yes"))
			{
				return true;
			}
			else if (line.equals("n") || line.equals("no"))
			{
				return false;
			}
			else
			{
				return isDefTrue;
			}
		}
		catch(IOException e)
		{
			ConsoleUtills.printError("IO unknown error");
			return isDefTrue;
		}
		
	}
	/**
	 *  It is a function which prints simple console dialog; When answer is not recognized it returns no (false)
	 * @param br - It is buffered reader from which is a answer readed
	 * @param question - It is a string with question without (y/n) on the end
	 * @return True if answer is 'yes' or 'y' and returns false if answer is 'no' or 'n'
	 * @author atomwoz
	 */
	public static boolean printSimpleYN(BufferedReader br, String question)
	{
		return printSimpleYN(br, question, 'n');
	}
	/**
	 *  It is a function which prints simple console dialog; When answer is not recognized it returns no (false)
	 * @param br - It is buffered reader from which is a answer readed
	 * @param question - It is a string with question without (y/n) on the end
	 * @return True if answer is 'yes' or 'y' and returns false if answer is 'no' or 'n'
	 * @author atomwoz
	 */
	public static boolean printSimpleYN(String question)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
		return printSimpleYN(br, question, 'n');
	}
	/**
	 *  It is a function which prints simple console dialog; When answer is not recognized it returns no (false)
	 * @param br - It is buffered reader from which is a answer readed
	 * @param question - It is a string with question without (y/n) on the end
	 * @return True if answer is 'yes' or 'y' and returns false if answer is 'no' or 'n'
	 * @author atomwoz
	 */
	public static boolean printSimpleYN(String question, char defaultOption)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
		return printSimpleYN(br, question, defaultOption);
	}
}
