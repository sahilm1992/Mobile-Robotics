package roboBLlejos;

import lejos.nxt.BasicMotorPort;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.DSwitch;
import lejos.nxt.addon.IRSeekerV2.Mode;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;
import lejos.robotics.navigation.DifferentialPilot;

public class compassHandler {

int power = 59;
MotorPort leftMotor =null; 		// MOTOR A 
MotorPort rightMotor =null; 		// MOTOR B
int haveInit =0 ; 

int directionAdd=0;
int dirError=12;

DifferentialPilot pilot=null;

public void setDiffentialRobot()
{
	pilot  = new DifferentialPilot(2.2f, 5.8f, Motor.A, Motor.B, true);  // parameters in inches
	pilot.setRotateSpeed(15);
	
/*	 pilot.setRobotSpeed(30);  // cm per second
	 pilot.travel(50);         // cm
	 pilot.rotate(-90);        // degree clockwise
	 pilot.travel(-50,true);  //  move backward for 50 cm
	 while(pilot.isMoving())Thread.yield();
	 pilot.rotate(-90);
	 pilot.rotateTo(270);
	 pilot.steer(-50,180,true); // turn 180 degrees to the right
	 waitComplete();            // returns when previous method is complete
	 pilot.steer(100);          // turns with left wheel stationary
	 Delay.msDelay(1000;
	 pilot.stop();

*/
	}

public void setupMotor(MotorPort motor , int power)
{
	  motor.controlMotor(power, BasicMotorPort.STOP);
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



// 0 for west and then find path .
// 1 for west and then move on
// 2 for north and find path .

	public int []getRequiredDegAndDir()
	{
		int compReading =(sensorHelp.getCompassReading()+directionAdd)%360;
		
	/*	int NORTH =0 ;
		int SOUTH=180;
		int EAST =90;
		int WEST = 270;
		*/
		
		int Dir[] = new int[4];
		
		for(int i=0;i<4;i++)
		{
			Dir[i]=i*90 +dirError;
			
		}
		
		
		int minDistIndex = -1;
		int minDist =99999;
		int actualMinDist =9999;
		for(int i=0;i<4;i++)
		{

			int dist = compReading - Dir[i];
			RConsole.println("\nDIR  :"+compReading +" : dist  " + dist );

			if(i==0 && dist >315 + dirError)
			{
				minDist = dist-360+dirError;
				minDistIndex = i;
				actualMinDist = minDist;
				
				break;
			}
			int absDist = Math.abs(dist);
			if(Math.abs(dist) < minDist)
			{
				minDist = absDist;
				actualMinDist =dist;
				minDistIndex = i;
				RConsole.println("\nDISTANCE READING :"+minDist +" : " + actualMinDist +" : " + minDistIndex);

			}
			RConsole.println("\nminDist :"+minDist +" :   actual " + actualMinDist +" :      minIndex" + minDistIndex);

		}
		
		int ret[] = {actualMinDist,minDistIndex};
		return ret;
	}

	public void changeDirection(int newDir ) // left->0 or right->1 
	{
		initMotors();
		RConsole.print(" *****COMP CHANGING DIR");
		int currentReading =(sensorHelp.getCompassReading()+directionAdd)%360;

		int Dir[] = new int[4];
		
		for(int i=0;i<4;i++)
		{
			Dir[i]=i*90 +dirError;
			
		}
	
	
		int minDistIndex = -1;
		int minDist =99999;
		int actualMinDist =9999;
		for(int i=0;i<4;i++)
		{

			int dist = currentReading - Dir[i];
			RConsole.println("\n******DIR  :"+currentReading +" : dist  " + dist );

			if(i==0 && dist >315+dirError)
			{
				minDist = dist-360+dirError;
				minDistIndex = i;
				actualMinDist = minDist;
				
				break;
			}
			int absDist = Math.abs(dist);
			if(Math.abs(dist) < minDist)
			{
				minDist = absDist;
				actualMinDist =dist;
				minDistIndex = i;
				RConsole.println("\n****DISTANCE READING :"+minDist +" : " + actualMinDist +" : " + minDistIndex);

			}
			RConsole.println("\n***sminDist :"+minDist +" :   actual " + actualMinDist +" :      minIndex" + minDistIndex);

		}
		
		
		int myDirection = minDistIndex;
		int reqdDirection = newDir==0?(Dir[myDirection]-90):(Dir[myDirection]+90);
		reqdDirection = Math.abs(reqdDirection);
				
		if(newDir==0)
		{
			power=-1*power;	
		}
		
		// Code to change direction by moving motors
		while(true){
			int compReading =(sensorHelp.getCompassReading()+directionAdd)%360;
			RConsole.println("****COMPASS :" + compReading + " Going : "+ reqdDirection );
			if(!(compReading >=reqdDirection-3  && compReading <=reqdDirection+3))
			{
				
					//pilot.rotate();
				
							// move straight
						moveMotorForward(leftMotor, power);
						moveMotorForward(rightMotor, -1*power);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
			else
				{
					moveMotorForward(leftMotor, 0);
					moveMotorForward(rightMotor, 0);
					break;
				}
			
		}
		RConsole.println("\n DIRECTION WORK COMPLETED");
		
		// Turn Right
	 
	}
		
	
	
	public void alignRobot(){
		//setDiffentialRobot();
		initMotors();
		int Dir[] = new int[4];
		for(int i=0;i<4;i++)
		{
			Dir[i]=i*90 +dirError;
			
		}
		int Dist[] = getRequiredDegAndDir();
		Dist[0]=Dist[0];
		RConsole.println("\nDISTANCE " + Dist[0] + " DIRECTION " + Dist[1] );
	//	pilot.rotate(Dist[0]);
		//pilot.quickStop();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		if(Dist[0] > 0){
		power = -1*power;
		}
		
		
		
			while(true){
				int compReading =(sensorHelp.getCompassReading()+directionAdd)%360;
				RConsole.println("COMPASS :" + compReading );
				if(!(compReading >=Dir[Dist[1]]-3  && compReading <=Dir[Dist[1]]+3))
				{
					
						//pilot.rotate();
					
								// move straight
							moveMotorForward(leftMotor, power);
							moveMotorForward(rightMotor, -1*power);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				}
				else
					{
						moveMotorForward(leftMotor, 0);
						moveMotorForward(rightMotor, 0);
						break;
					}
				
			}
			RConsole.println("\n DIRECTION WORK COMPLETED");
			
			// Turn Right
		 
		}
		
		
		
	
	
	public void beginCompass(int type) { // do till we reach west . and then give control back to line follower and wait for black line
	
		haveInit=0;
		

		switch(type)
		{
		
		case 0 : //
			
					
		case 1 :
			while(!Button.ENTER.isDown() )
			{

				if(haveInit==0)
				{ 
					
				  haveInit=1;
				  initMotors();
				  setupMotor(leftMotor, power);
				  setupMotor(rightMotor, power);
				}
				
					int compReading =sensorHelp.getCompassReading();
					int modCompReading = (directionAdd + compReading) %360;
					String compass = String.valueOf(modCompReading) ;
					
					LCD.clear();
					LCD.drawString("Cmp"+compass,1,1);
					LCD.refresh();
			
					if(modCompReading >264  && modCompReading <276)
					{
									// move straight
								moveMotorForward(leftMotor, power);
								moveMotorForward(rightMotor, power);
								try {
									Thread.sleep(850);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						
								if(type==1)			// just go west and forget 
								{
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							
									while(!Button.ENTER.isDown())
									{
										
										int lightSensor = sensorHelp.getLightSensorReading();
										
										if(lightSensor >sensorHelp.medLight)
											continue;
								
										else 
										{
											moveMotorForward(leftMotor, 0);
											moveMotorForward(rightMotor, 0);
											return;
										//	break;
										}
										
									}
									
									
									break;
								}
								
								// for case 0 search 
								
								try {
									Thread.sleep(700);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								while(!Button.ENTER.isDown())
								{
									int brightnesss = sensorHelp.getLightSensorReading();
									
			
									/*
									  Convert this to brightness level
									 */
									while(brightnesss >=sensorHelp.lowLight +13 && !Button.ENTER.isDown())
									{
										
										moveMotorForward(leftMotor,-1*(int) ( (int) power));
										moveMotorForward(rightMotor,(int) ((int) power));
										
										try {
											Thread.sleep(600);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										brightnesss = sensorHelp.getLightSensorReading();
										
										if(brightnesss <=sensorHelp.lowLight +13)
										{
											moveMotorForward(leftMotor,0);
											moveMotorForward(rightMotor,0);
											
												return;
										}
										
										moveMotorForward(leftMotor,1*(int) ( (int) power));
										moveMotorForward(rightMotor,-1*(int) ((int) power));
										try {
											Thread.sleep(1200);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										brightnesss = sensorHelp.getLightSensorReading();
										
										if(brightnesss <=sensorHelp.lowLight +13)
										{
											moveMotorForward(leftMotor,0);
											moveMotorForward(rightMotor,0);
											
												return;
										}
									
									}
									moveMotorForward(leftMotor,0);
									moveMotorForward(rightMotor,0);
							
									return;
								
								}
								
						//west
					}
				
					
					
						else if((modCompReading >=276  && modCompReading <=359) || (modCompReading >=0  && modCompReading <90))
						{
								// left
									moveMotorForward(leftMotor, -1*power +4);
									moveMotorForward(rightMotor, power);
									//west
						}
					
						else if(modCompReading >=90  && modCompReading <=264)
						{
								// right
									moveMotorForward(leftMotor, power);
									moveMotorForward(rightMotor, -1*power +4);
									//west
						}
						
			}
			
			
				// go to west and find
				break;
				
				
				
				
				
		case 2:
			
			
			while(!Button.ENTER.isDown() )
			{

				if(haveInit==0)
				{ 
					
				  haveInit=1;
				  initMotors();
				  setupMotor(leftMotor, power);
				  setupMotor(rightMotor, power);
				}
		
					int compReading =sensorHelp.getCompassReading();
					int modCompReading = (directionAdd+ compReading )%360;
					String compass = String.valueOf(modCompReading) ;
					LCD.clear();
					LCD.drawString("C"+compass,1,1);
					LCD.refresh();
					
					if(modCompReading >354 && modCompReading <=360  || (modCompReading >0 && modCompReading <=5 ))
					{
									// move straight
								moveMotorForward(leftMotor, power);
								moveMotorForward(rightMotor, power);
				
								break;
					}
					
						else
						{
								//move right
									moveMotorForward(leftMotor, power );
									moveMotorForward(rightMotor, -1*power );
								
						}
			}
			
				break;
		}
		
		return ;
	}
}