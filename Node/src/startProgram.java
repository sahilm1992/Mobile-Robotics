import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class startProgram implements ActionListener {

	static JTextField name;
	static JTextField demand;
	static JPanel panel;
	static JFrame frame;
	static JButton submit;
	static JButton enter;
	
	static JLabel currentDemand;
	static JLabel currentDemandLabel;
	public static CommClient commClient =null;
	public static int commInit = 0;
	public static String myCityId = "";
	
	//public static String [] RoboNames =null;
	public static ArrayList<String> RoboNames = null;
	public static HashMap<String, float[]> RoboPosition = null;
	
	public static String HomeFolderPath= "Files\\";
	public static String RoboName_FileName = "robotNames.txt";
	public static int robotDistanceThreshold=500;
	public static ArrayList<TSPRequest> TSPRequestArrayList = null;
	
	
	public void SetDemandPanel(){
		
		
		frame = new JFrame("Demand Generation");
	    panel = new JPanel(new FlowLayout());
	    name = new JTextField();
        enter = new JButton("Enter Name");
        demand = new JTextField();
        submit = new JButton("Submit");
        currentDemand = new JLabel("0");
        currentDemandLabel = new JLabel("Current Demand");
        enter.setActionCommand("1");
        submit.setActionCommand("2");   
        name.setPreferredSize(new Dimension(100,30));
        enter.setPreferredSize(new Dimension(100, 30));
        currentDemand.setPreferredSize(new Dimension(100,30));
        currentDemandLabel.setPreferredSize(new Dimension(100, 30));
        
        panel.setPreferredSize(new Dimension(250,150));
        frame.setPreferredSize(new Dimension(250,150));
        demand.setPreferredSize(new Dimension(100,30));
        submit.setPreferredSize(new Dimension(100, 30));
        
        panel.add(name);
        panel.add(enter);
        panel.add(demand);
        panel.add(submit);
        panel.add(currentDemand);
        panel.add(currentDemandLabel);
        frame.setContentPane(panel);
        frame.pack();
        submit.addActionListener(this);
        enter.addActionListener(this);
        frame.setVisible(true);
        demand.setEnabled(false);
        submit.setEnabled(false);
        currentDemand.setEnabled(false);
        currentDemandLabel.setEnabled(false);
     
	}
	
	public static void initNode()
	{
		initCommunication();
		while(commInit == 0)
		{
			
		}
		
		
		
		RoboNames= new ArrayList<String>();
		RoboPosition = new HashMap<String, float[]>();
		
		
		String Path  = "";
				try {
					FileInputStream fis = new FileInputStream(HomeFolderPath+RoboName_FileName);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					
					try {
						
						String line = br.readLine();
						while (line!=null)
						{

							line = line.replace("\n", "");
							
							RoboNames.add(line);
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
	
	
	
	public static void  main(String[] args) {
		startProgram start = new startProgram();
		start.SetDemandPanel();
		//DemandThread demandT = null;
		//demandT= new DemandThread();
		
		//Thread demandThread =new Thread(demandT);
		//demandThread.start();
	TSPRequestArrayList= new ArrayList<TSPRequest>();

			
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 int action = Integer.parseInt(e.getActionCommand());
		 
		 
		 switch(action) {
		 case 1:
			 myCityId  = name.getText();
			 demand.setEnabled(true);
			 submit.setEnabled(true);
			 currentDemand.setEnabled(true);
			 currentDemandLabel.setEnabled(true);
			name.setEnabled(false);
	        enter.setEnabled(false);
		 	System.out.println("name is "+ myCityId );
			initNode();
			 break;

		 case 2 :
			 String demandStr = demand.getText();
			 System.out.println("demand is "+ demandStr);
			 //create request object
			 int demandInt =  Integer.parseInt(demandStr);
			 TSPRequest req = new TSPRequest(myCityId ,demandInt);
			 TSPRequestArrayList.add(req);
			 handleRequests.handleOwnRequest(req);
			 currentDemand.setText(demandStr);
			 
			 break;
		 }
		 
		 
		
	}

	public static void initCommunication()
	{
		commClient = new CommClient();
		commClient.initCommwithServer();
	}
	
}
