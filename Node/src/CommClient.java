
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommClient {
	
	public static String commServerIP= "10.150.36.39";
	public static int commServerPort= 1111;
	static Socket commSocket = null;
	public static void initCommwithServer()
	{
		System.out.println(" HELLO CITY HOW ARE YOU !!!!! \nComm With City");
		PrintWriter print=null;
		BufferedReader brinput=null;
		try {
			commSocket= new Socket(commServerIP,commServerPort);
			
			print = new PrintWriter(commSocket.getOutputStream(), true);
		//	System.out.println("Enter City name : ");
			//brinput = new BufferedReader(new InputStreamReader(System.in));
			String msgtoServerString=null;
			msgtoServerString = startProgram.myCityId;    //brinput.readLine().trim().replace("\n", "");
			//startProgram.myCityId = msgtoServerString;
			print.println(msgtoServerString);
			print.flush();
			
			//CommSendThread sendThread = new CommSendThread(sock);
			//Thread thread = new Thread(sendThread);thread.start();
			RecieveThread recieveThread = new RecieveThread(commSocket);
			Thread thread2 =new Thread(recieveThread);thread2.start();
			startProgram.commInit = 1;
			
		} catch (Exception e) {System.out.println(e.getMessage());} 
	}
	
	public static void sendToServer(String message)
	{
		
		PrintWriter print=null;
		BufferedReader brinput=null;
		
		try{
			if(commSocket.isConnected())
			{
				System.out.println("Client connected to "+commSocket.getInetAddress() + " on port "+commSocket.getPort());
				print = new PrintWriter(commSocket.getOutputStream(), true);	
				
	//			brinput = new BufferedReader(new InputStreamReader(System.in));
				String msgtoServerString=null;
				msgtoServerString =message;
				print.println(msgtoServerString);
				print.flush();
				System.out.println(" MESSAGE SENT-------> "+message);

			
			}
			
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
}
