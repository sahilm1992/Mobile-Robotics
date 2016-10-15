package roboBLlejos;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;


public class startProgram  extends Thread{
	public static Thread detectAlgo = null;
	public static lineFollower da = null;
	public static colorSensorThread colorSensorThreadObject  = null;
	static BLConn blconn =null;
	public static int ISBLUETOOTHOK=0;
	
	public void initCommunication()
	{
		
		
	}
	
	public static void main(String[] args) {
		
		
		// TODO Auto-generated method stub
		blconn=  new BLConn();
		colorSensorThreadObject= new colorSensorThread();
		//blconn.start();
		da = new lineFollower();
	//	LCD.drawString("BLUETOOTH oK", 3, 2);
		/*while(ISBLUETOOTHOK==0)
		{
			
		}*/
		
	//	if(ISBLUETOOTHOK==1)
		{
			LCD.drawString("hello", 0, 2);
			RConsole.openBluetooth(2000000);
			RConsole.print(" HELLO EVERYBODY");
			
		
			// now start 
			/*LCD.drawString("COLOR-SEND", 0, 2);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Button.waitForAnyPress();
			LCD.drawString("COLOR-RECV", 0, 3);
			blConn.sendDummyCOLOR();
			*/
			colorSensorThreadObject.start();
			
			while(!Button.ENTER.isDown())
			  {
				if(da.stopMe == 0)
					{
						da.run();
					}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
			
			
		}
		
	}

}
