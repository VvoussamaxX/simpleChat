
import java.util.Scanner;
import common.*;


public class ServerConsole implements ChatIF 
{

	EchoServer server;
	Scanner fromConsole = new Scanner(System.in); 

	final public static int DEFAULT_PORT = 5555;
	
	public ServerConsole(int port){
		server = new EchoServer(port,this);
	}
	
	
	public void accept(){
		try{
		
			String message;

			while (true){
				message  = fromConsole.nextLine();
				server.handleMessageFromServerUI(message);
			}
		} 
		catch (Exception ex) 
		{
			System.out.println
			("Unexpected error while reading from console!");
		}
	}
	public void display(String message){
		System.out.println(">" + message);
    }
  

	 public static void main(String[] args) 
	 {
		 int port = 0; 
	
		 if(args.length==1) {
			 if(args[0]!=null)
		  port = Integer.parseInt(args[0]);} 
		 else { port = DEFAULT_PORT; }
		 
		 
		
		 ServerConsole consoleserveur = new ServerConsole(port);
	    
		 try 
		 {
			 consoleserveur.server.listen(); 
		 } 
		 catch (Exception ex) 
		 {
			 System.out.println(ex);
		 }
		 consoleserveur.accept();
	  }
}