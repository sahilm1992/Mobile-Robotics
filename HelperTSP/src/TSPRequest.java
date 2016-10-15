public class TSPRequest{
		
	public String goalCityId;
	public int demand;
	public int demandStatus;// 0->open   ,   1->Ack
	public String demandAckByRobot;
	
	public TSPRequest(String goalCityId, int demand){
		this.goalCityId = goalCityId;
		this.demand = demand;
		this.demandStatus = 0;
	}
	
	public String getGoalCityId() {
		return goalCityId;
	}

	public void setGoalCityId(String goalCityId) {
		this.goalCityId = goalCityId;
	}

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}

	public int getDemandStatus() {
		return demandStatus;
	}

	public void setDemandStatus(int demandStatus) {
		this.demandStatus = demandStatus;
	}

	public String getDemandAckByRobot() {
		return demandAckByRobot;
	}

	public void setDemandAckByRobot(String demandAckByRobot) {
		this.demandAckByRobot = demandAckByRobot;
	}

}