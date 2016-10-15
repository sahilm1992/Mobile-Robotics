import java.io.*;
import java.net.*;
import java.lang.*;


class RecieveFromClientThread implements Runnable
{
	Socket clientSocket=null;
	BufferedReader brBufferedReader = null;
	PrintWriter pwPrintWriter;
	public RecieveFromClientThread(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}//end constructor
	public void run() {
		try{
		brBufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));		
		
		String messageString;
		while(true){
		while((messageString = brBufferedReader.readLine())!= null){//assign message from client to messageString
			if(messageString.equals("EXIT"))
			{
				break;//break to close socket if EXIT
			}
			System.out.println("From Client: " + messageString);//print the message from client
			String data[] = messageString.split("@");
			Socket mySocket = Server.mapNameSocket.get(data[0]);
			String msg = data[1];
			pwPrintWriter =new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream()));//get outputstream
			pwPrintWriter.println(msg);//send message to client with PrintWriter
			pwPrintWriter.flush();//flush the PrintWriter
			System.out.println("Please enter something to send back to client..");
		}
		this.clientSocket.close();
		System.exit(0);
	}
		
	}
	catch(Exception ex){System.out.println(ex.getMessage());}
	}
}//end class RecieveFromClientThread

