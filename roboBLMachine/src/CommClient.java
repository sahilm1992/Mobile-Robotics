import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommClient {
	
	public static String commServerIP= "192.168.1.129";
	public static int commServerPort= 1111;
	static Socket commSocket = null;
	public static void initCommwithServer()
	{
		System.out.println("Comm With City");
		PrintWriter print=null;
		BufferedReader brinput=null;
		try {
			commSocket= new Socket(commServerIP,commServerPort);
			
			print = new PrintWriter(commSocket.getOutputStream(), true);
			System.out.println("Enter name : ");
			brinput = new BufferedReader(new InputStreamReader(System.in));
			String msgtoServerString=null;
			msgtoServerString = brinput.readLine();
			print.println(msgtoServerString);
			print.flush();
			
			//CommSendThread sendThread = new CommSendThread(sock);
			//Thread thread = new Thread(sendThread);thread.start();
			RecieveThread recieveThread = new RecieveThread(commSocket);
			Thread thread2 =new Thread(recieveThread);
			thread2.start();
			startProgram.commInit = 1;
			
			//String action = new BTMachine().whatToDoNext("E");
			//System.out.println("Action is : " + action);
			
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
				System.out.println(" MESSAGE SENT-------> "+message);
				print.println(msgtoServerString);
				print.flush();
			
			}
			
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
}
