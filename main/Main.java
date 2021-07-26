package main;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.NotLinkException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import add.BitmapDrawer;
import add.CPULoader;
import add.CPUTest;
import add.ConsoleMultiList;
import add.ConsoleSimpleSplash;
import add.ConsoleUtills;
import add.Garbage;
import add.LocalProcesses;
import add.Pair;
import add.Paraser;
import add.PathEngine;
import add.Processes;
import add.Shell;
import add.YesNoDialog;


//import com.sun.tools.javac.file.PathFileObject;


//This simple shell was made in 100% by Antoni Wozniak for someone important
//It target is to unify shells in Mac , Linux, Unix , Windows, Open BSD, Solaris , and be user friendly
// Version very non stable 0.0.0.0.0.0.0.0.0.0.0.0.0.0.2b
//Have a nice day !!!

public class Main {
	

	static String def_localization;
	static String exec_localization;
	static String term_sign = "/";
	static LocalProcesses localProcesses;
	static boolean isRunningOnWindows = false;
	static boolean isRichANSITerminal = false;
	static String alline = "";
	static String sys_localization = "\\";
	static boolean once = false, autoPathCorrect = true;
	static ArrayList<Garbage> allocs = new ArrayList<Garbage>();
	static PathEngine engine;
	//static ConsoleReader consoleUtills;
	public static enum FileTypes {FILE,DIR,BOTH};
	
	public static void main(String[] args) throws Exception {
		
		System.out.print("Starting...");
		
		//App constant
		final String SHELL_NAME = "ASH";
		final String SHELL_NICE_VERSION = "0.0.3a";
		//final int SHELL_VERSION = 1;
		final String DEFAULT_PASSWORD = "zaq1@WSX";
		
		//Declaring app variables
		localProcesses = new LocalProcesses();
		String os = System.getProperty("os.name");
		boolean isDebug = false, isExecuting = false, isFirstExecution = false;
		String executing_from = "";
		term_sign = File.separator;
		String accessPassword = DEFAULT_PASSWORD;
		String encryptionKey = "";
		
		
		//Checking system
		if(os.toLowerCase().contains("windows"))
		{
			 sys_localization = "C:\\Windows\\System32\\";
			 isRunningOnWindows = true;
		}
		else
		{
			sys_localization = System.getProperty("user.home");
		}
		isRichANSITerminal =  ConsoleUtills.isAnsiTerminal();
		ConsoleUtills utills =  ConsoleUtills.getInstance();
		
		//Init app variables
		def_localization = sys_localization;
		exec_localization = System.getProperty("user.dir");
		Process p = null;
		Runtime.getRuntime().addShutdownHook(
				new Thread()
				{
					@Override
					public void run() {
							System.out.println("\nThanks for using " +  SHELL_NAME + " " + SHELL_NICE_VERSION );
							if(!once)
							{
								System.out.print("Press enter to end");
								try 
								{
									System.in.read();
								} 
								catch (IOException e) {}
							}
							
					}
				}
				
				);
		BufferedReader br =  new BufferedReader(new InputStreamReader(System.in));
		Console mainConsole = System.console();
		engine = new PathEngine(isRunningOnWindows, exec_localization);
		if(mainConsole == null)
		{
			ConsoleUtills.printError("There isn't any Java console associated with that process !!!");
			boolean answer = YesNoDialog.printSimpleYN(br, "Do you want use legacy functions for things like reading passwords ?");
			if(!answer)
			{
				return;
			}
					
		}
		String input = "";
		boolean customIo = false;
		boolean echo = false;
		
		Shell shell = new Shell(def_localization);	
		//Old java check
		try
		{
			Runtime.getRuntime();
			//int ver = Runtime.version().build().get();
		}
		catch(NoSuchMethodError e)
		{
			ConsoleUtills.printError("Try to run this app on newer java (m.in 9) ");
			System.err.print("If you want to force run on this version type 'force' or 'f' if not press ENTER: ");
			String dec = br.readLine();
			if(dec.equals("force") || dec.equals("f"))
			{
				ConsoleUtills.printError("Legacy mode ,some functions can't work properly");
			}
			else
			{
				return;
			}
			
		}
		
		//Compiling start argues
		if(args.length > 0)
		{
			String loc = cd("",args[0],true,false,false,FileTypes.FILE);
			File startFile = new File(loc);
			if(startFile.exists())
			{
				executing_from = loc;
				isExecuting = true;
				isFirstExecution = true;
				once = true;
				turnOffPrinting();
			}
		}
		//Start code
		//System.out.println(System.getenv().get("TERM"));
		ConsoleUtills.clearScreen(isRunningOnWindows);
		System.out.println("\rWelcome in " + SHELL_NAME + " " + SHELL_NICE_VERSION);
		System.out.println("Made by atomwoz and published under BSD License ");
		System.out.println(Shell.horizontalSeparator("="));
		//App main body
		while(true)
		{
			if(isExecuting && isFirstExecution && !customIo)
			{
				//System.out.println("Plik br");
				br =  new BufferedReader(new FileReader(executing_from));
				isFirstExecution = false;
			}
			else if(!isExecuting && !customIo)
			{
				//System.out.println("Zwykle czytanie");
				br =  new BufferedReader(new InputStreamReader(System.in));
			}
			if(!isExecuting && once)
			{
				return;
			}
			Shell.brw = br;
			Processes.br = br;
			/*if(thread != null && thread1 != null)
			{
				thread.stop();
				thread1.stop();
			}*/
			shell.visible_name = def_localization;
			Pair<String[],String> pair = null;
			if(isExecuting && !customIo)
			{
				pair = shell.filereadCMD(br);
				if(pair == null)
				{
					//System.out.println("Pair to null");
					isExecuting = false;
					continue;
				}
			}
			else if(!customIo)
			{
				pair = shell.newreadCMD(isRichANSITerminal);
			}
			else if(customIo)
			{
				pair = shell.customreadCMD(br);
			}
			String[] s = pair.fst;
			alline = pair.snd;
			if(alline.isBlank()) { continue;}
			input = s[0];
			input = input.toLowerCase();
			//input = input.replace("&", "force_");
			String argue = "";
			try
			{
				s[0] = "";
				for(String str : s)
				{
					
					argue = argue.concat(str);
					argue = argue.concat(" ");
					
				}
				
				if(argue.length() - 1 > 0)
				{
					argue = argue.substring(1);
					argue = argue.substring(0, argue.length() - 1);
				}
				else
				{
					argue = "";
				}
				
				//argue = s[1];
				//System.out.println(argue);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				argue = "";
			}
			
			if(!allocs.isEmpty())
			{
				for(Garbage g : allocs)
				{
					g.useGarbage();
				}
			}
			
		
			
			if(input.startsWith("~") || input.startsWith("-")  || input.startsWith("!") )
			{
				
				input = input.substring(1);
				//System.out.println(input + "  " + s[0] + "  " + argue);
				if(input.compareTo("exit") == 0 || input.compareTo("end") == 0 || input.compareTo("bye") == 0)
				{
					if(!customIo)
					{
						return;
					}
					else
					{
						turnOnPrinting();
						customIo = false;
						continue;
					}
				}
				else if(input.equals("shell") || input.equals("super") || input.equals("terminal") || input.equals("cmd"))
				{
					Shell.system(isRunningOnWindows);
				}
				else if(input.compareTo("print") == 0 || input.compareTo("println") == 0)
				{
					System.out.println(argue);
				}
				else if(input.compareTo("system") == 0 || input.compareTo("command") == 0)
				{
					if(argue.isBlank())
					{
						Shell.system(isRunningOnWindows);
					}
					try
					{
						Shell.executeSystemCommand("X" + argue, isRunningOnWindows);
					}
					catch(IOException e)
					{
						ConsoleUtills.printError("Error with starting system command");
					}
				}
				else if(input.compareTo("whoami") == 0 || input.compareTo("who") == 0 || input.compareTo("current_user") == 0)
				{
					System.out.println(System.getProperty("user.name"));
					
				}
				else if(input.compareTo("reset_out") == 0 || input.compareTo("out_console") == 0 || input.compareTo("out_to_console") == 0)
				{
					turnOnPrinting();
				}
				else if(input.equals("time"))
				{
					DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
					formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
					System.out.println(formatter.format(System.currentTimeMillis()));
					
				}
				else if(input.equals("date"))
				{
					DateFormat formatter = new SimpleDateFormat("dd.MM.YYYY");
					formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
					System.out.println(formatter.format(System.currentTimeMillis()));
					
				}
				//TODO
				else if(input.equals("file") || input.equals("file_info"))
				{
					String loc = cd(def_localization, argue, false, false, false, FileTypes.BOTH);
					File file = new File(loc);
					if(file.exists() && !loc.isBlank())
					{
						
					}
					
				}
				else if(input.equals("timer"))
				{
					add.Timer.runTimer();
				}
				else if(input.equals("set_key") || input.equals("set_encrypt_key") || input.equals("set_decrypt_key"))
				{
					System.out.print("Give new key, or press ENTER for not setting: ");
					String key = Shell.readPassword(mainConsole);
					if(!key.isEmpty())
					{
						encryptionKey = key;
						System.out.println("New key is set");
					}	
					else
					{
						System.out.println("Your old key is very good (if it is)");
					}
				}
				else if(input.equals("get_key") || input.equals("get_encrypt_key") || input.equals("get_decrypt_key") || input.equals("show_key")  
						|| input.equals("show_encrypt_key") || input.equals("show_decrypt_key"))
				{
					if(YesNoDialog.printSimpleYN(br, "Do you want to show encryption key?"))
					{
						if(encryptionKey.isBlank())
						{
							System.out.println("Encryption key isn't set yet");
						}
						else
						{
							System.out.println("Encryption key: " + encryptionKey);
						}
						
					}
					else
					{
						System.out.println("Your key, your privacy!");
					}
					
				}
				else if(input.equals("set_rms_password") || input.equals("set_rmc_password")  || input.equals("set_remonte_password") || input.equals("set_connection_password"))
				{
					System.out.print("Give your new password, or press ENTER for not setting: ");
					String key = Shell.readPassword(mainConsole);
					if(!key.isEmpty())
					{
						accessPassword = key;
						System.out.println("New password is set");
					}	
					else
					{
						System.out.println("Your old password is nice, or not");
					}
				}
				else if(input.equals("get_rms_password") || input.equals("get_rmc_password")  || input.equals("get_remonte_password") || input.equals("get_connection_password") 
						|| input.equals("show_rms_password") || input.equals("show_rmc_password")  || input.equals("show_remonte_password") 
						|| input.equals("show_connection_password"))
				{
					if(YesNoDialog.printSimpleYN(br, "Do you want to show RMS/connection password?"))
					{
						if(accessPassword.isBlank())
						{
							System.out.println("RMS password isn't set yet");
						}
						else
						{
							System.out.println("RMS password: " + accessPassword);
						}
						
					}
					else
					{
						System.out.println("Your password, your privacy!");
					}
				}
				else if(input.compareTo("path") == 0 || input.compareTo("pwd") == 0)
				{
					if(argue.isBlank())
					{
						System.out.println(def_localization);
					}
					else
					{
						String str = cd(def_localization,argue,true,false,false,FileTypes.BOTH);
						File file = new File(str);
						System.out.println((file.exists() ? (file.isDirectory() ? "[DIR] " : "[FILE] ") : "") + (file.exists() ? "[EXISTS]\t" : "[NOT EXISTS]\t") + str);
					}
					
				}
				else if(input.compareTo("remote_terminal") == 0 && isDebug)
				{
					String tab[] = argue.split(":");
					Socket socket = new Socket(tab[0], Integer.valueOf(tab[1]));
					Thread writer = new Thread(new Runnable() {
						
						@Override
						public void run() 
						{
							BufferedReader bf = null;
							try {
								bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							} catch (IOException e1) 
							{
								return;
							}
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
					final BufferedReader bf = br;
					Thread reader = new Thread(new Runnable() {
						
						@Override
						public void run() 
						{
							
							PrintStream ps;
							try {
								ps = new PrintStream(socket.getOutputStream());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								return;
							}
							String input;
							while(true)
							{
								try {
									input = bf.readLine();
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
					socket.close();
					
				}
				else if(input.compareTo("simple_math") == 0)
				{
					if(!argue.isBlank())
					{
						System.out.println(Paraser.simpleParase(argue));
					}
				}
				else if(input.compareTo("math") == 0)
				{
					Processes.MathSubshell(argue);
				}
				else if(input.compareTo("tree") == 0 || input.compareTo("dirs") == 0)
				{
					System.out.println();
					if(argue.isBlank())
					{
						Shell.printTree(new File(def_localization), 0, isRichANSITerminal);
					}
					else
					{
						String loc = cd(def_localization, argue, false, false, false, FileTypes.DIR);
						if(!loc.isBlank())
						{
							Shell.printTree(new File(loc), 0, isRichANSITerminal);
						}
					}
					
					System.out.println();
				}
				else if(input.equals("download") || input.equals("curl") || input.equals("wget"))
				{
					superDownload(s, argue, false);
				}
				else if(input.equals("async_download") || input.equals("async_curl") || input.equals("async_wget"))
				{
					superDownload(s, argue, true);
				}
				else if(input.compareTo("chat_server") == 0)
				{
					if(s.length >= 2)
					{
						try
						{
							Processes.ChatApp("", Integer.valueOf(s[1]), true);
						}	
						catch(NumberFormatException e)
						{
							ConsoleUtills.printError("Error Wrong port number");
						}
						
					}
					else
					{
						Processes.ChatApp("", -1, true);
					}
					
				}
				else if(input.compareTo("chat_client") == 0)
				{
					if(s.length == 2)
					{
						Processes.ChatApp(s[1], -1, false);
						
					}
					else if(s.length > 2)
					{
						try
						{
							Processes.ChatApp(s[1],Integer.valueOf(s[2]), false);
						}
						catch(NumberFormatException e)
						{
							ConsoleUtills.printError("Error Wrong port number");
						}
					}
				}
				else if(input.compareTo("ididi") == 0)
				{
					System.out.println("Mouted");
				}
				else if(input.compareTo("test_bitmap") == 0)
				{
					BitmapDrawer bd = new BitmapDrawer();
					byte[][] bitmap =
					{
							{1,0,0,1},
							{0,1,1,0},
							{1,0,0,1},
					};
					bd.bitmap = bitmap;
					bd.fillChar = '#';
					bd.emptyChar = ' ';
					bd.show();
					
					
				}
				else if(input.compareTo("path_correct") == 0)
				{
					if(argue.equals("off") || argue.equals("false") || argue.equals("disable"))
					{
						autoPathCorrect = false;
						System.out.println("Disabled path separator correction");			
					}
					else if(argue.equals("on") || argue.equals("true") || argue.equals("enable"))
					{
						autoPathCorrect = true;
						System.out.println("Enabled path separator correction");
					}
					else if(argue.isBlank())
					{
						System.out.println(autoPathCorrect ? "enabled" : "disabled");
					}
					else
					{
						ConsoleUtills.printError("Not recognized path correction syntax try on/off");
					}
				}
				else if(input.compareTo("disable_path_correct") == 0)
				{
					autoPathCorrect = false;
					System.out.println("Disabled path separator correction");
				}
				else if(input.compareTo("enable_path_correct") == 0)
				{
					autoPathCorrect = true;
					System.out.println("Enabled path separator correction");
				}
				else if(input.compareTo("del") == 0 || input.compareTo("rm") == 0 || input.compareTo("remove") == 0 || input.compareTo("delete") == 0)
				{
					String localization = cd(def_localization, argue,true,false,false,FileTypes.BOTH);
					localization = new File(localization).exists() ? localization : "";
					if(!localization.isBlank())
					{
						File file = new File(localization);
						if(!file.delete())
						{
							ConsoleUtills.printError("In deleting " + localization + " ,check your permisions to file");
						}
					}
					else
					{
						ConsoleUtills.printError("File can't be deleted, check that file exist");
					}
					
				}
				else if(input.compareTo("debug_mode") == 0)
				{
					isDebug = true;
					System.out.println("WELCOME IN VERY VERY VERY SECRET DEBUG MODE ADMINNN");
				}
				else if((input.compareTo("test") == 0) && isDebug)
				{
					System.out.println("Siema \r To jest \r testttttttttt");
				}
				// Displays a system info and where you work (whereiw - where i work , whoit - who it)
				else if(input.compareTo("whereiw") == 0 || input.compareTo("whoit") == 0 || input.compareTo("system_info") == 0 || input.compareTo("host_info") == 0)
				{
					Runtime currentRuntime = Runtime.getRuntime();
					System.out.println();
					System.out.println(isRunningOnWindows ? "Windows like system" : "Non Windows like system");
					System.out.println("System name: " + System.getProperty("os.name"));
					System.out.println("System version: " + System.getProperty("os.version"));
					System.out.println("System architecture: " + System.getProperty("os.arch"));
					System.out.println();
					System.out.println("Avalible CPU's " + currentRuntime.availableProcessors());
					System.out.println("Maximum memory: " + Shell.simplifyMemory(currentRuntime.maxMemory(),false,false));
					System.out.println("Used memory: " + Shell.simplifyMemory(currentRuntime.maxMemory() - currentRuntime.freeMemory(),false,false));
					System.out.println("Allocated memory: " + Shell.simplifyMemory(currentRuntime.totalMemory(),false,false));
					System.out.println("Free memory: " + Shell.simplifyMemory(currentRuntime.freeMemory(),false,false));
					System.out.println();
					
				}
				else if(input.equals("load_garbage") || input.equals("alloc_memory") || input.equals("garbage_memory") || input.equals("alloc_mem"))
				{
					allocs.add(new Garbage(Math.round(Shell.longMemory(argue,true))));
				}
				else if(input.equals("test_mem"))
				{
					System.out.println(Shell.longMemory(s[1],s[2].equals("B") ? true : false));
				}
				else if(input.equals("clear_garbage") || input.equals("free_memory") || input.equals("collect_garbage") || input.equals("free_alloc") || input.equals("free_mem"))
				{
					allocs.clear();
				}
				else if(input.compareTo("force_cd") == 0)
				{
					def_localization = cd(def_localization, argue,true,false, false,FileTypes.DIR);	
				}
				else if(input.compareTo("local_proc") == 0 || input.compareTo("processes") == 0 || input.compareTo("local_tasks") == 0 || input.compareTo("background") == 0 || input.compareTo("back") == 0)
				{
					System.out.println(localProcesses);
				}
				else if(input.equals("reverse_all") || input.equals("reverse_chars") || input.equals("rev_all"))
				{
					if(s.length == 2)
					{
						String loc = cd(def_localization, argue, false, false, false, FileTypes.FILE);
						if(!loc.isBlank())
						{
							String fileContent = Shell.superGetFile(loc);
							StringBuilder sb = new StringBuilder(fileContent);
							sb = sb.reverse();
							System.out.println(sb.toString());
							System.out.println();
						}
					}
				}
				else if(input.equals("reverse_words") || input.equals("rev_wrds"))
				{
					if(s.length == 2)
					{
						String loc = cd(def_localization, argue, false, false, false, FileTypes.FILE);
						if(!loc.isBlank())
						{
							String fileContent = Shell.superGetFile(loc);
							String words[] = fileContent.split("\\s+");
							for(int i=words.length-1; i>=0; i--)
							{
								System.out.print(words[i] + " ");
							}
						}
						
						
					}
				}
				else if(input.equals("rev") || input.equals("reverse_lines") || input.equals("reverse"))
				{
					if(s.length == 2)
					{
						String loc = cd(def_localization, argue, false, false, false, FileTypes.FILE);
						if(!loc.isBlank())
						{
							String fileContent = Shell.superGetFile(loc);
							String words[] = fileContent.split("\\n+");
							System.out.println();
							for(int i=words.length-1; i>=0; i--)
							{
								System.out.println(words[i]);
							}
							System.out.println();
						}
						
					}
				}
				else if(input.compareTo("bye") == 0 || input.compareTo("bye_call") == 0)
				{
					
				}
				else if(input.equals("exec") || input.equals("execute") || input.equals("script"))
				{
					String loc = cd(def_localization,argue,false,false,false,FileTypes.FILE);
					if(!loc.isBlank())
					{
						executing_from = loc;
						isExecuting = true;
						isFirstExecution = true;
					}
					else
					{
						ConsoleUtills.printError("Choosen script file is not accesible");
					}
					
				}
				else if(input.compareTo("system_type") == 0)
				{
					System.out.println(isRunningOnWindows ? "Windows type system" : "UNIX like system");
				}
				else if(input.compareTo("make") == 0 || input.compareTo("create") == 0)
				{
					if(s.length <= 2)
					{
						createIfNeededFileCd(def_localization, argue, false);	
					}
					else
					{
						superGenerate(s);
					}
					
				}
				/*else if(input.compareTo("touch") == 0 || input.compareTo("access") == 0)
				{
					if(s.length <= 2)
					{
						createIfNeededFileCd(def_localization, argue, false);	
					}
					else
					{
						superGenerate(s);
					}
					
				}*/
				else if(input.compareTo("read_file") == 0 || input.compareTo("read_content") == 0  || input.compareTo("file_read") == 0  || input.compareTo("cat") == 0 || input.compareTo("read") == 0)
				{
					if(s.length >= 2)
					{						
						try
						{	
							String encoding = "UTF-8";
							if(s.length >= 3)
							{
								encoding = s[2];
							}				
							String[] numbered = {"-n","/n","--numbered"};
							String[] whitechars = {"-swc","/swc","--white-chars","--show-white-chars"};
							String loc = cd(def_localization, s[1], false, false, false,FileTypes.FILE);	
							Shell.readFile(loc,encoding,Shell.isInTable(numbered, s,2),Shell.isInTable(whitechars, s,2));
						}	
						catch(UnsupportedEncodingException | IllegalCharsetNameException e)
						{
							ConsoleUtills.printError("Wrong charset specified");
						}
						catch(IOException e)
						{
							ConsoleUtills.printError("File not exist or is not accesible");
						}
						
					}
					else
					{
						ConsoleUtills.printError("Too few arguments");
					}
					
				}
				else if(input.compareTo("out") == 0 || input.compareTo("set_out") == 0)
				{
					String loc = cd(def_localization, argue, true, false, false, FileTypes.BOTH);
					try
					{
						printToFile(loc);
					}
					catch(IOException e)
					{
						ConsoleUtills.printError("File not exist or is not accesible");
					}
					
					
				}
				else if(input.compareTo("write") == 0 || input.compareTo("write_to_file") == 0 || input.compareTo("write_file") == 0)
				{
					if(s.length >= 3)
					{	
						String encoding = "UTF-8";
						try
						{	
							
							if(s.length >= 4)
							{
								encoding = s[3].isBlank() ? encoding : s[3];
							}		
							String[] richSigns = {"-r","/r","--rich_format","-rtf","/rtf"};
							if(Shell.isInTable(richSigns, s, 3))
							{
								s[2] = Shell.replaceCharacters(s[2]);
							}
							String loc = cd(def_localization, s[1], true, false, false,FileTypes.FILE);	
							Shell.writeFile(loc, s[2], encoding, false);
						
						}	
						catch(UnsupportedEncodingException | IllegalCharsetNameException | UnsupportedCharsetException e)
						{
							ConsoleUtills.printError("Wrong or not support charset specified: " + encoding);
						}
						catch(IOException e)
						{
							ConsoleUtills.printError("File not exist or is not accesible");
						}
					}
					else
					{
						ConsoleUtills.printError("Too few arguments");
					}
					
				}
				else if(input.equals("append_lines") || input.equals("append_line") || input.equals("append_lines_to_file"))
				{
					if(s.length >= 2)
					{
						String loc = createIfNeededFileCd(def_localization, argue, false);
						String[] richSigns = {"-r","/r","--rich_format","-rtf","/rtf"};
						String encoding = "UTF-8";
						boolean replace_flag = false;
						if(s.length >= 3)
						{
							encoding = s[2];
						}
						if(s.length >= 4)
						{
							if(Shell.isInTable(richSigns, s, 2))
							{
								replace_flag = true;
							}
						}
						if(!loc.isEmpty())
						{
							ConsoleUtills.clearScreen(isRunningOnWindows);
							System.out.println("Type !end or !exit for end editing");
							String line;
							try
							{	
								Shell.writeFile(loc, "\n", encoding, true);
							}	
							catch(UnsupportedEncodingException | IllegalCharsetNameException e)
							{
								ConsoleUtills.printError("Wrong charset specified");
								continue;
							}
							catch(IOException e)
							{
								ConsoleUtills.printError("File not exist or is not accesible");
								continue;
							}
							System.out.println(Shell.horizontalSeparator("="));
							while(true)
							{
								System.out.print(">>> ");
								line = br.readLine();
								if(line.equals("!end") || line.equals("!exit"))
								{
									break;
								}
								if(replace_flag)
								{
									line = Shell.replaceCharacters(line);
								}
								try
								{	
									Shell.writeFile(loc, line+"\n", encoding, true);
								}	
								catch(UnsupportedEncodingException | IllegalCharsetNameException e)
								{
									ConsoleUtills.printError("Wrong charset specified");
									break;
								}
								catch(IOException e)
								{
									ConsoleUtills.printError("File not exist or is not accesible");
									break;
								}
								
							}
						}
						else
						{
							ConsoleUtills.printError("File not exist or is not accesible");
						}
						
						
					}
					else
					{
						ConsoleUtills.printError("To few arguments");
					}
					
					
					
				}
				else if(input.compareTo("shortcut") == 0 || input.compareTo("link") == 0 || input.compareTo("ln") == 0)
				{
					if(s.length >= 3)
					{
						String loc = cd(def_localization, s[1], true, false, false, FileTypes.BOTH);
						String dest_loc = cd(def_localization, s[2], true, false, false, FileTypes.BOTH);
						FileSystemProvider fs = FileSystems.getDefault().provider();
						File file = new File(dest_loc);
						if(!file.exists())
						{
							ConsoleUtills.printError("Target file not exist");
						}
						Path path = Paths.get(loc);
						Path dest_path = Paths.get(dest_loc);
						try
						{
							fs.createSymbolicLink(path, dest_path);
						}
						catch(IOException e)
						{
							ConsoleUtills.printError("In creating shortcut");
						}
						
						
					}
					else
					{
						if(!argue.endsWith(".lnk") && isRunningOnWindows)
						{
							argue = argue + ".lnk";
						}
						String loc = cd(def_localization, argue, false, false, true, FileTypes.FILE);
						FileSystemProvider fs = FileSystems.getDefault().provider();
						File file = new File(loc);
						Path path = Paths.get(loc);
						if(file.exists())
						{
							try
							{
								System.out.println(fs.readSymbolicLink(path));
							}
							catch(NotLinkException e)
							{
								ConsoleUtills.printError("Given file is not a symoblic link");
							}
							
						}
					}
					
				
					
				}
				else if(input.compareTo("rms_server") == 0 || input.compareTo("rmc_server") == 0 || input.compareTo("remonte_menagment_service_server") == 0)
				{
					Pair<PrintStream, InputStream> pairr = Processes.commandServer(accessPassword, -1, echo);
					System.setOut(pairr.fst);
					System.setIn(pairr.snd);
					br = new BufferedReader(new InputStreamReader(pairr.snd));			
					customIo = true;
				}
				else if(input.compareTo("rms_client") == 0 || input.compareTo("rmc_client") == 0 || input.compareTo("rms_menager") == 0 || input.compareTo("rmc_menager") == 0 || input.compareTo("remonte_menagment_service_client") == 0)
				{
					try {
						Processes.commandClient(accessPassword,argue, -1, echo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}				
				}
				else if(input.compareTo("bench_cpu") == 0 || input.compareTo("cpu_bench") == 0 || input.compareTo("benchmark_cpu") == 0 || input.compareTo("cpu_benchmark") == 0 )
				{		
					CPUTest.makeTest(argue);
				}
				else if(input.compareTo("append") == 0 || input.compareTo("append_to_file") == 0 || input.compareTo("append_file") == 0)
				{
					if(s.length >= 3)
					{						
						try
						{	
							String encoding = "UTF-8";
							if(s.length >= 4)
							{
								encoding = s[3];
							}				
							String[] richSigns = {"-r","/r","--rich_format","-rtf","/rtf"};
							if(Shell.isInTable(richSigns, s, 3))
							{
								s[2] = Shell.replaceCharacters(s[2]);
							}
							String loc = cd(def_localization, s[1], true, false, false,FileTypes.BOTH);	
							try
							{	
								Shell.writeFile(loc, s[2], encoding, true);
							}	
							catch(UnsupportedEncodingException | IllegalCharsetNameException e)
							{
								ConsoleUtills.printError("Wrong charset specified");
							}
							catch(IOException e)
							{
								ConsoleUtills.printError("File not exist or is not accesible");
							}
							
						}	
						catch(UnsupportedEncodingException | IllegalCharsetNameException e)
						{
							ConsoleUtills.printError("Wrong charset specified");
						}
						catch(IOException e)
						{
							ConsoleUtills.printError("File not exist or is not accesible");
						}
					}
					else
					{
						ConsoleUtills.printError("Too few arguments");
					}
					
				}
				else if(input.compareTo("move") == 0 || input.compareTo("mv") == 0)
				{
					moveOrCopy(s, false, false);	
				}
				else if(input.compareTo("force_move") == 0 || input.compareTo("force_mv") == 0)
				{
					moveOrCopy(s, false, true);	
				}
				else if(input.compareTo("copy") == 0 || input.compareTo("cp") == 0)
				{
					moveOrCopy(s, true, false);
					
				}
				else if(input.compareTo("force_copy") == 0 || input.compareTo("force_cp") == 0)
				{
					moveOrCopy(s, true, true);
					
				}
				else if(input.compareTo("cd") == 0 || input.compareTo("change_directory") == 0)
				{
					String tmp = cd(def_localization, argue,false,false,false,FileTypes.DIR);
					if(!tmp.equals(""))
					{
						def_localization = tmp;
					}
				}
				else if(input.compareTo("cd_default") == 0)
				{
					def_localization =  sys_localization;
					//System.out.println(def_localization);
				}
				else if(input.compareTo("md") == 0 || input.compareTo("mkdir") == 0)
				{
					String localization = cd(def_localization, argue,true,false,false,FileTypes.BOTH);
					if(!localization.isBlank())
					{
						File file = new File(localization);
						if(!file.mkdirs())
						{
							ConsoleUtills.printError("In creating folder " + localization);
						}
					}
					else
					{
						ConsoleUtills.printError("Wrong path to create folder");
					}
					
					
					
				}
				else if(input.compareTo("mds") == 0 || input.compareTo("mkdirs") == 0 || input.compareTo("mktree") == 0 || input.compareTo("mdtree") == 0)
				{
					Shell.makeTree(new File(def_localization), isRichANSITerminal);
				}
				else if(input.compareTo("root_dir") == 0 || input.compareTo("root_dirs") == 0 || input.compareTo("dir_partitions") == 0 || input.compareTo("roots") == 0 || input.compareTo("list_roots") == 0 || input.compareTo("roots_list") == 0)
				{
					System.out.println();
					for(File i : File.listRoots())
					{
						System.out.println(i);
					}
					System.out.println();
				}
				else if(input.compareTo("stress_cpu") == 0 || input.compareTo("stres_cpu") == 0 || input.compareTo("cpu_stres") == 0 || input.compareTo("cpu_stress") == 0 || input.compareTo("load_cpu") == 0 || input.compareTo("test_cpu") == 0)
				{
					int a = Runtime.getRuntime().availableProcessors();
					CPULoader loader[] = new CPULoader[a];
					for(int i=0; i<a; i++)
					{
						loader[i] = new CPULoader();	
						loader[i].start();
					}
					System.out.println("=================================================");
					System.out.println("Press ENTER for end testing (to chillout the CPU)");
					System.in.read();
					for(int i=0; i<a; i++)
					{
						loader[i].interrupt();
					}
					
				}
				else if(input.compareTo("yes") == 0)
				{
					String text = "y";
					if(!argue.isBlank())
					{
						text = argue;
					}
					
					while(true)
					{
						System.out.println(text);
					}
					
				}
				else if(input.compareTo("dir") == 0 || input.compareTo("ls") == 0)
				{
					File file = new File(def_localization);
					System.out.println();
					//Formatter fmt = new Formatter();
					int maxi = 0;
					try
					{
						for(File fl: file.listFiles())
						{
							maxi = Math.max(maxi, fl.getName().length());
						}
					}
					catch(NullPointerException e)
					{
						ConsoleUtills.printError("Error with listing files");
						continue;
					}
					
					DateFormat formatter = new SimpleDateFormat("YYYY:MM:dd HH:mm:ss");
					formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
					File files[] = file.listFiles();
					if(maxi > 64) maxi = 32;
					for(File fl: files)
					{
						Date date = new Date(fl.lastModified());
						//fmt.format("%" + maxi + "s %s %s %s", fl.getName(),(fl.isDirectory() ? " <DIR> " : "       "), formatter.format(date), (!fl.canExecute() ? "<NOT ALLOWED> " : "              "));
						System.out.printf("%" + maxi + "s %s %s %s %n", fl.getName(),(fl.isDirectory() ? " <DIR> " : "       "), formatter.format(date), (!fl.canExecute() ? "<NO ACCESS> " : "              "));
					}
					if(files.length == 0)
					{
						System.out.println("---Directory is empty---");
					}
					System.out.println();
					
				}
				else if(input.compareTo("attrib_dir") == 0)
				{
					File file = new File(def_localization);
					System.out.println();
					int maxi = 0;
					for(File fl: file.listFiles())
					{
						maxi = Math.max(maxi, fl.getName().length());
					}
					DateFormat formatter = new SimpleDateFormat("YYYY:MM:dd HH:mm:ss");
					File files[] = file.listFiles();
					if(maxi > 60) maxi = 32;
					for(File fl: files)
					{
						Date date = new Date(fl.lastModified());
						formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
						//System.out.println(fl.getName() + " " + (fl.isDirectory() ? " <DIR> " : "") + " "+ formatter.format(date) + (fl.canExecute() ? " A" : "") + (fl.canRead() ? " R" : "") + (fl.canWrite() ? " W" : "")+ (fl.isHidden() ? " H" : ""));
						//System.out.printf("%" + maxi + "s %s %s %s %n",fl.getName() , (fl.isDirectory() ? " <DIR> " : "") , "       ", formatter.format(date) , (fl.canExecute() ? " A" : "") , (fl.canRead() ? " R" : "") , (fl.canWrite() ? " W" : ""), (fl.isHidden() ? " H" : ""));
						System.out.printf("%" + maxi + "s %s %s %s %s %s %s %n",fl.getName(),(fl.isDirectory() ? " <DIR> " : "       "), formatter.format(date), (!fl.canExecute() ? "<NO ACCESS> " : ""), (fl.canWrite() ? "<WRITABLE>" : ""),(fl.canWrite() ? "<READABLE>" : ""), (fl.isHidden() ? "<HIDDEN>" : ""));
						
					}
					System.out.println();
					
				}
				else if(input.compareTo("attrib") == 0)
				{
					if(argue.compareTo("") == 0)
					{
						ConsoleUtills.printError("Nie podano pliku do pokazania argumentów");
					}
					else
					{
						File fl = new File(def_localization + argue);
						System.out.println();
						
						if(fl.isFile())
						{
							Date date = new Date(fl.lastModified());
							DateFormat formatter = new SimpleDateFormat("YYYY:MM:DD HH:mm:ss");
							formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
							System.out.println(fl.getName() + " " + (fl.isDirectory() ? " <DIR> " : "") + "Zmodyfikowano: "+ formatter.format(date) + (fl.canRead() ? " R " : "") + (fl.canWrite() ? " W " : ""));
						}
						else
						{
							ConsoleUtills.printError("Plik " + argue + " nie istenieje");
						}
					}
					
					
				}
				else if(input.compareTo("echo") == 0)
				{
					try
					{
						input = s[1];
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						input = "-";
					}
					if(input.compareTo("on") == 0)
					{
						echo = true;
						System.out.println("Włączono echo");
					}
					else if(input.compareTo("off") == 0)
					{
						echo = false;
						System.out.println("Wyłączono echo");
					}
					else
					{
						System.out.println("Additional info printing (echo) " + (echo ? "enabled" : "disabled"));
					}
				}
				else if(input.equals("test_list"))
				{
					ConsoleMultiList<String> cl = new ConsoleMultiList<String>();
					cl.addOption("WoW");
					cl.addOption("Zajebioza");
					cl.addOption("Pomarańcza");
					cl.addOption("Clonezilla to gówno");
					cl.addOption("Kurwa");
					cl.addOption("Uwu");
					var choose = cl.showMultiple();
					if(choose != null)
					{
						for(var i : choose)
						{
							System.out.println(i.snd + " at " + i.fst);
						}
					}
					
				
				}
				else if(input.equals("test_dialog"))
				{
					new ConsoleSimpleSplash("O KURwa").show();
					
				}
				else if(input.equals("test_keyboard"))
				{
					try {
						ConsoleUtills.testKeyboard();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else if(input.equals("generate") || input.equals("gen") || input.equals("garbage"))
				{
					superGenerate(s);
				}
				else if(input.compareTo("net") == 0)
				{
					Processes.Net();
				}
				else if(input.compareTo("files") == 0 || input.compareTo("disks") == 0 || input.compareTo("disk_info") == 0 ||  input.compareTo("file_system") == 0 || input.compareTo("files_info") == 0)
				{
					Shell.diskInfo();
				}
				else if((input.compareTo("clear") == 0 || input.compareTo("cls") == 0) || input.compareTo("clean") == 0)
				{
					ConsoleUtills.clearScreen(isRunningOnWindows);
				}
				else if(input.equals("unicode_table") || input.equals("test_unicode"))
				{
					for(char i = 0; i<Character.MAX_VALUE; i++)
					{
						System.out.print(i);
						System.out.flush();
					}
				}
				else if(input.equals("beep") || input.equals("bell"))
				{
					System.out.print("\u0007");
				}
				else if(input.equals("def_shell") || input.equals("get_shell") || input.equals("default_shell") || input.equals("get_default_shell") || input.equals("get_def_shell"))
				{
					System.out.println(utills.getDefaultShell());
				}
				else if(input.equals("printenv") || input.equals("print_env") || input.equals("get_env") || input.equals("get_system_env") || input.equals("system_env"))
				{
					 System.out.println();
					 Map<String, String> env = System.getenv();
				     for (String envName : env.keySet()) 
				     {
				            System.out.println(envName + " = " + env.get(envName));
				     }
				     System.out.println();
				}
				else if(input.compareTo("crs") == 0 && isDebug)
				{
					SetCoursor(4, 1);
					System.out.print("TEST");
					
				}
				else if(input.compareTo("test_ansi") == 0 && isDebug)
				{
					final String ANSI_CYAN = "\u001B[36m";
					System.out.print(ANSI_CYAN + "Niebieskość\n");
				}
				else if(input.compareTo("java") == 0)
				{
					Runtime.getRuntime();
					System.out.println("Java version: " + Runtime.version().feature());
					System.out.println("Java build: " + Runtime.version().build().get());
					
				}
				else if(input.equals("call"))
				{
					if(s.length >= 2)
					{
						String loc = cd(def_localization, s[1],false,false,true,FileTypes.FILE);
						Shell.callExec(s, loc,true);
					}	
				}
				else if(input.compareTo("run") == 0)
				{
					simpleRun(echo, argue, sys_localization);
					//Shell.run(input, argue, def_localization, echo, sys_localization);
				}
				else
				{
					ConsoleUtills.printError("Wrong command");
				}
			}	
			else if(input.compareTo("") == 0)
			{
				continue;
			}
			else if(input.startsWith("&"))
			{			
				if(s.length >= 2)
				{
					String str  = input.substring(1);
					String loc = cd(def_localization, str,false,false,true,FileTypes.FILE);
					Shell.callExec(s, loc,false);
				}
			}
			else if(input.startsWith("#"))
			{
				argue = alline.substring(1);
				//Shell.run(input, argue, def_localization, echo, sys_localization);
				simpleRun(echo, argue, sys_localization);
			}	
			else if(input.startsWith("="))
			{
				argue = alline.substring(1);
				//Shell.run(input, argue, def_localization, echo, sys_localization);
				System.out.println(Paraser.simpleParase(argue));
			}	
			else if(input.startsWith("$"))
			{
				if(alline.substring(1).isBlank())
				{
					Shell.system(isRunningOnWindows);
				}
				try
				{
					Shell.executeSystemCommand(alline, isRunningOnWindows);
				}
				catch(IOException e)
				{
					ConsoleUtills.printError("Error with starting system command");
				}
			}
			else
			{
				String localization = cd(def_localization, input, false, false, true, FileTypes.FILE);
				try {				
					if(localization != "")
					{
						p = Runtime.getRuntime().exec(localization);
						try
						{
							localProcesses.push(p.pid(), p);			
						}
						catch(NullPointerException e)
						{
							ConsoleUtills.printError("Null error");
						}
						//String str = "j";
						if(echo)
						{
							System.out.println(p.info());
							System.out.println(p.pid());
						}
					}
					/*else
					{
						ConsoleUtills.printError("File not exist: " + localization);
					}*/
						
				} 
				catch (IOException e) 
				{
					ConsoleUtills.printError("Error with starting: " + localization);
				}
			}
			System.gc();
			
			 
		
		}
		
	}
	static void StartWatcher(Process p)
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//localProcess.put(p.pid(), p);
				while(true)
				{
					if(!p.isAlive())
					{
						System.out.println();
						System.out.println(p + " " + "Was finished !!!! with code " + p.exitValue());
						//localProcess.remove(p.pid());
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
	static void SetCoursor(int x, int y)
	{
		char escCode = 0x1B;
		int row = y; int column = x;
		System.out.print(String.format("%c[%d;%df",escCode,row,column));
		
	}
	static String createIfNeededFileCd(String def_localization, String loc, boolean pointToExecFile) throws IOException
	{
		String localization = cd(def_localization, loc, true, false, pointToExecFile, Main.FileTypes.FILE);
		File file = new File(loc);
		if(file.exists() && file.isFile())
		{
			return localization;
		}
		else
		{
			File makefile = new File(localization);
			try
			{
				if(makefile.createNewFile())
				{
					return makefile.getCanonicalPath();
				}
				else
				{
					ConsoleUtills.printError("In creating file");
				}
			}
			catch(SecurityException e)
			{
				ConsoleUtills.printError("Choosen file is not accesible ,check your permisions");	
			}
			catch(IOException e)
			{
				ConsoleUtills.printError("In creating file");
			}
			return "";
		}
	}
	
	static String cd(String def_localization, String loc,boolean force,boolean ignore_last, boolean pointToExecFile, FileTypes type) throws IOException
	{
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
	static String[] trimLastInPath(String argue)
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
	static String createMnemonic(String s)
	{
		String str = "";
		for(int i = 0; i<s.length(); i++)
		{
			str = str.concat(" ");
		}
		return str;
	}
	
	static String compilePath(String def_loc, String path) throws InvalidPathException
	{
		//return FileSystems.getPath(path).normalize().toAbsolutePath().toString();
		return Paths.get(def_loc + path).normalize().toAbsolutePath().toString();
	}
	static String getPathUp(String path_name)
	{
		Path path = Paths.get(path_name);
		return path.getParent().toString();
		
	}
	static void superDownload(String[] s, String argue, boolean isAsync) throws IOException
	{
		if(!argue.isBlank() && s.length >= 3)
		{
			//String loc = cd(def_localization, s[2], false, true, false,FileTypes.BOTH);
			//String filee = trimLastInPath(s[2])[0];
			String loc = engine.cdWithMake(def_localization,s[2], FileTypes.FILE);
			if(!loc.isBlank())
			{
				try
				{
					Shell.downloadFile(new URL(s[1]), loc, isAsync);
				}
				catch(MalformedURLException e)
				{
					ConsoleUtills.printError("You have to specify protocol in URL");
				}
			}
		}
		else
		{
			ConsoleUtills.printError("Too few arguments");
		}
	}
	static void superGenerate(String[] s) throws IOException
	{
		String content = "X";
		if(s.length >= 3)
		{
			String loc = createIfNeededFileCd(def_localization, s[2],false);
			BigInteger size = Shell.bigMemory(s[1],true);
			if(!loc.isBlank() && size != null)
			{
				Thread generator = new Thread(new Runnable() {
					
					@Override
					public void run() 
					{
						try 
						{
							Shell.generateFile(loc, size, content,"");
						} 
						catch (IOException e) 
						{
							// TODO Auto-generated catch block
							ConsoleUtills.printError("In generating file");
							return;
						}
						
					}
				});
				generator.start();
			}
			else
			{
				ConsoleUtills.printError("Wrong file localization or file size");
			}
		}
		else
		{
			ConsoleUtills.printError("To few arguments");
		}
	}
	static void moveOrCopy(String[] s, boolean copy, boolean force) throws IOException
	{
		if(s.length >= 3)
		{
			String localization1 = cd(def_localization, s[1],true,false,false,FileTypes.BOTH);
			String localization2 = cd(def_localization, s[2],true,false,false,FileTypes.BOTH);
			if(new File(localization1).exists())
			{
				Shell.moveOrCopy(localization1, localization2, force, copy);
			}
			else
			{
				
				try
				{
					new URL(s[1]);
					superDownload(s, "X", false);
				}
				catch(MalformedURLException e)
				{
					ConsoleUtills.printError("We can't find given element for " + (copy ? "copy" : "move"));
				}
			}
			
		}
		else
		{
			ConsoleUtills.printError("Too few arguments");
		}
	}
	public static void turnOffPrinting()
	{
		System.setOut(new PrintStream(PrintStream.nullOutputStream()));
		System.setErr(new PrintStream(PrintStream.nullOutputStream()));
	}
	public static void turnOnPrinting()
	{
		System.setOut(System.out);
		System.setErr(System.err);
	}
	public static void printToFile(String fileName) throws IOException
	{
		System.setOut(new PrintStream(fileName));
		//System.setErr(new PrintStream(fileName));
	}
	
	public static void simpleRun(boolean echo,String argue,String def_localization) throws IOException
	{
		String localization = cd(def_localization, argue, false, false, true, FileTypes.FILE);
		Process p;
		try {				
			if(localization != "")
			{
				p = Runtime.getRuntime().exec(localization);
				
				try
				{
					localProcesses.push(p.pid(), p);			
				}
				catch(NullPointerException e)
				{
					ConsoleUtills.printError("Null error");
				}
				//String str = "j";
				Shell.runShell(p, echo);
			
			/*else
			{
				ConsoleUtills.printError("File not exist: " + localization);
			}*/
			}
				
		} catch (IOException e) {
			ConsoleUtills.printError("Error with starting: " + localization);
		}
	}
}
