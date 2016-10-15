import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.html.MinimalHTMLWriter;

import org.omg.CORBA.Request;




public class handleRequests {
	
	public static int shouldIwait_for_Robo =0 ; 
	public static String messageFromOtherSystem_fromRobo=null;
	
public static String RoboPositions_PathFolder = startProgram.HomeFolderPath+"camera_Pos\\";
	

	public static void requestNeighbouringCities(){
		HashSet<String> myNeighbours = new HashSet<String>();
		myNeighbours.add("City1");
		myNeighbours.add("City2");
	
		
	}
	
	
	public static HashMap<String,Float> getNearestRobotsId(float curCityXCord, float curCityYCord)
	{		
		updateRobotPositions();
		
		HashMap<String,Float>distRobotHashMap = new HashMap<String, Float>();
		
		float minDist =10000000;
		int minIndex = -1 ;
		for (int i =0 ; i <startProgram.RoboPosition.size();i++)
		{
			
			float roboCoord[] = startProgram.RoboPosition.get(startProgram.RoboNames.get(i));
			float dist =  Dist(roboCoord[0],roboCoord[1], curCityXCord,curCityYCord);
			if(dist <startProgram.robotDistanceThreshold)				
				distRobotHashMap.put(startProgram.RoboNames.get(i), dist);
			
		}
		
		
		return distRobotHashMap;
	}
	
	public static float Dist( float x1, float y1 , float x2, float y2)
	{
		
		float dist = (float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
		
		return dist;
	}
	
	public static void updateRobotPositions()
	{
	
//			demandArray=new int[3];
			ArrayList<String>FileNames = new ArrayList<String>(); 
			
			for ( int  i =0 ; i < startProgram.RoboNames.size();i++)
			{
				String Fname = startProgram.RoboNames.get(i);
				FileNames.add(Fname+".txt");
			}
			
			
			String Path  = RoboPositions_PathFolder;
			//Path = Path.concat(String.valueOf(CityId));
			//	String fin = " ";
			
			for ( int  i =0 ; i < FileNames.size();i++)
			{
				String filePath = Path+FileNames.get(i);
				try {
					FileInputStream fis = new FileInputStream(filePath);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					
					try {
						
						String line = br.readLine();
						while (line!=null)
						{
							
							line = line.replace("\n", "");
							
							String[] lineSplit = line.split(",");
							
							String leftSide = lineSplit[0];
							String rightSide = lineSplit[1];
							float xCoord = Float.parseFloat(leftSide);
							float yCoord = Float.parseFloat(rightSide);
							float coordPoints[]={xCoord,yCoord};
							startProgram.RoboPosition.put(startProgram.RoboNames.get(i),coordPoints);
							line=br.readLine();
						
						}
						

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}
	
	
	public static String handleReqViaRobot(TSPRequest reqObj)
	{
		
		 shouldIwait_for_Robo =0 ; 
		 messageFromOtherSystem_fromRobo=null;
		 
		HashMap<String, Integer> alreadyContactedRobot= new HashMap<String,Integer>();
		for (int i=0 ; i<startProgram.RoboNames.size();i++)
		{
			alreadyContactedRobot.put(startProgram.RoboNames.get(i) , 0);
		}
		
		
		String myCoord = MapClass.getCoordinates(startProgram.myCityId);
		String splitCoord[] = myCoord.split(",");
		int myXCoord = Integer.parseInt(splitCoord[0]);
		int myYCoord = Integer.parseInt(splitCoord[1]);
		HashMap<String,Float>distRobotHashMap =getNearestRobotsId(myXCoord,myYCoord);
		
		Set<String> distRobotsSet = distRobotHashMap.keySet();
		ArrayList<String>distRobotNames  = new ArrayList<String>();
		distRobotNames.addAll(distRobotsSet);
		
		 
		while(true)
			{
			
				int flagSomeoneProcessed =0; 
				float minDist = 1000000;
				String minRobot = null;
				for(int i=0;i<distRobotNames.size();i++)
				{
					if (alreadyContactedRobot.get(distRobotNames.get(i))==0)
					{
						flagSomeoneProcessed=1;
						float dist = distRobotHashMap.get(distRobotNames.get(i));
						//comm with robo
						if(dist<minDist)
						{
							minDist = dist;
							minRobot= distRobotNames.get(i);
						}
						
					}
					else
					{
						// do not process this
					}
				}
				if(minRobot!=null)
					{
					alreadyContactedRobot.put(minRobot,1);	
					String message= minRobot+"@" ;
					message+=StringConst.STR_REQUEST+":" +startProgram.myCityId+":"+ reqObj.goalCityId+"#"+reqObj.demand;
					//message+=StringConst.STR_REQUEST+"#" + reqObj.goalCityId+"#"+reqObj.demand;
					
					cityProcessing.sendMessageOnTelephone(message);
					
					
					while(shouldIwait_for_Robo ==0)
					{	
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					//	System.out.println((" WAITING FOR RESPONSE FROM " + minRobot));
						
					}
					
					if(shouldIwait_for_Robo ==1 && messageFromOtherSystem_fromRobo!=null)
					{
						String [] splitArray= messageFromOtherSystem_fromRobo.split("#");
						String roboId = splitArray[0];
						return roboId;
						
					}
					
					shouldIwait_for_Robo=0;
				
					}
				
				if(flagSomeoneProcessed ==0)
				{
					break;
				}
				
				distRobotHashMap=getNearestRobotsId(myXCoord,myYCoord);

			}
				
				//contact the robot
		
		return null;
		}
		
		
		
	
	public static void handleReqViaNeighbours(TSPRequest reqObj)
	{
		
		Set<String> neighbourCities=MapClass.getNeighbourNodes((startProgram.myCityId));
		ArrayList<String>neighbourArray  = new ArrayList<String>();
		neighbourArray.addAll(neighbourCities);
		
		//send message to them
		
		for (int i =0 ; i < neighbourArray.size();i++)
		{
			String payload = StringConst.STR_REQUEST+":"+startProgram.myCityId+":"+ reqObj.getGoalCityId()+"#"+reqObj.getDemand();
			String messageTo= neighbourArray.get(i);
			String message =messageTo+"@"+ payload;
			cityProcessing.sendMessageOnTelephone(message);
		}
		
		
		
	}
	
	
	public static void handleOwnRequest(TSPRequest reqObj)
	{
		String responseFromRobots=handleReqViaRobot(reqObj); //
			
		if(responseFromRobots!=null)
		{
			String payload = StringConst.STR_SECONDARYACK+":"+startProgram.myCityId+":"+"YES";
			String messageTo = responseFromRobots;
			
			cityProcessing.sendMessageOnTelephone(messageTo+"@"+payload);
			
		}
	
		else
		{
			// talk to neighbourig ctiy\\seek help from neighbours
			
			handleReqViaNeighbours(reqObj);
			
		}
	}
	
	
	public static void handleOthersRequest(TSPRequest reqObj)
	
	{
		
	}
	
	public static void handleRequest(TSPRequest reqObj)
	{
		String goalCityId = reqObj.getGoalCityId();
		
		if(goalCityId.equals(startProgram.myCityId))
		{
			handleOwnRequest(reqObj);
			
			
		}
		else
		{
			// from other city
		}
	}
	
}
