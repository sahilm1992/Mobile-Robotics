public class startProgram {

	public static CommClient commClient =null;
	public static int commInit = 0;
	public static int bLcommInit = 0;
	public static void  main(String[] args) {
		BTMachine btMachine = new BTMachine();
		initCommunication();
		while(commInit == 0)
		{
			
		}
		
		System.out.println("Before BL Start");
		btMachine.run();
		System.out.println("BL Started");
		while(bLcommInit == 0){
			
		}
	}
	
	
	public static void initCommunication()
	{
		commClient = new CommClient();
		commClient.initCommwithServer();
	}
	
}
