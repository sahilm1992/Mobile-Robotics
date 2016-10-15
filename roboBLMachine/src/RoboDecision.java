import java.util.ArrayList;
import java.util.List;


public class RoboDecision {
	
	public void addDemand (TSPRequest tspRequest) {
		
		List<TSPRequest> listTemp = new ArrayList<TSPRequest>(Robo.listCurrentRequests);
		if(listTemp.isEmpty()){
			Robo.listCurrentRequests.add(tspRequest);
		}
		else{
			for (TSPRequest ts : listTemp) {
				if (!tspRequest.getGoalCityId().equals(ts.getGoalCityId())) {
					Robo.listCurrentRequests.add(tspRequest);
				}
			}
		}
	}

	public boolean takeDecision (TSPRequest tspRequest) {
		
		//for (TSPRequest tspRequest : tempAllRequest) {
		boolean flag = false;
		
		System.out.println("Inside Decision module............");
		
		if (Robo.currentFollowingPath.isEmpty() 
				&& tspRequest.getDemand() < (StringConst.INT_MAX_ROBO_CAP - Robo.currentCapacity)) {
			//robo does not have anything to do, accept the request.
			
			flag = true;
			addDemand(tspRequest);
//			Robo.listCurrentRequests.add(tspRequest);
			Robo.currentFollowingPath = MapClass.getBestPath(tspRequest.getGoalCityId(), StringConst.INVENTORY);
			//Robo.currentFollowingPath = MapClass.getBestPath(Robo.lastNodeVisited, tspRequest.getGoalCityId());
			System.out.println(Robo.currentFollowingPath.toString());
			tspRequest.setDemandStatus(1);
			//now tell the robo to go ahead.
		} else {
			
			if (tspRequest.getDemandStatus() == 0) {
				
				if (tspRequest.getDemand() < (StringConst.INT_MAX_ROBO_CAP - Robo.currentCapacity)) {
					//robo can satisfy the request.
					
					//First check weather new request lies on the same path as current request.
					if (Robo.currentFollowingPath.contains(tspRequest.getGoalCityId())) {
						//yes accept the request. Add the request to its current list

						flag = true;
//						Robo.listCurrentRequests.add(tspRequest);
						addDemand(tspRequest);
						tspRequest.setDemandStatus(1);
					} else {
						
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
							
							if (temp.contains(tspRequest.getGoalCityId()) && temp.containsAll(currentRequestingCities)) {

								int tempMin = Integer.parseInt(temp.get(temp.size()-1));

								if (tempMin < minCost) {
									
									minCost = tempMin;
									minPath = temp;
								}
							}
						}
						
						//Apply heuristic here whether to choose that path or not.
						int currentPathCost = MapClass.getCost(Robo.currentFollowingPath);
						if (minCost <= 10*currentPathCost) {
							//accept the request
							flag = true;
//							Robo.listCurrentRequests.add(tspRequest);
							addDemand(tspRequest);
							Robo.currentFollowingPath = minPath;
							tspRequest.setDemandStatus(1);
						}
					}
				}
			}
		}
		
		return flag;
		
	}
}
