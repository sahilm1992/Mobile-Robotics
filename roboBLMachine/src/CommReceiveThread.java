import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class RecieveThread implements Runnable
{
	Socket sock=null;
	BufferedReader recieve=null;
	
	public RecieveThread(Socket sock) {
		this.sock = sock;
	}//end constructor
	
	public void run() {
		
		while(true)
			try {
				
				PrintWriter print=null;
				
				recieve = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));//get inputstream
				String msgRecieved = null;
				while((msgRecieved = recieve.readLine())!= null) {
					
					System.out.println("From Server: " + msgRecieved);
					
					//Has got the response back from the process message
					String responceMsg = RobotProcessing.processMessage(msgRecieved);
					
					//Now send response back to requesting node.
					if (null != responceMsg) {
						
						if(sock.isConnected()) {
							
							print = new PrintWriter(sock.getOutputStream(), true);	
							
							String msgtoServerString = responceMsg;
							System.out.println(" MESSAGE SENT-------> " + msgtoServerString);
							print.println(msgtoServerString);//send the message to the server
							print.flush();
						} else {
							System.out.println(" Socket is closed... ");
						}
					} else {
							System.out.println("Error not a valid response.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
	}//end run
}//end class recievethread
