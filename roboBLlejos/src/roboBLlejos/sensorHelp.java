package roboBLlejos;
import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.robotics.DirectionFinder;


public class sensorHelp {
	public final static int lowLight=32; // black //30
	public final static int medLight =40; //grey
	public final static int highLight=55; // white //55
	public final static int num_samples_light=1;
	public final static int num_samples_Color=5;
	public final static int num_samples_Usc=1;
	final static int num_samples_Compass=1;
	
	static UltrasonicSensor ultraSonicSensor = null;
	static ColorHTSensor colorHtSensor =null;
	static CompassHTSensor compassSensor =null;
	
	static int error = 7;
	  /*public static  void InitSensor()
	  {
		  uSc = new UltrasonicSensor(SensorPort.S2);

		  uSc.reset();
		  
	  }*/
	//Port 1
	  public static  int getLightSensorReading()
	    {
			int sum =0 ; 
	    
	    	int avgVal =0 ;
	    	LightSensor lS= new LightSensor(SensorPort.S1);
	    	for (int i = 0; i < num_samples_light; ++i) {
				/*try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

				}*/
				int val =lS.readValue();
				sum +=val;

			}
	    	
	    	avgVal = sum / num_samples_light;
	    	
	    	return avgVal;	
	    }
	  
	  //Port 4
	  public static  int getColorSensorReading()
	    {
		  int ArrayColor[] = new int[14];
		  if(colorHtSensor==null)
	    	{	colorHtSensor= new ColorHTSensor(SensorPort.S2);

	    	}
			
			int numSamp =0 ; 
			
			while(numSamp < num_samples_Color)
			{
				
				int colVal= colorHtSensor.getColorID();
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
	
					}
					
					ArrayColor[colVal]++;
					
					numSamp++;
		    	
			}
			int max = 0 ; 
			int index =0 ; 
			for(int i =0 ; i <14;i++)
			{
				if(ArrayColor[i]>max)
				{	max = ArrayColor[i];
					index = i;
				}
				}
			
			return index;
	    }
	  
	  
	  
	  //port 3
	 /* public static int getUltraSonicReading()
	    {
			int sum =0 ; 
	    
	    	int avgVal =0 ;
	    	if(ultraSonicSensor==null)
	    	{	ultraSonicSensor= new UltrasonicSensor(SensorPort.S3);
	    	ultraSonicSensor.continuous();
	    	}
	    	for (int i = 0; i < num_samples_Usc; ++i) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

				}
	    		//if(uSc.off() == 0)
	    		//uSc.off();
	    		//uSc.ping();
				int val =ultraSonicSensor.getDistance();
				sum +=val;

			}
	    	
	    	avgVal = sum / num_samples_Usc;
	    	return avgVal;	
	    }	  */
	  
	  
	  //port 4
	 public static  int getCompassReading()
	    {
			int sum =0 ; 
	    
	    	int avgVal =0 ;
	    	if(compassSensor==null)
	    	{	compassSensor= new CompassHTSensor(SensorPort.S4);
	    	}
	    	
	    	for (int i = 0; i < num_samples_Compass; ++i) {
	    		
	    		int val = (int) compassSensor.getDegrees();
				sum +=val;
	    		
	    	}
	    	avgVal = sum / num_samples_Compass;
	    	return avgVal;	
	    }
		 
	  
}


