package roboBLlejos;
import lejos.nxt.BasicMotorPort;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;
public class junction {
	public final int left =0 ;
	public final int right =1;
	int addPowConst=9;	
	
	int sleepTimer = 350; //in milliseconds
	int juncColor = Color.RED;
	public void takeRightTurn() throws InterruptedException{
		 lineFollower follow = new lineFollower();
		 follow.initMotors();
		 RConsole.println("TAKING RIGHT");
			
		 follow.leftMotor.controlMotor(follow.lowPower+addPowConst,BasicMotorPort.FORWARD);
		 follow.rightMotor.controlMotor(follow.lowPower+addPowConst,BasicMotorPort.FORWARD);
		 Thread.sleep(sleepTimer);
		 RConsole.print(" TAKING RIGHT NOW , HAVE SLEPT ENOUGH");
		 compassHandler compassHandlerObject = new compassHandler();
		 
		 compassHandlerObject.changeDirection(right);
	}
	
	public void takeLeftTurn()throws InterruptedException{
		 lineFollower follow = new lineFollower();
		 RConsole.println("TAKING LEFT");
		 follow.initMotors();
		 follow.leftMotor.controlMotor(follow.lowPower+addPowConst,BasicMotorPort.FORWARD);
		 follow.rightMotor.controlMotor(follow.lowPower+addPowConst,BasicMotorPort.FORWARD);
		 Thread.sleep(sleepTimer);
		 RConsole.println("TAKING LEFT , SLEPT ENOUGH NOW");
			
		 compassHandler compassHandlerObject = new compassHandler();
		 
		 compassHandlerObject.changeDirection(left);
		
		 }
	
	public void fwdTillColor(int color){
	//	RConsole.print("FOLLOWING" );
		lineFollower follow = new lineFollower();
	//	RConsole.println(" FOLLOW TILL COLOR ");
		
		if(color == Color.BLACK)
		{
			int lightValue =sensorHelp.getLightSensorReading();
			RConsole.print("\n LIGHT Reading "+String.valueOf(lightValue));
			LCD.drawString(String.valueOf(lightValue),1,1);
			
			follow.initMotors();
			while(lightValue >= sensorHelp.lowLight+4){
				RConsole.print("\n LIGHT Reading "+String.valueOf(lightValue));
				RConsole.println("\n ----------------FOLLOWING TILL COLOR :------------"+ color);
				follow.moveMotorForward(follow.leftMotor, follow.lowPower+addPowConst);
				follow.moveMotorForward(follow.rightMotor, follow.lowPower+addPowConst);
				lightValue =sensorHelp.getLightSensorReading();
			}
		}
		else{
				int value =sensorHelp.getColorSensorReading();
				RConsole.print("\n Reading "+String.valueOf(value));
				LCD.drawString(String.valueOf(value),1,1);
				follow.initMotors();
				while(value != color){
					RConsole.println("\n ----------------FOLLOWING TILL COLOR :------------"+ color);
					follow.moveMotorForward(follow.leftMotor, follow.lowPower+8);
					follow.moveMotorForward(follow.rightMotor, follow.lowPower+8);
					value =sensorHelp.getColorSensorReading();
				}
				//stop motors
			}
		follow.leftMotor.controlMotor(0, BasicMotorPort.STOP);
		follow.rightMotor.controlMotor(0, BasicMotorPort.STOP);
		RConsole.println("FOLLOW TILL COLOR "+color + " COMPLETED");
		
	}
	
	public void handleJunction(String actionString, int nodeColor){
		char action= actionString.charAt(0);
		//LCD.drawString("TAKING ACTION",1,1);

		switch (action){
		case 'S':
			//forward till black line
			fwdTillColor(juncColor);
			
			fwdTillColor(Color.BLACK);
			break;
		case 'U':
			fwdTillColor(juncColor);
			//right turn
			try {
				takeRightTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fwdTillColor(nodeColor);

			fwdTillColor(juncColor);
			//right turn
			try {
				takeRightTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//forward till black line
			fwdTillColor(Color.BLACK);
			break;
		case 'L':
			
			RConsole.println("TOLD TO GO LEFT ");
			fwdTillColor(juncColor);
			
			
			//left turn
			try {
				takeLeftTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//forward till black line
			fwdTillColor(Color.BLACK);
			break;
		case 'R':
			RConsole.println("TOLD TO GO RIGHT ");

			fwdTillColor(juncColor);  
			fwdTillColor(nodeColor);
			fwdTillColor(juncColor);
			//right turn
			try {
				takeRightTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fwdTillColor(Color.BLACK);
			break;
		}
		RConsole.print("\nDONE JUNCTION HANDLING --------------------");

	}
	
}
