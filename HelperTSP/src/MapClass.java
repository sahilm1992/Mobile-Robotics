

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapClass {	
	static Map<String, String> mapNodes = new HashMap<String, String>();
	static Map<String, String> mapJunctions = new HashMap<String, String>();
	static Map<String, String> mapLinks = new HashMap<String, String>();
	static Map<String, String> mapCost = new HashMap<String, String>();
	static Map<String, Set<String>> mapNeighbours = new HashMap<String, Set<String>>();	
	
	static {
	
		try {
			createMap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		//getAllPathsWithCost("City7", "ColdStorage");
		System.out.println(getAllPaths("City7", "ColdStorage"));
	}
	
	public static String getCoordinates(String name){
		String coord = "-1,-1";
		if(mapNodes.containsKey(name)){
			coord = mapNodes.get(name);
		}
		return coord;
	}
	
	public static int getCost(List<String> path){
		int cost = 0;
		for(String node : path){
			
			int index  = path.indexOf(node);
			
			if (index == path.size()-1) {
				break;
			}
			
			index++;
			String nextNode = path.get(index);
			
			//Get the cost from the map
			if (mapCost.containsKey(node+","+nextNode)) {
				
				String strCost = mapCost.get(node+","+nextNode);
				cost += Integer.parseInt(strCost);
			} else if (mapCost.containsKey(nextNode+","+node)) {
				
				String strCost = mapCost.get(nextNode+","+node);
				cost += Integer.parseInt(strCost);
			}
			
		}
		return cost;
	}

	public static String getNextAction(String src, String dest, String direction){
		List<String> pathTillNow = new ArrayList<String>();
		List<List<String>> paths = new ArrayList<List<String>>();
		pathTillNow.add(src);
		
		getAllPossiblePathsNew(src,dest, pathTillNow,paths);
		System.out.println("Paths : "+paths.toString());
		
		int minCost = 999999999;
		List<String> bestPath = new ArrayList<String>();
		for (List<String> path : paths) {
			
			int cost = getCost(path);
			
			if (cost < minCost) {
				minCost = cost;
				bestPath.clear();
				bestPath.addAll(path);
			}
		}
		
		String nextDesti = bestPath.get(1);
		String nextAction = mapLinks.get(src+","+nextDesti+","+direction);
		return nextAction;
	}
	
	public static List<List<String>> getAllPathsWithCost(String src, String dest) {
		List<String> pathTillNow = new ArrayList<String>();
		List<List<String>> paths = new ArrayList<List<String>>() ;
		pathTillNow.add(src);
		getAllPossiblePathsNew(src, dest, pathTillNow, paths);
	
		List<List<String>> pathTillNowWithCost = new ArrayList<List<String>>();
		pathTillNow.add(src);
		
		for (List<String> path : paths) {
			String cost = String.valueOf(getCost(path));
			path.add(cost);
			pathTillNowWithCost.add(path);
		}
		return pathTillNowWithCost;
	}
	
	public static List<List<String>> getAllPaths(String src, String dest) {
		List<String> pathTillNow = new ArrayList<String>();
		pathTillNow.add(src);
		List<List<String>> paths = new ArrayList<List<String>>();
		getAllPossiblePathsNew(src, dest, pathTillNow, paths);
		return(paths); 
	}
	
	public static List<String> getBestPath (String src, String dest) {
		
		List<String> pathTillNow = new ArrayList<String>();
		List<List<String>> paths = new ArrayList<List<String>>();
		pathTillNow.add(src);
		
		getAllPossiblePathsNew(src, dest, pathTillNow, paths);
		System.out.println("Paths : "+paths.toString());
		
		int minCost = 999999999;
		List<String> bestPath = new ArrayList<String>();
		for (List<String> path : paths) {
			
			int cost = getCost(path);
			
			if (cost < minCost) {
				minCost = cost;
				bestPath.clear();
				bestPath.addAll(path);
			}
		}
		
		return bestPath;
	}
	
	public static void getAllPossiblePaths(String src, String dest, List<String> pathTillNow, List<List<String>> paths) {
		Set<String> neighbours = getNeighbours(src);
//		
//		if(neighbours.contains(dest)){
//			pathTillNow.add(dest);
//			paths.add(pathTillNow);
//			//return pathTillNow;
//			//String cost = mapCost.get(src+","+dest);
//		}
		//else{
			//find all possible paths
			for(String str : neighbours){
				if(str.equals(dest)){
					List<String> newPathTillNow = new ArrayList<String>(pathTillNow); 
					newPathTillNow.add(str);
					//pathTillNow.add(dest);
					paths.add(newPathTillNow);
				} 
				else if (!pathTillNow.contains(str)) {
					List<String> newPathTillNow = new ArrayList<String>(pathTillNow); 
					newPathTillNow.add(str);
					getAllPossiblePaths(str, dest, newPathTillNow,paths);
				}
			}
		//}
	}

	
	public static int getCount(List<String> pathTillNow, String str){
		int cnt = 0;
		
		for (String temp : pathTillNow) {
			
			if (temp.equals(str)) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public static void getAllPossiblePathsNew(String src, String dest, List<String> pathTillNow, List<List<String>> paths) {
		Set<String> neighbours = getNeighbours(src);
//		
//		if(neighbours.contains(dest)){
//			pathTillNow.add(dest);
//			paths.add(pathTillNow);
//			//return pathTillNow;
//			//String cost = mapCost.get(src+","+dest);
//		}
		//else{
			//find all possible paths
			for(String str : neighbours){
				if(str.equals(dest)){
					List<String> newPathTillNow = new ArrayList<String>(pathTillNow); 
					newPathTillNow.add(str);
					//pathTillNow.add(dest);
					paths.add(newPathTillNow);
				} 
				else if (/*!pathTillNow.contains(str) && */getCount(pathTillNow,str) <= 1) {
					List<String> newPathTillNow = new ArrayList<String>(pathTillNow); 
					newPathTillNow.add(str);
					getAllPossiblePathsNew(str, dest, newPathTillNow,paths);
				}
			}
		//}
	}

	
	
	public static Set<String> getNeighbours(String src) {
		Set<String> neighbours = mapNeighbours.get(src);
		return neighbours;
	}

	public static Set<String> getNeighbourNodes(String src) {
		Set<String> neighbours = mapNeighbours.get(src);
		Set<String> newNeighbours = new HashSet<String>();
		for(String str:neighbours){
			if(!mapJunctions.containsKey(str)&& !(str.equals(src))){
				newNeighbours.add(str);
			}
			else{
				Set<String> junctionNeighbours = mapNeighbours.get(str);
				for(String str1:junctionNeighbours){
					if(!mapJunctions.containsKey(str1) && !(str1.equals(src))){
						newNeighbours.add(str1);
					}
				}
			}
		}
		return newNeighbours;
	}
	
	public static void createMap() throws Exception {
		BufferedReader br = null;
		String desktopPath = System.getProperty("user.home","Desktop")+"\\Desktop";
		desktopPath += "\\TSPMap\\";
		try{
			String tempStr;
			
			br =new BufferedReader(new FileReader(desktopPath+"Nodes.txt"));
			while ((tempStr = br.readLine()) != null) {
				if (!tempStr.isEmpty()){
					tempStr = tempStr.trim();
					String[] arr = tempStr.split(" ");
					mapNodes.put(arr[0],arr[1]+","+arr[2]);
				}
			}
		} catch (IOException e) {e.printStackTrace();} finally {try {if (br != null)br.close();} catch (IOException ex) {ex.printStackTrace();}}
		try{
			String tempStr;
			br =new BufferedReader(new FileReader(desktopPath+"Junctions.txt"));
			while ((tempStr = br.readLine()) != null) {
				if (!tempStr.isEmpty()){
					tempStr = tempStr.trim();
					String[] arr = tempStr.split(" ");
					mapJunctions.put(arr[0],arr[1]+","+arr[2]);
				}
			}
		} catch (IOException e) {e.printStackTrace();} finally {try {if (br != null)br.close();} catch (IOException ex) {ex.printStackTrace();}}
		//System.out.println(mapJunctions.toString());
		try{
			String tempStr;
			br =new BufferedReader(new FileReader(desktopPath+"Links.txt"));
			while ((tempStr = br.readLine()) != null) {
				if (!tempStr.isEmpty()){
					tempStr = tempStr.trim();
					String[] arr = tempStr.split(" ");
					mapLinks.put(arr[0]+","+arr[1]+","+arr[2],arr[3]);
					
				}
			}
		} catch (IOException e) {e.printStackTrace();} finally {try {if (br != null)br.close();} catch (IOException ex) {ex.printStackTrace();}}
		//System.out.println(mapLinks.toString());
		try{
			String tempStr;
			br =new BufferedReader(new FileReader(desktopPath+"Cost.txt"));
			while ((tempStr = br.readLine()) != null) {
				if (!tempStr.isEmpty()){
					tempStr = tempStr.trim();
					String[] arr = tempStr.split(" ");
					mapCost.put(arr[0]+","+arr[1],arr[2]);
					
					
					if(mapNeighbours.containsKey(arr[0])){
						mapNeighbours.get(arr[0]).add(arr[1]);
					}
					else{
						Set<String> tempSet = new HashSet<String>();
						tempSet.add(arr[1]);
						mapNeighbours.put(arr[0],tempSet);
					}
					
					if(mapNeighbours.containsKey(arr[1])){
						mapNeighbours.get(arr[1]).add(arr[0]);
					}
					else{
						Set<String> tempSet = new HashSet<String>();
						tempSet.add(arr[0]);
						mapNeighbours.put(arr[1],tempSet);
					}
					
					
				}
				
			}
			
		} catch (IOException e) {e.printStackTrace();} finally {try {if (br != null)br.close();} catch (IOException ex) {ex.printStackTrace();}}
			}
	
	//Updates distance - point to point
	public static int updateDistance(String src,String dest,int dist){
		if(mapCost.containsKey(src+","+dest)){
			
			mapCost.put(src+","+dest, String.valueOf(dist));
			return 0 ;
		}
		return 1; 
	}
	
}
