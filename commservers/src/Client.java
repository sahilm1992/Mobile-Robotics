

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	
	public static void main(String[] args)
	{
		PrintWriter print=null;
		BufferedReader brinput=null;
		try {
			Socket sock = new Socket("192.168.1.131",1111);
			
			print = new PrintWriter(sock.getOutputStream(), true);
			System.out.println("Enter name : ");
			brinput = new BufferedReader(new InputStreamReader(System.in));
			String msgtoServerString=null;
			msgtoServerString = brinput.readLine();
			print.println(msgtoServerString);
			print.flush();
			
			SendThread sendThread = new SendThread(sock);
			Thread thread = new Thread(sendThread);thread.start();
			RecieveThread recieveThread = new RecieveThread(sock);
			Thread thread2 =new Thread(recieveThread);thread2.start();
			
		} catch (Exception e) {System.out.println(e.getMessage());} 
	}
}
