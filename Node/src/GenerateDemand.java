

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class GenerateDemand implements ActionListener{
	String myName;
	static JTextField name;
	static JTextField demand;
	static JPanel panel;
	static JFrame frame;
	static JButton submit;
	static JButton enter;
	public void SetDemandPanel(){
		
		frame = new JFrame("Demand Generation");
	    panel = new JPanel(new FlowLayout());
	    name = new JTextField();
        enter = new JButton("Enter Name");
        demand = new JTextField();
        submit = new JButton("Submit");
        
        enter.setActionCommand("1");
        submit.setActionCommand("2");   
        name.setPreferredSize(new Dimension(150,30));
        enter.setPreferredSize(new Dimension(150, 30));
        
        panel.setPreferredSize(new Dimension(350,350));
        frame.setPreferredSize(new Dimension(350,350));
        demand.setPreferredSize(new Dimension(150,30));
        submit.setPreferredSize(new Dimension(150, 30));
        
        panel.add(name);
        panel.add(enter);
        panel.add(demand);
        panel.add(submit);
        frame.setContentPane(panel);
        frame.pack();
        submit.addActionListener(this);
        enter.addActionListener(this);
        frame.setVisible(true);
        demand.setEnabled(false);
        submit.setEnabled(false);
     
        
	}
	
	public static void main(String[] args)throws InterruptedException {
	
		GenerateDemand demand = new GenerateDemand();
		demand.SetDemandPanel();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 int action = Integer.parseInt(e.getActionCommand());
		 
		 
		 switch(action) {
		 case 1:
			 myName = name.getText();
			 demand.setEnabled(true);
			 submit.setEnabled(true);
			name.setEnabled(false);
	        enter.setEnabled(false);
		 	System.out.println("name is "+ myName);
			 break;

		 case 2 :
			 String demandStr = demand.getText();
			 System.out.println("demand is "+ demandStr);
			 //create request object
			 int demandInt =  Integer.parseInt(demandStr);
			 TSPRequest req = new TSPRequest(myName,demandInt);
			 break;
		 }
		 
		
	}
	
	
	
}
