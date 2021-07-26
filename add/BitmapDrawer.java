package add;

import java.io.IOException;

public class BitmapDrawer extends ConsoleDialog 
{
	public byte[][] bitmap = new byte[consoleWidth+1][consoleHeight+1];
	public char fillChar = '#', emptyChar = ' ';
	
	public BitmapDrawer() throws IOException 
	{
		super();
	}

	@Override
	public int show() 
	{
		for(byte[] xs : bitmap)
		{
			for(byte ys : xs)
			{
				System.out.print(ys == 1 ? fillChar : emptyChar);
			}
			System.out.println();
		}
		return 0;
	}

}
