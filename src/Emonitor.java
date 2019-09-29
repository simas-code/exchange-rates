import java.util.ArrayList;

public class Emonitor {
	private static final String INTRO = "Emonitor is a program which can be used for monitoring currency exchange rates data from Lbank.lt website.\n";
	private static final String PARAM_NUMBER_ERROR = "Wrong number of parameters, for help write parameter -h.\nInitiating manual input.\n";
	private static final String HELP = "Help.\n"
			+ "Sending parameters through console is in this format: java Emonitor <ccy_list> <date_from> <date_to> .\n"
			+ "Example_1: program AUD;USD;GBP 2019-09-01 2019-09-05.\n"
			+ "Example_2: program USD 2019-09-04 2019-09-04.\n"
			+ "---------------------------------------------\n"
			+ "Input can be written manually using the same format.\n"
			+ "Currency code should be 3 leters long, codes must be seperated by semicolons.\n"
			+ "<ccy_list>: AUD;USD;GBP;.. \n"
			+ "<date_from>: yyyy-mm-dd \n"
			+ "<date_to>: yyyy-mm-dd \n";
	
	private static RequestManager rManager;
	
	public static void printCurrCodeList() {
		System.out.print( "Available currency codes list:\n");
		ArrayList<Request> templist =  new ArrayList<>();
		try {
			templist.add(new Request(null,null,null,2));
			rManager.setRequestList(templist);
			rManager.sendRequests();
		}catch (InvalidParameterException e) {
			System.out.print("Wrong parameter values.");
		}
	}
	
	public static void main(String[] args) {
		System.out.print(INTRO);
		try {
			rManager = new RequestManager();
			if (args.length > 0) {
				if (args[0].equals("-l")) {
					printCurrCodeList();
				} else if (args[0].equals("-h")) {
					System.out.print(HELP);
				} else if (args.length != 3) {
					System.out.print(PARAM_NUMBER_ERROR);
					rManager.generateRequests(null);
					rManager.sendRequests();
				} else {
					rManager.generateRequests(args);
					rManager.sendRequests();
				}
			} else {
				System.out.print(PARAM_NUMBER_ERROR);
				rManager.generateRequests(null);
				rManager.sendRequests();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}