package add;

public class CenteredTextDialog extends ConsoleDialog 
{
	public String text;
	public CenteredTextDialog(String s) 
	{
		super();
		text = s;
	}

	@Override
	public int show() 
	{
		System.out.println(printAsCentered(text));
		return 0;
	}

}
