package roboBLlejos;


import lejos.nxt.BasicMotorPort;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;

public class colorSensorThread  extends Thread{
	public static final int bigBoxColor = Color.BLUE;
	public static int stopColorSensor =0 ; 
	public static String colorHeader;
	static String COLORREADING = "COLORREADING";

	public void run()
	{
		this.setPriority(Thread.MIN_PRIORITY + 4);
		 //TouchSensor touch = new TouchSensor(SensorPort.S3);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	while(!Button.ENTER.isDown())
		{	
    		
    		if(stopColorSensor==1)
    		{
    			continue;
    		}
    		
    		int color =sensorHelp.getColorSensorReading();
    		LCD.drawString("Col"+ String.valueOf(color)+" :",1,1);
			LCD.refresh();
				  
			
			  switch(color)
			  {
		  			
			/* case Color.YELLOW ://startProgram.da.stopLineFollower();
			 					colorHeader = COLORREADING + ":" + String.valueOf(color);
			 					//startProgram.blconn.putData(colorHeader);
			 					//this.stopColorSensor();
			 					break;
				*/ 				
	//		 case Color.GREEN: //Say this is city
				/* this.stopColorSensor();
				 startProgram.da.stopLineFollower();
				 junction
				 junc = new junction();
				 String action = "S";
				 junc.handleJunction(action,Color.GREEN);
				 this.startColorSensor();
				 startProgram.da.run();
				 break;
				*/
			 case bigBoxColor: //Say this is junction
				 		/*RConsole.println("GREEN DETECTED\n");
						startProgram.da.stopLineFollower();
						this.stopColorSensor();
						compassHandler DistAndDir = new compassHandler();
						int[] compReading =DistAndDir.getRequiredDegAndDir();
						String Dir[] = {"N","E","S","W"}; 
						String reqDir = Dir[compReading[1]];
				 		RConsole.println(" sending to server \n");
				*/
						//startProgram.blconn.putData(color + ":" + reqDir );
				 
				 junction junc1 = new junction();
				 String action1 = "L";
				 startProgram.da.stopLineFollower();
				 this.stopColorSensor();
				 compassHandler compassHandlerObject = new compassHandler();
				 compassHandlerObject.alignRobot();
				 
				 junc1.handleJunction(action1,Color.RED);
				 RConsole.println("\n Resuming Line Follower ");
				 
				 startProgram.da.stopMe = 0;
				 this.startColorSensor();
				 
				 
				 break;
				 
				 default :	//LCD.clear();
				 		//	LCD.drawString("C:"+ String.valueOf(color),1,1);
  								//LCD.refresh();
					 		RConsole.print("\n COL DEF "+ String.valueOf(color));
				  			
			  }
			  
    		/*else if(touch.isPressed()) 
    			{	this.stopColorSensor();
    				System.out.print(" PRESSED ");
    				startProgram.blconn.putData(BLConn.COLORREADING +":"  + "YELLOW");
    			
    			}*/
    		
		}
    	
	}
	public void stopColorSensor(){
		this.stopColorSensor = 1;
	}
	
	public void startColorSensor(){
		this.stopColorSensor = 0;
	}
	
}
