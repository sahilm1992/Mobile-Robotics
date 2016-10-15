import java.util.ArrayList;
import java.util.List;


public class RobotProcessing {

	//public static List<TSPRequest> listRequestes = new ArrayList<TSPRequest>();
	
	public static String  createMessageForCity(String city, String message) {
		return city + "@" + message;
	}
	
	public static String processMessage(String message) {
		
		System.out.println("Message Recv from someone  as \n" + message );
		
		//Create the request object and add it to the list of request.
		if (message.contains(StringConst.STR_SPERARTOR_COLON)) {
			
			String[] requestParts = message.split(StringConst.STR_SPERARTOR_COLON);
			
			if (null == requestParts[0] && requestParts[0].equals("")) {
				
				BTMachine.waitingFlag = false;
				return null;
			}
				
			/*if (requestParts[0].equals(StringConst.STR_REQUEST)) {
				
				String strMsg = StringConst.STR_ACK + StringConst.STR_SPERARTOR_COLON + Robo.roboName 
						+ StringConst.STR_SPERARTOR_COLON;
				
				//request
				TSPRequest tspRequest = new TSPRequest(requestParts[1], Integer.parseInt(requestParts[2]));
				//listRequestes.add(tspRequest);
				
				//Now take decision what to do when request somes.
				boolean flagStatus = new RoboDecision().takeDecision(tspRequest);
				
				String strMsg = StringConst.STR_ACK + StringConst.STR_REQUEST_SPERARTOR 
									+ requestParts[1] + StringConst.STR_REQUEST_SPERARTOR 
										+ (flagStatus?"YES":"NO") + StringConst.STR_REQUEST_SPERARTOR + Robo.roboName;
				
				return createMessageForCity(requestParts[1], strMsg);
				
			} else*/ 
			if (requestParts[0].equals(StringConst.STR_SEEK_DEMAND_REPLY) || requestParts[0].equals(StringConst.STR_REQUEST)) {
				
				String strMsg = StringConst.STR_ACK_ROBOT + StringConst.STR_SPERARTOR_COLON + Robo.roboName 
									+ StringConst.STR_SPERARTOR_COLON;
		
				System.out.println("Inside ACK...");
				
				if (requestParts.length <= 2) {
					
					int minCost = 999999999;
					List<String> minPath = new ArrayList<String>();
					List<List<String>> listAllPaths = MapClass.getAllPathsWithCost(Robo.nextNodeToBeVisited, StringConst.INVENTORY);
					
					//Find all the current requesting cities
					List<String> currentRequestingCities = new ArrayList<String>();
					for (TSPRequest tempTSP : Robo.listCurrentRequests) {
						currentRequestingCities.add(tempTSP.getGoalCityId());
					}
					
					//Find the min path which has this goal node 
					for (List<String> temp : listAllPaths) {
						
						if ( temp.containsAll(currentRequestingCities)) {

							int tempMin = Integer.parseInt(temp.get(temp.size()-1));

							if (tempMin < minCost) {
								
								minCost = tempMin;
								minPath = temp;
							}
						}
					}
					
					if (Robo.listCurrentRequests.isEmpty()) {
						
						BTMachine.waitingFlag = false;
						return null;
					}
					
					Robo.currentFollowingPath = minPath;
					
					BTMachine.waitingFlag = false;
					return null;
				}
				
				String payLoad = requestParts[2];
				
				String[] payLoadRequests = payLoad.split(StringConst.STR_SPERARTOR_PERCENT);
				
				for (String req : payLoadRequests) {
					
					if (null != req && !req.equals("")) {
						
						String[] tempReq = req.split(StringConst.STR_REQUEST_SPERARTOR);
						
						TSPRequest tspRequest = new TSPRequest(tempReq[0], Integer.parseInt(tempReq[1]));
						
						boolean flagStatus = new RoboDecision().takeDecision(tspRequest);
						
						strMsg += tempReq[0] + StringConst.STR_REQUEST_SPERARTOR + (flagStatus?"YES":"NO") 
								+ StringConst.STR_SPERARTOR_PERCENT;
					} 
					
				}
				
				return createMessageForCity(requestParts[1], strMsg);
				
			} else if (requestParts[0].equals(StringConst.STR_SECONDARYACK)) {
				//Change the flag where robot machine is waiting for reply. 
				
				if (requestParts.length <= 2) {
					
					BTMachine.waitingFlag = false;
					return null;
				}
				
				//change request array
				String[] secAckNodes = requestParts[2].split(StringConst.STR_SPERARTOR_PERCENT);
				
				List<TSPRequest> removeThese = new ArrayList<TSPRequest>();
				for(String req: secAckNodes){
					//Check all the request, The cities which has as flag set, remove this cities from the current list of nodes.
					if (null != req && !req.equals("") && req.contains(StringConst.STR_REQUEST_SPERARTOR) 
								&& req.split(StringConst.STR_REQUEST_SPERARTOR)[1].equals("NO")) {
						
						for(TSPRequest tempReq : Robo.listCurrentRequests){
							if(tempReq.getGoalCityId().equals(req.split(StringConst.STR_REQUEST_SPERARTOR)[0])){
								removeThese.add(tempReq);
							}
						}
					}
				}
				
				Robo.listCurrentRequests.removeAll(removeThese);
				//change current path
				int minCost = 999999999;
				List<String> minPath = new ArrayList<String>();
				List<List<String>> listAllPaths = MapClass.getAllPathsWithCost(Robo.nextNodeToBeVisited, StringConst.INVENTORY);
				
				//Find all the current requesting cities
				List<String> currentRequestingCities = new ArrayList<String>();
				for (TSPRequest tempTSP : Robo.listCurrentRequests) {
					currentRequestingCities.add(tempTSP.getGoalCityId());
				}
				
				//Find the min path which has this goal node 
				for (List<String> temp : listAllPaths) {
					
					if ( temp.containsAll(currentRequestingCities)) {

						int tempMin = Integer.parseInt(temp.get(temp.size()-1));

						if (tempMin < minCost) {

							minCost = tempMin;
							minPath = temp;
						}
					}
				}
				
				Robo.currentFollowingPath = minPath;
				
				BTMachine.waitingFlag = false;
				
			} else {
				System.out.println("Ideally It should not come into this block");
			}
		} else {
			
			System.out.println("BAD REQUEST");
		}
		return null;
	}
	
	public static void sendMessage(String message) {
		
		startProgram.commClient.sendToServer(message);
		
	}
	
	public static float Dist( float x1, float y1 , float x2, float y2)
	{
		
		float dist = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
		
		return dist;
	}
	
}
