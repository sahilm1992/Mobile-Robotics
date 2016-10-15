
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

class DemandThread implements Runnable
{
	static HashMap<String,Integer> demandCities = null;
	
	public static String demandsFileName="demands.txt";
	public static void getDemandFromFile()
	{
//		demandArray=new int[3];
		String Path  = startProgram.HomeFolderPath+demandsFileName;
		//Path = Path.concat(String.valueOf(CityId));
		//	String fin = " ";
				try {
					FileInputStream fis = new FileInputStream(Path);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					
					try {
						
						String line = br.readLine();
						while (line!=null)
						{
							
							line = line.replace("\n", "");
							System.out.println(line);
							String[] lineSplit = line.split("%");
							
							String leftSide = lineSplit[0];
							String rightSide = lineSplit[1];
							int demandValue = Integer.parseInt(rightSide);
							demandCities.put(leftSide, demandValue);
							line = br.readLine();
							
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
	
	public DemandThread() {
	}//end constructor
	public void run() {
		
		demandCities= new HashMap<String, Integer>();
		
		
		while(true)
		{
			this.getDemandFromFile();
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(demandCities.get("City1") ==1)
			{
				
				TSPRequest req = new TSPRequest("City1", 1020);
				handleRequests.handleRequest(req);
				break;
			}
			
		}
		
	}//end run
	
	
	
}//end class recievethread