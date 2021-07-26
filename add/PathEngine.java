package add;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import main.Main.FileTypes;

public class PathEngine 
{
	private boolean isRunningOnWindows, autoPathCorrect = true;
	private String exec_localization;
	public PathEngine(boolean isOnWin, String exec_loc)
	{
		isRunningOnWindows = isOnWin;
		exec_localization = exec_loc;
	}
	public void disablePathCorrect()
	{
		autoPathCorrect = false;
	}
	public void enableePathCorrect()
	{
		autoPathCorrect = true;
	}
	public String cdWithMake(String def_localization, String loc, FileTypes type) throws IOException
	{
		String local = cd(def_localization, loc, true, false, false, type);	
		String rootLoc = cd(def_localization, loc, false, true, false, FileTypes.DIR);	
		if(rootLoc.isBlank())
		{
			return "";
		}
		File file = new File(local);
		if(file.exists())
		{
			return local;
		}
		else
		{
			if(type == FileTypes.DIR)
			{
				if(file.mkdir())
				{
					return local;
				}
				else
				{
					return "";
				}
			}
			else
			{
				if(file.createNewFile())
				{
					return local;
				}
				else
				{
					return "";
				}
			}
			
		}
	}
	private String cd(String def_localization, String loc,boolean force,boolean ignore_last, boolean pointToExecFile, FileTypes type) throws IOException
	{
		String term_sign = File.separator;
		File file; 
		boolean absolute = false;
		if(ignore_last)
		{
			loc = trimLastInPath(loc)[1];
		}
		//Checking directory up
		loc = loc.replace("...", "..");
		if(loc.contains(".."))
		{
			try
			{
				loc = loc.replace("..",Paths.get(def_localization).getParent().toString());
				absolute = true;
				
			}
			catch(NullPointerException e)
			{
				ConsoleUtills.printError("Choosen path not exist");
				return "";
			}

		}
			
		
		if(!isRunningOnWindows && autoPathCorrect)
		{
			loc = loc.replace("\\", "/");
		}
		if(isRunningOnWindows && autoPathCorrect)
		{
			loc = loc.replace("/", "\\");
		}
		String root = Paths.get(def_localization).getRoot().toString();
		if(loc.startsWith("*"))
		{
			if(loc.length() >= 2)
			{
				loc = loc.substring(1);
			}
			else
			{
				loc = "";
			}
			loc = root.concat(loc);
			absolute = true;
		}
		if(!def_localization.endsWith(File.separator))
		{
			def_localization = def_localization + File.separator;
		}
		/*if(loc.equals("~"))
		{
			loc = System.getProperty("user.home");
		}*/
		
		//System.out.println("root: " + root);
		//Replacing tokens
		if(loc.contains("~"))
		{
			loc = loc.replace("~", System.getProperty("user.home"));
			absolute = true;
		}
		
		loc = loc.replace("#", exec_localization);
		//Working with system specified start and end string
		if(!isRunningOnWindows && !loc.startsWith(File.separator))
		{
			/*String main_sep = File.separator;
			String str = main_sep;
			if(loc.contains(main_sep))
			{
				str = main_sep;
			}
			else if(loc.contains("\\"))
			{
				str = "\\";
			}*/
			
			loc = File.separator + loc;
		}
		
		//Path path;
		try
		{
			Paths.get(loc);
		}
		catch(InvalidPathException e)
		{
			if(!force)
			{
				ConsoleUtills.printError("Invalid path given");
				return "";
			}
			
		}
				
		//System.out.println(loc + " ABS: " + absolute);
		
		String prob_loc = "";
		try
		{
			prob_loc = compilePath(def_localization,loc);
			//System.out.println("Kompilowanie");
		}
		catch(InvalidPathException e)
		{
		}
		
		//Path path = Path.of(loc);
		File oldfile  = new File(loc);
		File newfile = new File(prob_loc);
		
		if(isRunningOnWindows && oldfile.exists())
		{
			absolute = true;
		}
		if(isRunningOnWindows && loc.contains(":"))
		{
			absolute = true;
		}
		
		//Decide when use the absolute or def_loc+argue localization
		if((!(absolute || (oldfile.exists() && !newfile.exists())) || (!oldfile.exists() && force)) && !absolute)
		{
			loc = prob_loc;
			//System.out.println("Kompilowanie");
		}
		/*else if(new File(prob_loc).exists() && absolute)
		{
			ConsoleUtills.printError("Choosen absolute path not exist");
			return "";
		}*/
		//System.out.println(loc);
		try
		{
			file = new File(loc);
			if(isRunningOnWindows && pointToExecFile && !file.exists())
			{
				file = new File(loc + ".cmd");
			}
			if(isRunningOnWindows && pointToExecFile && !file.exists())
			{
				file = new File(loc + ".bat");
			}
			if(isRunningOnWindows && pointToExecFile && !file.exists())
			{
				file = new File(loc + ".exe");
			}
			if(absolute && file.exists())
			{
				File ready_file = new File(loc);
				return ready_file.getCanonicalPath().toString();
			}
			if(file.exists() || (!file.exists() && force))
			{
				//System.out.println(def_localization);
				try
				{
					loc = Paths.get(loc).normalize().toString();
				}
				catch(InvalidPathException e)
				{
					if(!force)
					{
						ConsoleUtills.printError("Choosen path contains wrong characters");
						return "";
					}
					
				}
				if(!loc.endsWith(term_sign) && type != FileTypes.FILE )
				{
					loc = loc.concat(term_sign);
				}
				if(file.isDirectory() && type == FileTypes.FILE)
				{
					ConsoleUtills.printError("Choosen path is a file");
					return "";
					
				}
				//System.out.println(loc);
				File ready_file = new File(loc);
				return ready_file.getCanonicalPath().toString();
				
			}
			else 
			{
				ConsoleUtills.printError("Choosen path " + loc +  " not exist");
				return "";
			}
		}
		catch(NullPointerException e)
		{
			ConsoleUtills.printError("Choosen path is null (empty) ");
			return "";
		}
		
		
	}
	private String[] trimLastInPath(String argue)
	{
		String str[] = new String[2];		
		int last = argue.lastIndexOf(File.separator);
		if(last >= 0)
		{
			str[0] = argue.substring(last);
			str[1] = argue.substring(0,last);
		}
		else
		{
			str[0] = argue;
			str[1] = "";
		}
		return str;
	}
	private String compilePath(String def_loc, String path) throws InvalidPathException
	{
		//return FileSystems.getPath(path).normalize().toAbsolutePath().toString();
		return Paths.get(def_loc + path).normalize().toAbsolutePath().toString();
	}
}
