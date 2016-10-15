import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Server {

	public static Map<String, Socket> mapNameSocket = new HashMap<String, Socket>();

	public static void main(String[] args) throws IOException {
		
		final int port = 1111;
		System.out.println("Server waiting for connection on port "+port);
		ServerSocket ss = new ServerSocket(port);
		
		int clientCount = 0; 
		while (clientCount < 3) {
			clientCount++;
			Socket clientSocket = ss.accept();
			
			
			//receive name
			BufferedReader brBufferedReader = null;
			brBufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));		
			
			String messageString;
	
			while((messageString = brBufferedReader.readLine())!= null){//assign message from client to messageString
	
				System.out.println("From Client: " + messageString);//print the message from client
				System.out.println("Name received ..");
				break;
			}
			
			//put the socket into the name
			mapNameSocket.put(messageString, clientSocket);
			
			System.out.println("Recieved connection from "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
			//create two threads to send and recieve from client
			RecieveFromClientThread recieve = new RecieveFromClientThread(clientSocket);
			Thread thread = new Thread(recieve);
			thread.start();
	
			/*SendToClientThread send = new SendToClientThread(clientSocket);
			Thread thread2 = new Thread(send);
			thread2.start();*/
		}
	}
}
