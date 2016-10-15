
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

class RecieveThread implements Runnable
{
	Socket sock=null;
	BufferedReader recieve=null;
	
	public RecieveThread(Socket sock) {
		this.sock = sock;
	}//end constructor
	public void run() {
		System.out.println(" start receiving");
		try{
		recieve = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));//get inputstream
		String msgRecieved = null;
		while(true)
		{
			msgRecieved = recieve.readLine();
			System.out.println("From Server: " + msgRecieved);
			cityProcessing.processMessage(msgRecieved);
			
			//			System.out.println("Please enter something to send to server..");
		}
		}catch(Exception e){System.out.println(e.getMessage());}
	}//end run
}//end class recievethread
