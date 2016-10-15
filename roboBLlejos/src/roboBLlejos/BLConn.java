package roboBLlejos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;

public class BLConn extends Thread {
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private BTConnection btc = null;
	private int BLCOMMTIME = 800; //1 sec
	int testMessageCounter =0 ; 
	public BLConn(){
		
	}
	
	
	
	public void acceptConnection(){
		String connected = "Connected";
        String waiting = "Waiting...";
		LCD.drawString(waiting,0,0);
		LCD.refresh();
	    btc = Bluetooth.waitForConnection();
		LCD.clear();
		LCD.drawString(connected,0,0);
		LCD.refresh();	
		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
		startProgram.ISBLUETOOTHOK=1;
	}
	
	public void closeConnection() throws InterruptedException{
		String closing = "Closing...";
		Thread.sleep(100); // wait for data to drain
		try {
			dis.close();
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//EvoRobot.logServer.putData(e.getMessage());
		}
		LCD.clear();
		LCD.drawString(closing,0,0);
		LCD.refresh();
		btc.close();
		LCD.clear();
	}
	
	public DataInputStream getInputDataStream(){
		return this.dis;
	}
	
	public DataOutputStream getOutDataStream(){
		return this.dos;
	}
	
	public String getData() {
		try{
			return dis.readUTF();
		}catch(IOException e){
			
			//EvoRobot.logServer.putData(e.getMessage());
			return null;
		}
	}
	
	public synchronized void putData(String str){
		try{
			dos.writeUTF(str);
			dos.flush();
		}catch(Exception e){
			e.printStackTrace();
			//EvoRobot.logServer.putData(e.getMessage());
		}
	}
	public static String NA="NA";
	/*public static String STOPLINEFOLLOWER="STOPLINEFOLLOWER";
	public static String STARTLINEFOLLOWER="STARTLINEFOLLOWER";
	public static String PARKROBOT="PARKROBOT";
	public static String STARTLINEFOLLOWER_DIR="STARTLINEFOLLOWER_DIR";
	public static String COLORREADING = "COLORREADING";
	public static String NEXTACTION = "NEXTACTION";
*/
	//   ACTIONS  L R S U NA
	
	/*public static String[] processReachedArray(String[] Array)
	{	
		String roboId = Array[1];
		
		String []GoalsArray = Array[2].split(",");
		
		return GoalsArray;
		
	}
	*/

	public static void processMessageFromServer(String message)
	{
		
		String action = message;
		if(action.equals(NA)){
			return;
		}
		else if(action.equals("L") ||action.equals("R") ||action.equals("S") ||action.equals("U")){
			
		 compassHandler compassHandlerObject = new compassHandler();
		 compassHandlerObject.alignRobot();
		 junction junc1 = new junction();
		 junc1.handleJunction(action,startProgram.colorSensorThreadObject.bigBoxColor);
		 RConsole.println("\n Resuming Line Follower ");
		 
		 startProgram.da.stopMe = 0;
		 startProgram.colorSensorThreadObject.stopColorSensor();
		}
		/*int indexOfColon= message.indexOf(":");
		//RConsole.print(message);
		//Button.waitForAnyPress();
		LCD.drawString(message, 0, 1);
		
		if (indexOfColon==-1)
		{
			
			
		}
		else
		{
			
			String myheader = message.substring(0,indexOfColon);
			String remMessage = message.substring(indexOfColon+1);
			remMessage = remMessage.replace('\n', ' ').trim();
		//	RConsole.print(" inside pmfromserver ");
			//Button.waitForAnyPress();
			LCD.drawString("rem"+remMessage, 0, 3);
			
			//System.out.println("My Header" + myheader);
			if(myheader.equals(NA))
			{
				System.out.print("NO ACTION");
				//process test header
			}
			else if(myheader.equals(STARTLINEFOLLOWER))
			{
				//System.out.println("Header->" + myheader +  "   "  +remMessage);
				
				//String goalOfRobot=message.substring(indexOfColon+1);
				//process reached header
				//LCD.drawString("GOAL"+ goalOfRobot, 0, 2);
				//int goalCompleted = Integer.parseInt(goalOfRobot);
			}
			else if(myheader.equals(STOPLINEFOLLOWER))
			{
				//System.out.println("Header->" + myheader +  "   "  +remMessage);
			
			}
			else if(myheader.equals(PARKROBOT))
			{
				//System.out.println("Header->" + myheader +  "   "  +remMessage);
			
			}
			else if(myheader.equals(NEXTACTION))
			{
				String action = remMessage;
				compassHandler compassHandlerObject = new compassHandler();
				 compassHandlerObject.alignRobot();
				 junction junc1 = new junction();
				 junc1.handleJunction(action,startProgram.colorSensorThreadObject.bigBoxColor);
				 RConsole.println("\n Resuming Line Follower ");
				 
				 startProgram.da.stopMe = 0;
				 startProgram.colorSensorThreadObject.stopColorSensor();
				 
			}
			{
				//System.out.println("Header->" + myheader +  "   "  +remMessage);
			
			}
			
		}*/
	}

	
	
	
	public void keepAlive() 
	{
		
		while (true)
		{
			try{
			Thread.sleep(BLCOMMTIME);
			}
			catch(Exception e){
				
			}
			String ReceivedDataFromServer = this.getData();			
			processMessageFromServer(ReceivedDataFromServer);
			this.putData(NA + ":NA");
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		acceptConnection();
		this.setPriority(Thread.MIN_PRIORITY+4);
		keepAlive();
	}
	
}

