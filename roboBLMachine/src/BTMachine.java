import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
//Rob PC 1

public class BTMachine implements Runnable {

	static boolean waitingFlag = true; 
	private static DataInputStream in;
	private static DataOutputStream out;
	private static NXTConnector conn;
	//public static CityComm cityCommObject=null;
	
	@Override
	public void run() {
		
		setupConnection();
		startProgram.bLcommInit = 1;
		int testMessageCount = 500;
		String msg = StringConst.NO_ACTION;
		while (true) {
			
			//String recvText = "init";
			//sendText("TEST:TEST"+ String.valueOf(testMessageCount++));
			sendText(msg);
			try {
				
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			msg = recvText();
			System.out.println("Received to NXT: " + msg);
			
			
			//processMessageFromRobot(recvText);
			
			//get city id from color sensor 
			//ROBOTREACHED:ROBOID:PREVCITYID:GOALS1-GOALS2-GOALS3:
			//Data Receive Here
			
		}
	}

	private static void setupConnection() {
		// TODO Auto-generated method stub
		conn = new NXTConnector();
		conn.addLogListener(new NXTCommLogListener() {
			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: " + message);
			}
			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				throwable.printStackTrace();
			}
		});
		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://newrobo");

		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}

		out = new DataOutputStream(conn.getOutputStream());
		in = new DataInputStream(conn.getInputStream());
		System.out.println("Connected with NXT");		
	}
	
	/*private static void closeConnection() {
		try {  
			in.close();
			out.close();
			conn.close();
		} catch (IOException ioe) {
	   		System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
		System.out.println("Connection Closed\n");
	}
	*/
	public void sendText(String str) {

		try {
			out.writeUTF(str);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static String TESTHEADER=StringConst.NO_ACTION;
	static String JUNCTIONCOLOR = "2";
	static String CITYCOLOR = "1";
	public void processTestHeader()
	{
		
	}
	public static String[] processReachedArray(String[] Array)
	{	
		String roboId = Array[1];
		String []GoalsArray = Array[2].split(",");
		
		return GoalsArray;
		
	}
	
	public String processMessageFromRobot(String message)
	{
		String action = StringConst.NO_ACTION;
		if(message==null) {
			
			System.out.println("NULL Message Received");
			return action;
		}
		
		String parts[] = message.split(StringConst.STR_REQUEST_SPERARTOR);
		
		String tempMessage = parts[0];
		
		if (parts.length == 2) {
			
			String logs = parts[1];
			System.out.println(">>>>>>>>>>>    " + logs);
		}
		
		
		String[] SplitMessageByColon = tempMessage.split(":");
		
		if (SplitMessageByColon.length==0) {
			
			System.out.print(message +"  was corrupted ");
		} else {
			
			String myheader = SplitMessageByColon[0];
			System.out.println("My Header" + myheader);
			for(int i=1;i<SplitMessageByColon.length;i++)
				System.out.println(SplitMessageByColon[i]);
			
			if(myheader.equals(TESTHEADER)) {
				
				//process test header
			}else if(Robo.nextNodeToBeVisited.startsWith("Cold") ){
				
				System.out.println("Robo Reached A Inventory "+Robo.nextNodeToBeVisited);
				action = "NA";
				
				action = findBestDemand(SplitMessageByColon[1]);
				
			}
			else if(Robo.nextNodeToBeVisited.startsWith("J")  ) { //|| myheader.equals(JUNCTIONCOLOR)
				
				System.out.println("Header->" + myheader);
				System.out.println("Robo Reached A Junction "+Robo.nextNodeToBeVisited);
				//handle junction
				action = whatToDoNextForJunction(SplitMessageByColon[1]);
				//action = whatToDoNext(SplitMessageByColon[1]);
				
			}else if(Robo.nextNodeToBeVisited.startsWith("City") ) { //|| myheader.equals(CITYCOLOR)
				
				System.out.println("Header->" + myheader);
				System.out.println("Robo Reached A City "+Robo.nextNodeToBeVisited);
				//String messageToCity = cityProcessing.createMessageForCity("City0", "---Robo 0 Reached");
				
				//this.sendText(messageToCity);
				//startProgram.commClient.sendToServer(messageToCity);
				
				//process reached header
				action = whatToDoNext(SplitMessageByColon[1]);
				
			}
		}
		
		System.out.println("Following path : "+Robo.currentFollowingPath);
		System.out.println("Last Node : "+Robo.lastNodeVisited);
		System.out.println("Next Node : "+Robo.nextNodeToBeVisited);
		
		return action;
	}
	
	public int getCount (String goalId) {
		
		int cnt = 0;
		for (TSPRequest tspRequest : Robo.listFullfilledRequest) {
			
			if (tspRequest.getGoalCityId().equals(goalId)) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public String findBestDemand(String direction) {
		
		int maxCount = -1;
		String action = "NA";
		
		List<TSPRequest> listReq = new ArrayList<TSPRequest>();
		
		//Find the farmers who have requested more number of times
		for (TSPRequest tspRequest : Robo.listFullfilledRequest) {
				
			int count = getCount(tspRequest.getGoalCityId());
			
			if (count >= maxCount) {
				
				listReq.add(tspRequest);
				maxCount = count;
			}
		}
		
		int maxDemand = -1;
		String nextNode = "";
		//Now find among them whose demand is high
		for (TSPRequest tspRequest : listReq) {
			
			if (tspRequest.getDemand() > maxDemand) {
				
				maxDemand = tspRequest.getDemand();
				nextNode = tspRequest.getGoalCityId();
			}
		}
		
		action = MapClass.getNextAction(StringConst.INVENTORY, nextNode, direction);
		
		Robo.currentFollowingPath = MapClass.getBestPath(StringConst.INVENTORY, nextNode);
		Robo.lastNodeVisited = StringConst.INVENTORY;
		Robo.nextNodeToBeVisited = Robo.currentFollowingPath.get(1);
		
		System.out.println("Current Following path : " + Robo.currentFollowingPath);
		System.out.println("Last Node : " + Robo.lastNodeVisited);
		System.out.println("Next Node : " + Robo.nextNodeToBeVisited);
		
		return action;
	}
	
	/**
	 * This method will tell robo what to do next when it reaches the city.
	 */
	public String whatToDoNextForJunction(String direction) {
		
		String action = StringConst.NO_ACTION;
		String src = Robo.nextNodeToBeVisited;
		String dest = null;
		if (!Robo.currentFollowingPath.isEmpty()) {
			
			dest = Robo.currentFollowingPath.get(Robo.currentFollowingPath.indexOf(src)+1);
			action = MapClass.getNextAction(src, dest, direction);
		}
		
		
		

		//Now check all the requests that robo has and take the request that is best.
		String currentCity = Robo.nextNodeToBeVisited;
		Robo.lastNodeVisited = currentCity;
		String nextCity = dest;			
		Robo.nextNodeToBeVisited = nextCity;
		return action;
	}
	
	
	/**
	 * This method will tell robo what to do next when it reaches the city.
	 */
	public String whatToDoNext(String direction) {
		
		List<String> listCurrentRequest = Robo.currentFollowingPath;
		String currentCity = Robo.nextNodeToBeVisited;
		
		
//		for (TSPRequest tspRequest : tempReuest) {
//			
//			if (tspRequest.getGoalCityId().equals(currentCity)) {
//				//reached the farmers place now get the saman and move to next farmer.
//
//				//first remove from the request list
//				Robo.listCurrentRequests.remove(tspRequest);
//
//				//change the capacity.
//				Robo.currentCapacity = Robo.currentCapacity + tspRequest.getDemand();
//
//				//Ask node to remove request from it array.
//				String serverMsg = StringConst.STR_REQUEST_FULLFILLED + StringConst.STR_REQUEST_SPERARTOR 
//						+ Robo.roboName + StringConst.STR_REQUEST_SPERARTOR + "DEMAND_SATISFIED";
//
//				String nodeMsg = RobotProcessing.createMessageForCity(currentCity, serverMsg);
//				
//				//Send message to server.
//				CommClient.sendToServer(nodeMsg);
//				
//				break;
//			}
//		}
		
		//Now read all the info from that node.
		String nodeDemandRequestMsg = StringConst.STR_SEEK_DEMAND + StringConst.STR_SPERARTOR_COLON + Robo.roboName;
		
		nodeDemandRequestMsg = Robo.nextNodeToBeVisited + "@" +nodeDemandRequestMsg;
		
		//Send message to server.
		CommClient.sendToServer(nodeDemandRequestMsg);
		
		while (waitingFlag) {}//program will wait here till response comes.
		
		waitingFlag = true;
		
		System.out.println("After waititng ......");

		List<TSPRequest> tempReuest = new ArrayList<TSPRequest>();
		
		for (TSPRequest tr : Robo.listCurrentRequests) {
			
			tempReuest.add(tr);
		}
		
		for (TSPRequest tspRequest : tempReuest) {
			
			if (tspRequest.getGoalCityId().equals(currentCity)) {
				//reached the farmers place now get the saman and move to next farmer.

				//first remove from the request list
				Robo.listCurrentRequests.remove(tspRequest);

				//Add fulfilled request to list.
				Robo.listFullfilledRequest.add(tspRequest);
				
				//change the capacity.
				Robo.currentCapacity = Robo.currentCapacity + tspRequest.getDemand();

				//Ask node to remove request from it array.
				String serverMsg = StringConst.STR_REQUEST_FULLFILLED + StringConst.STR_SPERARTOR_COLON 
						+ Robo.roboName + StringConst.STR_SPERARTOR_COLON + "DEMAND_SATISFIED";

				String nodeMsg = RobotProcessing.createMessageForCity(currentCity, serverMsg);
				
				//Send message to server.
				CommClient.sendToServer(nodeMsg);
				
				break;
			}
		}

		
		
		
		
		//Now check all the requests that robo has and take the request that is best.
		Robo.lastNodeVisited = currentCity;
		
		String action = StringConst.NO_ACTION;
		if (Robo.currentFollowingPath.isEmpty() || Robo.currentFollowingPath.get(Robo.currentFollowingPath.size()-1).equals(currentCity)) {
			//remain stop message to robot
			
			action = StringConst.NO_ACTION;
		} else {
			
			int index1 = Robo.currentFollowingPath.indexOf(currentCity);
			String nextCity = Robo.currentFollowingPath.get(++index1);
			
			Robo.nextNodeToBeVisited = nextCity;
			
			action = MapClass.getNextAction(currentCity, nextCity, direction);
		}
		return action;
	}

	/*
	//returns city ID
	public static int roboReachedCity(){
		BufferedReader br=null;
		 FileReader fileReader;
		try {
			fileReader = new FileReader(new File("\\Camera\\coord.txt"));
			 br= new BufferedReader(fileReader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 String line = null;
		 // if no more lines the readLine() returns null
		 try {
			while ((line = br.readLine()) != null) {
			      // reading lines until the end of the file

			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] coordinatesSplit = line.split(",");
		 float xCoord = Float.parseFloat(coordinatesSplit[0]);
		 float yCoord = Float.parseFloat(coordinatesSplit[1]);
		 
		 
		 int cityId = cityProcessing.getCityFromCoord(xCoord, yCoord);
		 
		 return cityId;
	}	
	
	*/
	public String recvText() {
		String str = "NA";
		try {
			str = in.readUTF();
			
			System.out.println(">>>>>>>>>>>>>>>> " + str);
			String action = this.processMessageFromRobot(str);
			/*if(!action.equals(StringConst.NO_ACTION))
				str = "NEXTACTION:"+action;
			else*/
			
			/*if (str.contains("NA")) {
				str = "NA";
			} else {
				str = "R";
			}*/
			str = action;
			//System.out.println("Received: " + str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection terminated");
			e.printStackTrace();
		}
		return str;
	}

	
}