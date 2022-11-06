
//This file contains material supporting section 3.7 of the textbook:
//"Object Oriented Software Engineering" and is issued under the open-source
//license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
* This class overrides some of the methods in the abstract 
* superclass in order to give more functionality to the server.
*
* @author Dr Timothy C. Lethbridge
* @author Dr Robert Lagani&egrave;re
* @author Fran&ccedil;o is B&eacute;langer
* @author Paul Holden
* @version July 2000
*/
public class EchoServer extends AbstractServer 
{
//Class variables *************************************************

/**
* The default port to listen on.
*/
final public static int DEFAULT_PORT = 5555;
//Server User Interface
ChatIF serverUI;
//Boolean showing the server state
private boolean server_state;
//Constructors ****************************************************

/**
* Constructs an instance of the echo server.
*
* @param port The port number to connect on.
*/
public EchoServer(int port,ChatIF serverUI) 
{
 super(port);
 this.serverUI = serverUI;
 server_state = true;
}



//Instance methods ************************************************

public EchoServer(int port) {
	  super(port);
	  server_state = true;
}


/**
* This method handles any messages received from the client.
*
* @param msg The message received from the client.
* @param client The connection from which the message originated.
*/
public void handleMessageFromClient(Object msg, ConnectionToClient client)
{
	serverUI.display("Message received: " + msg + " from " + client.getInfo("loginID"));
	if((boolean)client.getInfo("First Login")){
		String loginID = ((String) msg).split(" ")[1];  
		client.setInfo("loginID", loginID);
		client.setInfo("First Login", false);
		serverUI.display(client.getInfo("loginID")+" has logged on.");	
	}
	else {
		if(((String) msg).split(" ")[0].equals("#login")) {
			try {
				client.sendToClient("Already logged in . Closing .");
				client.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}  
	  
 
 this.sendToAllClients((String)client.getInfo("loginID")+ ">"+ msg);
}
 
/**
* This method overrides the one in the superclass.  Called
* when the server starts listening for connections.
*/
protected void serverStarted()
{
 serverUI.display("Server listening for connections on port " + getPort());
 server_state = true;
}

/**
* This method overrides the one in the superclass.  Called
* when the server stops listening for connections.
*/
protected void serverStopped()
{
 serverUI.display("Server has stopped listening for connections.");
 server_state = false;

}
/**
* Hook method called each time a new client connection is
* accepted. The default implementation does nothing.
* @param client the connection connected to the client.
*/
@Override
protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("A new client is attempting to connect to the server.");
	  client.setInfo("First Login", true);
	  

}
synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  clientDisconnected(client);
}


/**
* Hook method called each time a client disconnects.
* The default implementation does nothing. The method
* may be overridden by subclasses but should remains synchronized.
*
* @param client the connection with the client.
*/
@Override
synchronized protected void clientDisconnected( ConnectionToClient client) {
	  
	  serverUI.display((String)client.getInfo("loginID")+ "  has disconnected.");
	  this.sendToAllClients((String)client.getInfo("loginID")+" has disconnected.");
}

public static void main(String[] args) 
{
 int port = 0; //Port to listen on

 try
 {
   port = Integer.parseInt(args[0]); //Get port from command line
 }
 catch(Throwable t)
 {
   port = DEFAULT_PORT; //Set port to 5555
 }
	
 EchoServer sv = new EchoServer(port);
 
 try 
 {
   sv.listen(); //Start listening for connections
 } 
 catch (Exception ex) 
 {
   System.out.println("ERROR - Could not listen for clients!");
 }
}

//Class methods ***************************************************

/**
* This method is responsible for the creation of 
* the server instance (there is no UI in this phase).
*
* @param args[0] The port number to listen on.  Defaults to 5555 
*          if no argument is entered.
*/


public void handleMessageFromServerUI(String message) throws IOException {
	  if(message.charAt(0) == '#'){
			try{
				switch(message.split(" ")[0]) {
				  case "#quit":
					    serverUI.display("Closing Server");
					  	System.exit(0);
					  	break;
					  	
			  	  case "#stop":
					    serverUI.display("No longer listening for new clients");			  		
			  		  	stopListening();
			  		  	break;
			  	  case "#close":
					    serverUI.display("Closing all connections");
			  	  		close();
			  	  		break;
			  	  case "#getport":
			  		  serverUI.display("Port: "+ getPort());
			  		  break;
			  	  case  "#setport":
					if(!server_state) {
				  		setPort(Integer.parseInt(message.split(" ")[1]));
					    serverUI.display("Setting Port to: " +message.split(" ")[1]);
				  	}
					else{
					    serverUI.display("Server running. Close it to set a port");
					}
			  		break;
			  	  case "#start":
			  		if(!isListening()) {
			  			listen();
				  	}
					else{
					    serverUI.display("Server Already listening");
						 }
			  		break;

			  		
			  	  default :
					    serverUI.display("Unvalid Command");			  		  
			  }
			}
			catch(IOException e){
			}
		}
	  else{
		  serverUI.display("SERVER MSG>" + message);
		  sendToAllClients("SERVER MSG>" + message); }
	  }
}
//End of EchoServer class