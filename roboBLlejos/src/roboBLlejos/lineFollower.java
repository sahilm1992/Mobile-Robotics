package roboBLlejos;


//import obstacleHandler.obstacleHandler;

//import compassHandler.compassHandler;
import lejos.nxt.BasicMotorPort;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.RConsole;


public class lineFollower  {

public static MotorPort leftMotor =null; 		// MOTOR A 
public static MotorPort rightMotor =null; 		// MOTOR B
static int initPower = 54;
int lowPower = 50;
final int highPower = 80;
//range 50 to 90 at the time of demo

int haveInit =0; 
public static  int curLPower = initPower;
public static int curRPower = initPower;
float speedFactor = (float) 3.2;
float MaxSpeedFactor=speedFactor;
float MinSpeedFactor=speedFactor;
int prevVal=0;
int isFirstItr=1;
int midLight=0;
float speedIncrFactor=0;

float membershipLow=(float) 5; //5 
float membershipHigh=(float) 5; //5

public static int stopMe =0 ; 

public void setupMotor(MotorPort motor , int power)
{	
	  motor.controlMotor(power, BasicMotorPort.STOP);
}
		

public void stopLineFollower(){
	RConsole.println("\n-------------------SOMEBODY STOPPED LINE FOLLOWER------------------");
	this.stopMe=1;
	this.leftMotor.controlMotor(0, BasicMotorPort.STOP);
	this.rightMotor.controlMotor(0, BasicMotorPort.STOP);
}

public void initMotors()
{
	 leftMotor = MotorPort.A;
	 rightMotor = MotorPort.B;
}
		
public void moveMotorForward(MotorPort motor, int power)
{
	motor.controlMotor(power, BasicMotorPort.FORWARD);
}
	  
public void run()
{	
	//stopMe = 0;
	RConsole.print("\nINSIDE RUN LINE FOLLOWER");
	try{
				if(haveInit==0)
				{ 
					  initMotors();
					  setupMotor(leftMotor, initPower);
					  setupMotor(rightMotor, initPower);
					  haveInit=1;
					  moveMotorForward(leftMotor,curLPower);
					  moveMotorForward(rightMotor,curRPower);
					  speedFactor = (float)(highPower-lowPower)/(float)(sensorHelp.highLight-sensorHelp.lowLight);
					  MaxSpeedFactor = (float)(highPower-lowPower)/(float)(sensorHelp.highLight-sensorHelp.lowLight);
					  MinSpeedFactor=(float)0.9;
					  midLight = sensorHelp.highLight-sensorHelp.lowLight;
					  speedIncrFactor = (float)(MaxSpeedFactor-MinSpeedFactor)/(float)(midLight-sensorHelp.lowLight);
			  }
				while(!Button.ENTER.isDown())
				{
					 if(stopMe==1)
					  {

						//	RConsole.print("STOPPING LINE FOLLOWER");
						  	startProgram.da.leftMotor.controlMotor(0, BasicMotorPort.STOP);
							startProgram.da.rightMotor.controlMotor(0, BasicMotorPort.STOP);
							return;
					  }
				
					 else 
						 interruptMe();
					
				}
	 }
	catch(Exception e)
	{
				
				
	}

}
  

  public void interruptMe() 
  {	
	  //RConsole.print("\n----------------INTERRUPT-----------------");

	  int avgLightSensor= sensorHelp.getLightSensorReading();
	//  int avgColorSensor = sensorHelp.getColorSensorReading();
	
	  {
			  
			  int modifiedAvgLightSensorForLeft = -1; //left wheel
			  int modifiedAvgLightSensorForRight = -1; //right wheel
			  //New logic
			  if(isFirstItr == 1)
			  {
				  prevVal = avgLightSensor;
				  isFirstItr=0;
			  }
			  else
			  {
				  //speedFactor = (float)(highPower-lowPower)/(float)(sensorHelp.highLight-sensorHelp.lowLight);
				  
				  //fuzzy it is : border cases
				  
				  if(avgLightSensor >= sensorHelp.highLight-membershipHigh ) //right turn
				  {
					  //we wnt this range to be wide. But not by changing membershipHigh
					  modifiedAvgLightSensorForRight = (int) (sensorHelp.highLight +  (membershipHigh*4)); //4 is good
					  
					  //modifiedAvgLightSensorForLeft = (int) (sensorHelp.lowLight - (membershipHigh*1.5)); //for 1.5 it works fine 
					  modifiedAvgLightSensorForLeft = (int) (sensorHelp.highLight -membershipHigh); //this is good
					  if(modifiedAvgLightSensorForLeft < 0)
						  modifiedAvgLightSensorForLeft = 0;
		  			  speedFactor-=MinSpeedFactor+(speedIncrFactor*2);
					  
				  }
				  
				  else  if(avgLightSensor <= sensorHelp.lowLight+membershipLow )     //left turn 
				  {
					  modifiedAvgLightSensorForLeft = (int) (sensorHelp.lowLight - (membershipLow*4));
					  if(modifiedAvgLightSensorForLeft < 0)
						  modifiedAvgLightSensorForLeft = 0;
					  modifiedAvgLightSensorForRight= (int) (sensorHelp.lowLight + membershipLow);
					  speedFactor-=MinSpeedFactor+(speedIncrFactor*2);
				  }
				  
				  else //otherwise
				  {
					  lowPower=45;
					  //set new speed factor
					  if(avgLightSensor < midLight)	
					  {
						  if(avgLightSensor > prevVal) //increase speed factor
						  {
							  speedFactor += speedIncrFactor *Math.abs(midLight-avgLightSensor);
						  }
						  else //decrease speed factor
						  {
							  speedFactor -= speedIncrFactor * Math.abs(midLight-avgLightSensor);
						  }
					  }
					  else	//midLIght < avgLightSensor 
					  {
						  if(avgLightSensor < prevVal) //increase speed factor
						  {
							  speedFactor += speedIncrFactor * Math.abs(midLight-avgLightSensor);
						  }
						  else //decrease speed factor
						  {
							  speedFactor -= speedIncrFactor * Math.abs(midLight-avgLightSensor);
						  }
					  }
				  }//end else
				  prevVal=avgLightSensor;
			  }//end else
			  
			  if(speedFactor > MaxSpeedFactor)
			  {
				  speedFactor = MaxSpeedFactor;
			  }
			  else if(speedFactor < MinSpeedFactor)
			  {
				  speedFactor = MinSpeedFactor;
			  }
			  
			  float diffForL=0;
			  if(modifiedAvgLightSensorForLeft != -1)
			  {
				  diffForL = modifiedAvgLightSensorForLeft-sensorHelp.lowLight;  
			  }
			  else
			  {
				  diffForL = avgLightSensor-sensorHelp.lowLight;
			  }
			   
			  float diffForR = 0;
			  
			  if(modifiedAvgLightSensorForRight != -1)
			  {
				  diffForR = sensorHelp.highLight-modifiedAvgLightSensorForRight ;  
			  }
			  else
			  {
				  diffForR = sensorHelp.highLight-avgLightSensor;  
			  }
			  
			  curLPower =(int)((float)(diffForL) * speedFactor);
			  if(curLPower >= 0)
				  curLPower += lowPower;
			  else
				  curLPower -= lowPower;
		
			  curRPower =(int)((float)(diffForR) * speedFactor);
			  if(curRPower >= 0)
				  curRPower += lowPower;
			  else
				  curRPower -= lowPower;
			  
			  String output = avgLightSensor + "-" + curLPower +"-"+ curRPower;
			 LCD.drawString(output, 2, 2);
		      LCD.refresh();
		      moveMotorForward(leftMotor, curLPower);
			  moveMotorForward(rightMotor, curRPower);
	  }
  }
}
