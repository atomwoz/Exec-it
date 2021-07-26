package add;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;

import jline.console.ConsoleReader;








//import com.sun.tools.javac.file.PathFileObject;


class AShell
{
		
}

public class Shell {

	public String visible_name = "";
	char promptChar = '>';
	static final char NULLCHAR = 0;
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
	
	public static BufferedReader brw = new BufferedReader(new InputStreamReader(System.in));
	
	public String[] readCMD(BufferedReader br) throws IOException
	{		
		
		System.out.print(visible_name + promptChar);	
		String input = br.readLine();
		String[] param = input.split("\\s+");
		return param;
	}
	public String[] readCMD() throws IOException
	{		
		System.out.print(visible_name + promptChar);	
		String input = brw.readLine();
		String[] param = input.split("\\s+");
		return param;
	}
	public Pair<String[],String> newreadCMD(boolean isRich) throws IOException
	{		
		String prompt = visible_name + promptChar;
		AttributedStringBuilder builder = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).bold())
                .append(prompt);
		
		
		System.out.print(isRich ? builder.toAnsi() : prompt);
		//String input = brw.readLine();
		String input = brw.readLine();

		String[] param = arrayToString(tokenizeInput(input));
		Pair<String[],String> p = new Pair<String[], String>(param, input);
		return p;
	}
	public Pair<String[],String> customreadCMD(BufferedReader br) throws IOException
	{		
		System.out.println(visible_name + promptChar);
		System.out.flush();
		String input = br.readLine();
		if(input == null) {return null;}
		String[] param = arrayToString(tokenizeInput(input));
		Pair<String[],String> p = new Pair<String[], String>(param, input);
		return p;
	}
	public Pair<String[],String> filereadCMD(BufferedReader file) throws IOException
	{		
		String input = file.readLine();
		if(input == null) {return null;}
		String[] param = arrayToString(tokenizeInput(input));
		Pair<String[],String> p = new Pair<String[], String>(param, input);
		return p;
	}
	public static String[] compileStartArgue(String cmd)
	{
		HashSet<String> returnstr = new HashSet<String>();
		returnstr.add(cmd.substring(0,cmd.indexOf(' ')));
		cmd = cmd.substring(cmd.indexOf(' '));
		int lastIndex = 0 , i = 0;
		for(char c : cmd.toCharArray())
		{
			if(c == ' ')
			{
				returnstr.add(cmd.substring(lastIndex,i));
				cmd = cmd.substring(i,lastIndex);
				lastIndex = i;
			}
			i++;
		}
		while(!cmd.isEmpty())
		{
			//Math.min(cmd.indexOf('-'),Math.min(cmd.indexOf('~'),cmd.indexOf('/')));
			cmd = cmd.substring(0,cmd.indexOf(' '));
			returnstr.add(cmd.substring(0,cmd.indexOf(' ')));
			
		}
		return (String[]) returnstr.toArray();
	}
	public static String readPassword(Console console) throws IOException
	{
		if(console == null)
		{
			return brw.readLine();
		}
		else
		{
			return new String(console.readPassword());
		}
	}
	public static void moveOrCopy(String fileName1,String fileName2, boolean force, boolean copy) throws IOException
	{
		//FileSystemProvider provider = FileSystems.getDefault().provider();
		try
		{
			if(copy)
			{
				if(force)
				{
					Files.copy(Paths.get(fileName1), Paths.get(fileName2),StandardCopyOption.REPLACE_EXISTING);
				}
				else
				{
					Files.copy(Paths.get(fileName1), Paths.get(fileName2));
				}
				
			}
			else
			{
				if(force)
				{
					Files.move(Paths.get(fileName1), Paths.get(fileName2),StandardCopyOption.REPLACE_EXISTING);
				}
				else
				{
					Files.move(Paths.get(fileName1), Paths.get(fileName2));
				}
				
			}
			
		}						
		catch(FileAlreadyExistsException e)
		{
			System.out.print("File " + fileName2 + " already exist , overwrite it? (y/n) ");
			String dec = brw.readLine();
			dec = dec.toLowerCase();
			if(dec.equals("y") || dec.equals("yes"))
			{
				try
				{
					if(copy)
					{
						Files.copy(Paths.get(fileName1), Paths.get(fileName2),StandardCopyOption.REPLACE_EXISTING);
					}
					else
					{
						Files.move(Paths.get(fileName1), Paths.get(fileName2),StandardCopyOption.REPLACE_EXISTING);
					}
				}
				catch(SecurityException en)
				{
					ConsoleUtills.printError("ERROR: No permisions for " + (copy ? "copying" : "moving") + " file");
				}
				catch(IOException en)
				{
					ConsoleUtills.printError("ERROR: In "+ (copy ? "copying" : "moving") +" file");
				}
			}
		}
		catch(SecurityException e)
		{
			ConsoleUtills.printError("ERROR: No permisions to " +(copy ? "copy" : "move") + " file");
		}
		catch(IOException e)
		{
			ConsoleUtills.printError("ERROR: In "+ (copy ? "copying" : "moving") +" file");
		}
				
		
			
	
	}
	public static void downloadFile(URL url, String outputFileName, boolean isAsync) throws IOException
    {
		String protocol = url.getProtocol();
		if(protocol.equals("http") || protocol.equals("https") || protocol.equals("file"))
		{
			File f = new File(outputFileName);
			if(!f.exists())
			{
				Shell.touchFile(outputFileName);
			}
			
				//System.out.println("I'm trying to download: " + new URL(s[1]).toString());
				//System.out.println("To: " + loc + filee);
				
				Thread thread = new Thread(new Runnable() 
				{
						
							@Override
							public void run() 
							{
								
								try 
								{
									InputStream in;
									in = url.openStream();
									ReadableByteChannel rbc = Channels.newChannel(in);   
						            try 
						            {
						            	FileOutputStream fos;
						            	try
						            	{
						            		fos = new FileOutputStream(outputFileName);
						            	}
						            	catch(FileNotFoundException e)
										{
											ConsoleUtills.printError("We can't find path to the target file");
											return;
										}
										fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
										if(isAsync)
										{
											System.out.println("\nDownloaded file " + outputFileName);
										}
										fos.close();
						            }
									catch(IOException e)
									{
										ConsoleUtills.printError("We can't find source file");
									}
								} 
								catch (IOException e1) 
								{
									ConsoleUtills.printError("Wrong url to download file specified");
								}
								
							}
								
				});
				if(isAsync)
				{
					System.out.println("Starting download file " + outputFileName);
					thread.start();
				}
				else
				{
					thread.start();
					try 
					{
						thread.join();
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			
			
		}
		else 
		{
			ConsoleUtills.printError("ERROR: Not supported protocol");
		}
    }
	public static void makeTree(File file, boolean isRichCompatible)
	{
		if(isRichCompatible)
		{
			try {
				ConsoleReader cr = new ConsoleReader();
				Terminal term = TerminalBuilder.builder().system(true).build();
				ConsoleUtills.clearScreen();
				
				System.out.println("Press [TAB] to make child folder from the current selected"
						+ "\n[ENTER] to make folder on the same level as current selected"
						+ "\nUse arrows for navigate");
				//TODO: Zrobić to
				cr.readCharacter();
				
				ConsoleUtills.finalizeConsole(cr);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		else
		{
			ConsoleUtills.printError("To run this command you need terminal with ANSI support, if you need to make folder please use mkdir");
		}
		
	}
	public static String genLine(int width)
	{
		String out = "";
		for(int i=0; i<width/3; i++)
		{
			out += "│  ";
		}
		return out;
	}
	public static void printTree(File file, int nodenum, boolean isRichANSITerminal)
	{
		if(nodenum == 0)
		{
			System.out.println(file.getName());
		}
		
		if(file.listFiles() != null)
		{
			for(File child : file.listFiles())
			{
				if(child.canExecute() && child.isDirectory())
				{
					AttributedStringBuilder builder = new AttributedStringBuilder()
				              .style(AttributedStyle.DEFAULT.foreground(nodenum/3).bold().background(AttributedStyle.WHITE))
				              .append(child.getName());
					System.out.println(Shell.genLine(nodenum) +  "├─" +(isRichANSITerminal ? builder.toAnsi() : child.getName()));
					printTree(child, nodenum + 3, isRichANSITerminal);		
				}
				else
				{
					AttributedStringBuilder builder = new AttributedStringBuilder()
				              .style(AttributedStyle.DEFAULT.foreground(nodenum/3).bold())
				              .append(child.getName());
					System.out.println(Shell.genLine(nodenum) +  "├─" +(isRichANSITerminal ? builder.toAnsi() : child.getName()));
				}
					
			}
		}
		
	}
	public static void generateFile(String fileName, BigInteger bytes, String content, String encoding) throws IOException
	{
		content = content.isEmpty() ? "A" : content;
		encoding = encoding.isBlank() ? "UTF-8" : encoding;
		try 
		{
			try(FileWriter garbageFile = new FileWriter(fileName,Charset.forName(encoding),true))
			{
				for(BigInteger i = new BigInteger("0"); i.compareTo(bytes) < 0 ; i = i.add(new BigInteger("1")))
				{
					try 
					{
						garbageFile.write(content);
					} 
					catch (IllegalCharsetNameException | UnsupportedEncodingException  e) 
					{
						ConsoleUtills.printError("ERROR: System not support this encoding");
						return;
					}
					catch (IOException e) 
					{
						ConsoleUtills.printError("ERROR: Unable to access specified file");
						return;
					}
				}
			}
		} 
		catch (IOException e1) 
		{
			ConsoleUtills.printError("ERROR: Unable to access specified file");
			return;
		}
		
		
		
	} 
	public static void touchFile(String fileName)
	{
		File file = new File(fileName);
		try
		{
			file.createNewFile();
		}
		catch(SecurityException e)
		{
			ConsoleUtills.printError("ERROR: You don't have permisions for creating file");
		}
		catch(IOException e)
		{
			ConsoleUtills.printError("ERROR: Error in creating file");
		}
	}
	public static String getFile(String fileName, String encoding, boolean useNumbered, boolean showWhiteChars) throws IOException , UnsupportedEncodingException , IllegalCharsetNameException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName,Charset.forName(encoding)));
		int i = 0;
		String line = "", content = "";
		line = reader.readLine();
		while(line != null)
		{
			if(showWhiteChars)
			{
				line = line.replace(' ', '·');
				line = line.replace(System.getProperty("line.separator"), "\\n");
				line = line.replace("\t", "→");
				line = line.replace("\r", "«");
				line = line.replace("\f", "Ḟ");
			}
			content = content.concat((useNumbered ? (i+1) + ": " : "" )+ line+"\n");
			line = reader.readLine();
			i++;
		}
		reader.close();
		return content;
	}
	public static String superGetFile(String fileName) throws IOException , UnsupportedEncodingException , IllegalCharsetNameException
	{
		return getFile(fileName, "UTF-8", false, false);
	}
	public static void readFile(String fileName, String encoding, boolean useNumbered, boolean showWhiteChars) throws IOException , UnsupportedEncodingException , IllegalCharsetNameException
	{
		PrintStream ps = new PrintStream(System.out, true, encoding);
		ps.println();
		String content = getFile(fileName, encoding, useNumbered, showWhiteChars);
		if(!content.isBlank())
		{
			ps.println(content);
		}
		else
		{
			ps.println("-- File is empty --");
		}
		ps.println();
	}
	public static void writeFile(String fileName, String writeThis, String encoding, boolean append) throws IOException , UnsupportedEncodingException , IllegalCharsetNameException, UnsupportedCharsetException
	{
		File file = new File(fileName);
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch(SecurityException e)
			{
				ConsoleUtills.printError("ERROR: You don't have permisions for creating file");
			}
			catch(IOException e)
			{
				ConsoleUtills.printError("ERROR: Error in writing to file");
			}
		}
		FileWriter writer = new FileWriter(fileName,Charset.forName(encoding),append);
		writer.write(writeThis);
		writer.close();
	}
	public static void diskInfo()
	{
		File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
            	float freeSpace = aDrive.getFreeSpace();
            	float totalSpace = aDrive.getTotalSpace();
            	double allocatedSpace = totalSpace - freeSpace;
            	double procentallocatedSpace = Math.round(10000-((freeSpace/totalSpace)*10000))/100d;
                System.out.println("Drive: " + aDrive);
                System.out.println("\tTotal space: " + Shell.simplifyMemory(totalSpace,false,false));
                System.out.println("\tFree space: " + Shell.simplifyMemory(freeSpace,false,false));
                System.out.println("\tUsed space: " + Shell.simplifyMemory((float) allocatedSpace,false,false));
                System.out.println("\tUsed space: " + procentallocatedSpace + "%");
                System.out.println();
            }
        }
	}
	public static void swapToWrite(BufferedReader bf, BufferedReader in, PrintWriter out) throws IOException
	{
		String input = "";
		//System.out.print("[YOU] ");
	    while(!(input.equals("!bye") || input.equals("!exit")))
	    {	
	    	System.out.print("\r[YOU] ");
	    	input = bf.readLine();
	    	out.println(input);
   	
	    }
	}
	public static void swapToRead(String name, BufferedReader bf, BufferedReader in, PrintWriter out) throws IOException
	{
		String input = "";
	    while(!(input.equals("!bye") || input.equals("!exit")))
	    {   	
	        input = in.readLine();
		    System.out.print("\r[" + name + "]" + " " +input + "\n");
		    System.out.print("[YOU] ");
	    }
	}
	public static void callExec(String s[], String loc,boolean cut)
	{
		if(s.length >= 2)
		{	
				try
				{
					if(cut)
					{
						s[0] = loc;
						s[1] = "";
					}
					else
					{
						s[0] = loc;
					}
					ProcessBuilder pb = new ProcessBuilder(s);
					pb.inheritIO();
					Process pr = pb.start();
					try 
					{
						pr.waitFor();
					} 
					catch (InterruptedException e) 
					{
						ConsoleUtills.printError("Interrupted execution of "+ loc + " program halted");
					}
				}
				catch(IOException e)
				{
					ConsoleUtills.printError("Error with starting process " + loc);
				}
				
			
		}
	}
	public static void system(boolean isWindows)
	{
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(ConsoleUtills.getInstance().getDefaultShell());
		pb.inheritIO();
		try {
			pb.start().waitFor();
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void executeSystemCommand(String alline, boolean isRunningOnWindows) throws IOException
	{
		//System.out.println(input + "||||" + argue + "||||" + alline);
		String argue = alline.substring(1);	
		//System.out.println(input + "||||" + argue);
		
		if(!argue.isBlank())
		{
			ArrayList<String> al = tokenizeInput(argue);
			String newtokens[] = new String[al.size() + 2];
			String tokens[] = Shell.arrayToString(al);
			for(int i=0; i<tokens.length; i++)
			{
				if(tokens[i] != null)
				{
					newtokens[i+2] = tokens[i];
				}
					
			}
			if(isRunningOnWindows)
			{
				newtokens[0] = "cmd";
				newtokens[1] = "/c";
			}
			else
			{
				newtokens[0] = ConsoleUtills.getInstance().getDefaultShell();
				newtokens[1] = "-c";
			}
			ProcessBuilder pb = new ProcessBuilder(newtokens);
			try 
			{
				pb.inheritIO();
				pb.start().waitFor();
					
			}
			catch (InterruptedException e) {}
			
		}
		
	}
	
	
	public static boolean isInTable(String[] check, String[] str, int firstCheckedElement)
	{
		for(int i = firstCheckedElement; i<str.length; i++)
		{
			for(String s : check)
			{
				if(str[i].equals(s))
				{
					return true;		
				}
			}
			
		}
		return false;
	}
	public static Process startProcess(boolean echo,String name) 
	{
		try {
			Process p = Runtime.getRuntime().exec(name);
			if(echo)
			{
				System.out.println(p.info());
				System.out.println(p.pid());
			}
			return p;
	
				
		} catch (IOException e) {
			ConsoleUtills.printError("Error z wystartowaniem: " + name);
			return null;
		}
		
	}
	public static void rmsClient(Pair<PrintStream, BufferedReader> pair) throws InterruptedException
	{
		Thread writer = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				BufferedReader bf = pair.snd;
				String input;
				while(true)
				{
					try {
						input = bf.readLine();
						if(input == null)
						{
							break;
						}
						System.out.println(input);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		Thread reader = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				PrintStream ps = pair.fst;
				String input;
				while(true)
				{
					try {
						input = brw.readLine();
						if(input == null)
						{
							break;
						}
						ps.println(input);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		reader.start();
		writer.start();
		reader.join();
		writer.join();
	}
	//TODO: Ogarnać bo nie działa
	public static void subChatApp(BufferedReader bf, BufferedReader in, PrintWriter out, Socket clientSocket, int version, String appTransferKey) throws IOException
	{
		final char KEY = '\u0002';
		final String ENCRYPTION = "no encryption";
				
		System.out.print("Give your name: ");
        String name = bf.readLine();
        System.out.print("I'm waiting for other host for give name");
       
        
        //It sends header it consist of 88658673[KEY][name of user][KEY][version][KEY][encryption][KEY]88658673 
        if(name.contains(KEY + ""))
        {
        	name = name.replace(KEY + "", " ");
        }
        out.println(appTransferKey + KEY + name + KEY + version + KEY + ENCRYPTION + KEY + appTransferKey);
        //Reading and compiling other header
        String header = in.readLine();
        String[] headerlines = header.split("\\u0002+");
        if(headerlines.length < 5)
        {
        	ConsoleUtills.printError("\rERROR: Too short communication header\t\t");
        	return;
        }
        if(!headerlines[0].equals(appTransferKey)|| !headerlines[4].equals(appTransferKey))
        {
        	ConsoleUtills.printError("\rERROR: Wrong communication header\t\t\t");
        	return;
        }
        String othername = headerlines[1];
        int otherversion = -1;
        String othercryption = headerlines[3];
        System.out.println("\rUsing encryption: " + othercryption + "   \t\t\t");
        try
        {
        	int x = Integer.valueOf(headerlines[2]);
        	otherversion = x;
        }
        catch(NumberFormatException e)
        {
        
        }
        if(otherversion == -1)
        {
        	ConsoleUtills.printError("ERROR: Header contains wrong version number, do you want to contiunue (Y/N)? ");
        	String input = bf.readLine();
        	input = input.toLowerCase();
        	if(input.equals("y") || input.equals("yes"))
        	{
        		otherversion = Integer.MAX_VALUE;
        	}
        	else
        	{
        		return;
        	}
           
        }
        //readFrom(header, ";;;");
        if(otherversion > version)
        {
        	ConsoleUtills.printError("!!! You have older version of app than other host, some function may working different !!!");
        }
        else if(otherversion < version)
        {
        	ConsoleUtills.printError("!!! You have newer version of app than other host, some function may working different !!!");
        }
        
        final String supername = othername;
        System.out.print("\rYou are chatting with " + othername + "\t\t\t\t\n\tStart chatting\n =============================\n");
        Thread writeThread = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				try {
					Shell.swapToWrite(bf, in, out);
				} catch (IOException e) 
				{
				
						System.out.println("Host was disconected");
					
					
				}		
			}
		});
    	Thread readThread = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				try 
				{
					Shell.swapToRead(supername,bf, in, out);
				} catch (IOException e) 
				{
					System.out.println("Host was disconected");
				}		
			}
		});
    	writeThread.start();
    	readThread.start();
    	while(writeThread.isAlive() && readThread.isAlive() && clientSocket.isConnected()) {}
    	
	}
	public static String readUntill(String str, String untill)
	{
		int firstTime = str.indexOf(untill);
		if(firstTime < -1) return null;
		return str.substring(0,firstTime);
	}
	public static String readFrom(String str, String untill)
	{
		int firstTime = str.indexOf(untill) + untill.length();
		if(firstTime < -1 + untill.length()) return null;
		return str.substring(firstTime);
	}
	public static Process runShell(Process p, boolean echo)
	{
		String input;
		try {
			if(echo)
			{
				System.out.println(p.info());
				System.out.println(p.pid());
			}
			String str = "";// = brw.readLine();
			while(str.compareTo("exit") != 0)
			{
				if(!p.isAlive())
				{
					System.out.println("Procces was ended");
					break;
				}
				Shell shell2 = new Shell(processInfo(p),'>');
				String[] strr = shell2.readCMD();
				str = strr[0];
				if(str.compareTo("pid") == 0)
				{
					System.out.println(p.pid());
				}
				else if(str.compareTo("terminate") == 0 || str.compareTo("destroy") == 0)
				{
					p.destroyForcibly();
					break;
				}
				else if(str.compareTo("end") == 0 || str.compareTo("kill") == 0)
				{
					p.destroy();
					break;
				}
				else if(str.compareTo("is_alive") == 0)
				{
					System.out.println(p.isAlive() ? "Proces działa" : "Proces jest wyłączony");
				}
				else if(str.compareTo("io") == 0)
				{
					if(str.length() > 3)
					{
						input = str.substring(3);
					}
					else
					{
						input = "";
					}
					
					
					if(input.compareTo("file") == 0)
					{
						
					}
					else
					{
						System.out.println("Strumień wejścia: "+ p.getInputStream());
						System.out.println("Strumień wyjścia: "+ p.getOutputStream());
						System.out.println("Strumień błędu: " + p.getErrorStream());
					}
					
					
				}
				else if(str.startsWith("wait"))
				{
					try
					{
						str = str.substring(5);
						System.out.println(p);	
						synchronized(p)
						{
							
							p.wait(Integer.parseInt(str));
						}
						
					}
					catch(NumberFormatException n)
					{
						ConsoleUtills.printError("Nieprawidłowy czas czekania");
					}
					catch(InterruptedException e)
					{
						ConsoleUtills.printError("Błąd przerwania");
					}
					catch(IllegalMonitorStateException e)
					{
						ConsoleUtills.printError("Błąd monitora");
					}
					
					
				}
				else if(str.compareTo("watch") == 0)
				{
					StartWatcher(p);
					System.out.println("Rozpoczęto monitor");
					
				}
				
			}
			
		} catch (IOException e) {
			ConsoleUtills.printError("Internall shell error " + e.getMessage());
		}
		return p;
	}
	public Shell()
	{
		
	}
	public Shell(String name) 
	{
		visible_name = name;
	}
	public Shell(char prompt) 
	{
		promptChar = prompt;
	}
	public Shell(String name,char prompt) 
	{
		promptChar = prompt;
		visible_name = name;
	}
	
	static void StartWatcher(Process p)
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true)
				{
					if(!p.isAlive())
					{
						System.out.println();
						System.out.println(processInfo(p) + " was terminated with exit code " + p.exitValue());
						break;
					}
					/*try {
						synchronized(p)
						{
							this.wait(100);
						}
						
					} catch (InterruptedException e) {
						
					}*/
					
				}
				
				
			}
		});
		t.start();
		
	}
	public static String replaceCharacters(String str)
	{
		str = str.replace("\\n","\n");
		str = str.replace("<\br>","\n");
		str = str.replace("\\t","\t");
		str = str.replace("<\tb>","\t");
		str = str.replace("\\r","\r");
		return str;
	}
	public static BigInteger bigMemory(String mem, boolean useBytes)
	{
		int multiplier = 1024;
		int divider =  useBytes ? 1 : 8;
		mem = mem.toUpperCase();
		if(mem.length() < 1)
		{
			ConsoleUtills.printError("ERROR: Wrong size specified");
			return null;
		}
		if(!mem.endsWith("B") && !mem.endsWith("b"))
		{
			mem = mem.concat("B");
		}
		char[] prefixList = {NULLCHAR,'K','M','G','T','P','E','Z','Y'};
		int globali = -2,lastChar = 0;
		char prefixChar = mem.charAt(mem.length()-2);
		lastChar = mem.length()-2;
		try
		{
			Integer.valueOf(String.valueOf(prefixChar));
			prefixChar = NULLCHAR;
			lastChar = mem.length()-1;
		}
		catch(NumberFormatException e){}
		for(int i=0; i<prefixList.length; i++)
		{
			if(prefixList[i] == prefixChar)
			{
				globali = i;
				break;
			}
		}
		if(globali == -2)
		{
			ConsoleUtills.printError("ERROR: Wrong size specified");
			return null;
		}
		
		BigInteger bigMultiplier =  BigInteger.valueOf(multiplier);
		BigInteger value = new BigInteger(mem.substring(0,lastChar));
		return bigMultiplier.pow(globali).multiply(value).divide(BigInteger.valueOf(divider));
		//return (Math.pow(multiplier,globali) * value)/divider;
	}
	public static double longMemory(String mem, boolean useBytes)
	{
		BigInteger ret = bigMemory(mem, useBytes);
		if(ret != null)
		{
			return ret.doubleValue();
		}
		else
		{
			return -1;
		}
	}
	
	public static String simplifyMemory(float memory, boolean isBite, boolean useHunderts)
	{
		int onebyte = useHunderts ? 1000 : 1024;	
		char[] prefixList = {NULLCHAR,'K','M','G','T','P','E','Z','Y'};
		int i = 0;
		DecimalFormat df = new DecimalFormat("0.00");
		if(isBite)
		{
			memory /= 8;
		}
		while(memory >= onebyte && i < prefixList.length)
		{
			memory /= 1024;
			i++;
		}
		return String.valueOf(df.format(memory)) + prefixList[i] + "B";
		
	}
	public static String[] arrayToString(ArrayList<String> lines)
	{
		String[] table = new String[lines.size()];
		//System.out.println("Pojemnosc tablicy: " + table.length);
		for(int i = 0; i<lines.size(); i++)
		{
			table[i] = lines.get(i);
		}
		return table;
	}
	public static ArrayList<String> tokenizeInput(String string)
	{
		final String openToken = " \"";
		final String closeToken = "\" ";
		boolean isOnApos = false;
		ArrayList<String> lines = new ArrayList<String>();
		String str = string;
		/*if(str.indexOf(openToken) == -1)
		{
			String[] line = str.split("\\s+");
			for(String stru : line)
			{
				lines.add(stru);
			}
			return lines;
		}*/
		int lastStamp = 0;
		int ignore = 0;
		str = str.concat(" ");
		str = str.replace("\" \"", "\"  \"");
		for(int i=0; i<str.length()-1; i++)
		{
			if(ignore > 0)
			{
				ignore --;
				continue;
			}
			char c = str.charAt(i);
			char x = c;
			if(i+1 < str.length())
			{
				x = str.charAt(i+1);
			}
			String seq = Character.valueOf(c).toString() + Character.valueOf(x).toString();
			//System.out.println(seq);
			if(Character.isWhitespace(c) && Character.isWhitespace(x))
			{
				continue;
			}
			if(openToken.equals(seq) && !isOnApos)
			{
				isOnApos = true;	
				String substr = str.substring(lastStamp,i);
				if(Character.isWhitespace(substr.charAt(0)))
				{
					substr = substr.substring(1);
				}
				if(!substr.isEmpty())
				{
					lines.add(substr);
					
				}
				lastStamp = i;
				
			}
			else if (closeToken.equals(seq) && isOnApos)
			{
				isOnApos = false;
				String ready = str.substring(lastStamp,i);
				ready = ready.substring(2);
				//ready = ready.substring(0,ready.length()-1);
				if(Character.isWhitespace(ready.charAt(0)))
				{
					ready = ready.substring(1);
				}
				if(!ready.isEmpty())
				{
					lines.add(ready);
					
				}
				lastStamp = i+1;
				//str = str.trim();
				ignore = 1;
				
			}
			else if(Character.isWhitespace(c) && !isOnApos)
			{
				//System.out.println("SPACE " + isOnApos);
				String lastStr = str.substring(lastStamp,i).trim();
				if(!lastStr.isBlank())
				{
					lines.add(lastStr);
					lastStamp = i;
				}
				
				
			}
			
		}
		String stri = str.substring(lastStamp).trim();
		if(stri.startsWith("\""))
		{
			stri = stri.substring(1);
		}
		if(stri.endsWith("\""))
		{
			stri = stri.substring(0,stri.length()-1);
		}
		lines.add(stri);
		//String strr = new String();
		if(lines.toArray()[lines.size()-1].toString().isBlank())
		{
			lines.remove(lines.size()-1);
		}
		return lines;
	}
	
	public static String horizontalSeparator(String separator)
	{
		String str = "";
		for(int i=0; i<ConsoleUtills.getTerminalWidth()-1; i++)
		{
			str = str + separator;
		}
		return str;
	}
	public static void stress()
	{
		int x = 0;
		for(long i=0; i<100000*100000; i++)
		{
			for(long j=0; j<5; j++)
			{
				x = 23 - 2*5;
			}
			x = 23 - 2*5;
		}
		int z = x;
	}
	static String processInfo(Process p)
	{
		String str;
		try
		{
			str = "PROCESS: " + LocalProcesses.getNameFromProcess(p) + " PID: " + p.pid();
		}catch(NoSuchElementException e)
		{
			str = "";
		}
		
		return str;
	}
	

}
