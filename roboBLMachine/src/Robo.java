import java.util.ArrayList;
import java.util.List;



public class Robo {

	static String roboName = "Robot1";
	static int currentCapacity = 0;
	static String lastNodeVisited = "J2";
	static String nextNodeToBeVisited = "City3";
	static int timeSinceLastCityLeft = 0;
	static String currentDestination = StringConst.INVENTORY;
	static List<TSPRequest> listCurrentRequests = new ArrayList<TSPRequest>();
	static List<String> currentFollowingPath = new ArrayList<String>();
	static List<TSPRequest> listFullfilledRequest = new ArrayList<TSPRequest>();

}
