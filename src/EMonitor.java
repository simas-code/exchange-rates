import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class EMonitor {
	public static void main(String[] args) {
		try {
			RequestManager rManager = new RequestManager();
			
			System.out.print("Emonitor is a program which can be used for monitoring currency exchange rates data from Lbank.lt website.\n");
			if (args.length > 0) {
				if (args[0].equals("-l")) {
					System.out.print( "Available currency codes list:\n");
					ArrayList<Request> templist =  new ArrayList<>();
					templist.add(new Request(null,null,null,2));
					rManager.setRequestList(templist);
					rManager.sendRequests();
					
				} else if (args[0].equals("-h")) {
					System.out.print( "Help.\n"
					+ "Sending parameters through console is in this format: java Emonitor <ccy_list> <date_from> <date_to> .\n"
					+ "Example_1: program AUD;USD;GBP 2019-09-01 2019-09-05.\n"
					+ "Example_2: program USD 2019-09-04 2019-09-04.\n"
					+ "---------------------------------------------\n"
					+ "Input can be written manually using the same format.\n"
					+ "Currency code should be 3 leters long, codes must be seperated by semicolons.\n"
					+ "<ccy_list>: AUD;USD;GBP;.. \n"
					+ "<date_from>: yyyy-mm-dd \n"
					+ "<date_to>: yyyy-mm-dd \n");
				} else if (args.length != 3) {
					System.out.print("Wrong number of parameters, for help write parameter -h.\n"
					+ "Initiating manual input.\n");
					rManager.generateRequests(null);
					rManager.sendRequests();
				} else {
					rManager.generateRequests(args);
					rManager.sendRequests();
				}
			} else {
				System.out.print("No parameters found, for help write parameter -h.\n"
						+ "Initiating manual input.\n");
				rManager.generateRequests(null);
				rManager.sendRequests();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}