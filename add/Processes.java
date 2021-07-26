package add;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Processes 
{
	public static BufferedReader br;
	public static void Io_subshell(Process p)
	{
		
	}
	
	public static Pair<PrintStream, InputStream> commandServer(String password, int rawport, boolean echo) throws IOException
	{
		final int DEFAULT_PORT = 555;	
		final int VERSION = 1;
		final String APP_TRANSFER_KEY = "60710"; 
		
		int port = rawport > 0 ? rawport : DEFAULT_PORT;
	    Socket clientSocket;
	    PrintStream out;
	    InputStream in;
	    ServerSocket serverSocket = new ServerSocket(port);
	    		System.out.println("Starting remonte menagment service (RMS) on port " + port);
	    		System.out.print("Server is waiting for client to connect");
		    	clientSocket = serverSocket.accept();
		    	out = new PrintStream(clientSocket.getOutputStream(), true);
		        in = clientSocket.getInputStream();
		        BufferedReader inn = new BufferedReader(new InputStreamReader(in));
		    	System.out.println("\rHost connected");
		    	//READING HEADER FOR CLIENT
		    	String header[] = inn.readLine().split("\\u0002+");
		    	//AUTHORIZING 
		    	//TODO: Encryption password
		    	//HEADER: RMS[APP_TRANSFER_KEY]\u0002[PASSWORD]\u0002[VERSION]\u0002[APP_TRANSFER_KEY]RMS
		    	if(header.length >= 4)
		    	{
		    		if(header[0].equals("RMS"+APP_TRANSFER_KEY) && header[3].equals(APP_TRANSFER_KEY+"RMS"))
		    		{
		    			if(echo)
		    			{
		    				System.out.println("Handshake OK");
		    			}
		    			if(password.equals(header[1]))
				    	{
				    		out.println("AUTH_OK");
				    		if(echo)
				    		{
				    			System.out.println("Client auth OK");
				    		}
				    		try
				    		{
				    			if(Integer.valueOf(header[2]) == VERSION)
					    		{
					    			//APP BODY
					    			return new Pair<PrintStream, InputStream>(out, in);
					    		}
					    		else
					    		{
					    			if(echo)
					    			{
					    				System.err.println("ERROR: App versions are not the same HALTED!");
					    			}
					    			
					    		}
				    		}
				    		catch(NumberFormatException e)
				    		{
				    			if(echo)
				    			{
				    				System.err.println("ERROR: Wrong app version HALTED!");
				    			}
				    		}
				    		
				    	}
			    		else
			    		{
			    			out.println("AUTH_WRONG");
			    			if(echo)
				    		{
				    			System.out.println("ERROR: Client auth WRONG credentials HALTED!");
				    			System.err.println("ERROR: Client auth WRONG credentials HALTED!");
				    		}
			    			
			    		}
		    		}
		    		else
		    		{
		    			if(echo)
		    			{
		    				System.err.println("ERROR: Client header transfer key error HALTED!");
		    			}
		    		}
		    	}
		    	else   		
		    	{
		    		if(echo)
		    		{
		    			System.err.println("ERROR: Client header too short");
		    		}
		    		
		    	}
		    	if(!echo)
		    	{
		    		System.err.println("ERROR: App connection error application halted");
		    	}    	
		    	return null;
		    	

	    	
	    	
	}
	public static Pair<PrintStream, BufferedReader> commandClient(String password, String address, int rawport, boolean echo) throws IOException
	{
		final int DEFAULT_PORT = 555;	
		final int VERSION = 1;
		final String APP_TRANSFER_KEY = "60710"; 
		
		int port = rawport > 0 ? rawport : DEFAULT_PORT;
	    PrintStream out;
	    BufferedReader in;
	    try( Socket clientSocket = new Socket(address,port))
	    {
	    		System.out.println("Connected to remonte menagment service (RMS) on port " + port);
		    	out = new PrintStream(clientSocket.getOutputStream(), true);
		        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    	//WRITING HEADER FOR SERVER
		    	out.println("RMS"+APP_TRANSFER_KEY + "\u0002" + password + "\u0002" + VERSION + "\u0002" + APP_TRANSFER_KEY + "RMS");
		    	//AUTHORIZING 
		    	//TODO: Encryption password
		    	//HEADER: RMS[APP_TRANSFER_KEY]\u0002[PASSWORD]\u0002[VERSION]\u0002[APP_TRANSFER_KEY]RMS
		    	String authLine = in.readLine();
		    	if(authLine.equals("AUTH_OK"))
		    	{
		    		if(echo)
		    		{
		    			System.out.println("Server auth OK");
		    		}
		    		try {
						Shell.rmsClient( new Pair<PrintStream, BufferedReader>(out,in));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    	else if(authLine.equals("AUTH_WRONG"))
		    	{
		    		if(echo)
		    		{
		    			System.err.println("ERROR: Server auth WRONG credentials");
		    		}
		    		
		    	}
		    	
	    }
	    return null;
	    	
	}
	public static void ChatApp(String dest, int rawport, boolean server) throws IOException
	{
		
		final int DEFAULT_PORT = 556;	
		final int VERSION = 1;
		final String APP_TRANSFER_KEY = "6071"; 
		
		int port = rawport > 0 ? rawport : DEFAULT_PORT;
	    Socket clientSocket;
	    PrintWriter out;
	    BufferedReader in;
	    if(server)
	    {
	    	try(ServerSocket serverSocket = new ServerSocket(port))
	    	{
	    		System.out.print("Starting chatapp server waiting for host to connect on port: " + port);
		    	clientSocket = serverSocket.accept();
		    	out = new PrintWriter(clientSocket.getOutputStream(), true);
		        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    	System.out.println("\rHost connected");
		    	Shell.subChatApp(br, in, out, clientSocket,VERSION,APP_TRANSFER_KEY);
		    	 System.out.println("Your connection with client has been ended, thanks for using Chat App");
	    	}
	    	
	    	
	    }
	    else
	    {
	    	System.out.println("I'm trying to connect to chatapp server: "+ dest + ":" + port);
	    	try
	    	{
	    		clientSocket = new Socket(InetAddress.getByName(dest),port);
	    		out = new PrintWriter(clientSocket.getOutputStream(), true);
		        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		        System.out.println("Connected to server");
		        Shell.subChatApp(br, in, out, clientSocket,VERSION,APP_TRANSFER_KEY);
		        System.out.println("Your chat has been ended, thanks for using Chat App");
	    	}
	    	catch(IOException e)
	    	{
	    		System.err.println("Error with connection to server");
	    	}
	    	
	        
	        
	    }
	}
	public static void MathSubshell(String argue) throws IOException
	{
		ConsoleUtills.clearScreen();
		System.out.println("Core of math subshell was made by Mariusz Gromada and published under BSD license");
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));		
		Paraser paraser = new Paraser();
		boolean virginFlag = true, scientific=false, cut=false; 	//PL - Virgin flag sprawdza czy jesteœmy pierwszy raz wywo³ywani w pêtli subshella
		String input;
		while(true)
		{						
			if(virginFlag && !argue.isBlank())
			{
				input = argue;
				virginFlag = false;
			}
			else
			{
				System.out.print("math> ");	
				input =  br.readLine();			
			}
			input = input.toLowerCase();
			if(input.compareTo("exit") == 0 || input.compareTo("end") == 0 || input.compareTo("bye") == 0)
			{
				return;
			}
			else if(input.startsWith("define"))
			{
				input = input.substring("define".length());
				paraser.define(input);
			}
			else if(input.startsWith("def"))
			{
				input = input.substring("def".length());
				paraser.define(input);
			}
			else if(input.startsWith("display_mode") || input.startsWith("display") || input.startsWith("mode") || input.startsWith("disp_mode"))
			{
				if(!input.isBlank())
				{
					String[] in = input.split("\\s+");
					in[0] = "";
					for(String splitedinput : in)
					{
						switch (splitedinput)
						{
							case "scientific":
								scientific = true;
							break;
							case "long":
								scientific = false;
							break;
							case "normal":
								scientific = false;
								cut = false;
							break;
							case "cut":
								cut = true;
							break;
							case "decimal":
								cut = true;
							break;
							case "float":
								cut = false;
							break;
							case "notcut":
								cut = false;
							break;
							case "":
							break;
							default:
								System.err.println("Wrong number display mode");
							break;
							
								
						}
					}
				}
				else
				{
					System.err.println("Error: no given number display mode");
				}
				
					
				
			}
			else if(input.contains("=") && !input.startsWith("="))
			{
				paraser.defineWithEquals(input);
			}
			else
			{
				if(input.startsWith("="))
				{
					input = input.substring(1);
				}
				double out = paraser.parase(input);
				
				if(scientific && cut)
				{	
					System.out.println(Math.round(out));					
				}
				else if(scientific && !cut)
				{
					System.out.println(out);	
				}
				else if(!scientific && cut)
				{
					DecimalFormat df = new DecimalFormat("0");
					df.setMaximumFractionDigits(340);
					System.out.println((df.format(Math.round(out)))); 
				}
				else
				{
					DecimalFormat df = new DecimalFormat("0");
					df.setMaximumFractionDigits(340);
					System.out.println(df.format(out)); 
				}
				
			}
		}
	}

	public static void Net() throws IOException
	{
		while(true)
		{
			//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			NetworkInterface defaultNetworkInterface = null;
			System.out.print("net> ");
			String input =  br.readLine();
			String[] s = Shell.arrayToString(Shell.tokenizeInput(input));
			String argue = "";
			for(String str : s)
			{
				
				argue = argue.concat(str);
				argue = argue.concat(" ");
				
			}
			input = s[0];
			if(input.compareTo("exit") == 0)
			{
				return;
			}
			else if(input.compareTo("check") == 0 || input.compareTo("!check") == 0)
			{
				String str;
				if(s.length >= 2)
				{
					str = s[1];
				}
				else
				{
					System.out.print("Host address: ");
					str = br.readLine();
				}
				InetAddress address = null;
				try
				{
					
					address = InetAddress.getByName(str);	
				}
				catch (UnknownHostException e) 
				{
					System.out.println("Host is not accesible !");
					continue;
				}
				System.out.println("Net settings, type 'config', 'c' or anything else like 'enter' for default settings " );
				input = br.readLine();
				int ttl = 0,timeout = 0;
				boolean isDefault = input.compareTo("config") == 0 ||input.compareTo("c") == 0;
				//System.out.println(input);
				if(isDefault)
				{
					boolean errflag = true;
					while(errflag)
					{
						System.out.print("Wprowadz ttl: ");
						try
						{
							ttl = Integer.parseInt(br.readLine());
							errflag = false;
						}
						catch (NumberFormatException n) 
						{
							System.err.println("Parametr ttl nie jest liczb¹ !");
							errflag = true;
						}
					}
					errflag = true;
					while(errflag)
					{
						System.out.print("Wprowadz timeout w ms: ");
						try
						{
							timeout = Integer.parseInt(br.readLine());
							errflag = false;
						}
						catch (NumberFormatException n) 
						{
							System.err.println("Parametr timeout nie jest liczb¹ !");
							errflag = true;
						}
					}
				}
				else
				{
					ttl = 128;
					timeout = 5000;
					System.out.println();
					System.out.println("Default ttl:" + ttl + " timeout:" + timeout/1000 + "sec");
				}
				
		
				System.out.print("Name of host is: " + address.getHostName() + " at " + address.getHostAddress() + "\n");		
				if(address.isReachable(timeout))
				{
					System.out.println("Host is accessible");
				}
				else if(isDefault)
				{
					System.out.println("Host is not accesible !!!");
				}
				else 
				{
					System.out.println("Host is not accesible in given timeout and ttl !!!");
				}
				
				//Address.isReachable(N, ttl, timeout)
			 	 /*for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
			 	 {
			 		 NetworkInterface ni = e.nextElement();
			 		 if(ni.isUp())
			 		 {
			 			 System.out.println(ni.getDisplayName());
			 		 }
			 	 }*/
			 	 //HttpClient client = HttpClient.newHttpClient();
			 	 //HttpRequest httpRequest = (HttpRequest) HttpRequest.newBuilder(new URI("http://java.sun.com/j2se/1.3/"));
			 	 //client.send(httpRequest, null);
				
			}
			else if(input.compareTo("interface") == 0 ||input.compareTo("!interface") == 0 || input.compareTo("int") == 0 || input.compareTo("!int") == 0 || input.compareTo("default_interface") == 0 || input.compareTo("!default_interface") == 0)
			{
				ConsoleList<NetworkInterface> cl = new ConsoleList<>();
				
					for (Iterator<NetworkInterface> i = NetworkInterface.getNetworkInterfaces().asIterator(); NetworkInterface.getNetworkInterfaces().hasMoreElements();)
				 	{
						
						try
						{
							 NetworkInterface ni = i.next();
							 if(ni.isUp())
					 		 {
								 cl.addOption(ni);
					 		 }
						}
				 		 catch(NoSuchElementException e)
						{
				 			 break;
						}
				 		 
				 	}
					
					try 
					{
						NetworkInterface ni = cl.show("Choose network interface with arrows or WASD and press ENTER").snd;
						if(ni != null)
						{
							defaultNetworkInterface = ni;
						}
						
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				

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
						System.err.println("Error Wrong port number");
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
						System.err.println("Error Wrong port number");
					}
				}
			}
			else if(input.compareTo("interfaces") == 0 ||input.compareTo("!interfaces") == 0 || input.compareTo("ints") == 0 || input.compareTo("!ints") == 0)
			{
				for (Iterator<NetworkInterface> i = NetworkInterface.getNetworkInterfaces().asIterator(); NetworkInterface.getNetworkInterfaces().hasMoreElements();)
			 	 {
					
					try
					{
						 NetworkInterface ni = i.next();
						 if(ni.isUp())
				 		 {
				 			 System.out.println(ni.getDisplayName());
				 		 }
					}
			 		 catch(NoSuchElementException e)
					{
			 			 break;
					}
			 		 
			 	 }
			}
			else
			{
				System.err.println("Non recognize net subshell command");
			}
		}
		
	}
	
}
