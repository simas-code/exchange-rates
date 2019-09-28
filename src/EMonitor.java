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
	
	public static void sendRequests(ArrayList<Request> requests) {		
		HttpURLConnection con = null;
		URL obj = null;
		
		try {
			for (Request r : requests) {
				obj = new URL(r.getUrl());				
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
			    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			    f.setNamespaceAware(false);
			    f.setValidating(false);
			    DocumentBuilder b = f.newDocumentBuilder();
			    Document doc = b.parse(con.getInputStream());
			    doc.getDocumentElement().normalize();

			    if ((doc.getElementsByTagName("Err")).getLength() > 0){
			    	System.out.println("Currency " + r.getCcy() + ": " + (doc.getElementsByTagName("Desc")).item(0).getTextContent());
			    } else {
			    	NodeList nodeList_date = doc.getElementsByTagName("Dt");
				    NodeList nodeList_ccyamt = doc.getElementsByTagName("Amt");

				    System.out.println("------------------------------------------------");
		            System.out.printf("|%10s|%6s|%6s|%6s|%6s|%7s|\n", "DATE","CUR","AMT","CUR","AMT","DIFF");
		            System.out.println("------------------------------------------------");
				    for (int i = 0, z = 0; i < nodeList_date.getLength(); i++,z+=2) {
				        Node node_i = nodeList_date.item(i);		// Date
				        Node node_z1 = nodeList_ccyamt.item(z); 	// EUR amount
				        Node node_z2 = nodeList_ccyamt.item(z+1); 	// Chosen curr amount
				        BigDecimal curr_rat = new BigDecimal(nodeList_ccyamt.item(nodeList_ccyamt.getLength()-1).getTextContent()); // Period start curr amount
				        
				        if (node_i.getNodeType() == Node.ELEMENT_NODE &&
				        		node_z1.getNodeType() == Node.ELEMENT_NODE &&
				        		node_z2.getNodeType() == Node.ELEMENT_NODE ) {
				        	System.out.printf ("|%10s|%6s|%.4f|%6s|%.4f|%+.4f|\n",node_i.getTextContent(),"EUR",new BigDecimal(node_z1.getTextContent()), r.getCcy(),new BigDecimal(node_z2.getTextContent()),new BigDecimal(node_z2.getTextContent()).subtract(curr_rat));
				        }
				    }
				    System.out.println("------------------------------------------------\n");
			    }
			}
		} catch(Exception e) {
		    System.out.println(e);
		}
	}
	
	public static ArrayList<Request> generateRequests(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date_from = null;
		ArrayList<Request> request_list = null;
		String ccy_string = null;
		String from = null;
		String to = null;

		try {
			if (args == null) { // If no parameters were provided
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
				
				System.out.println("Enter the parameters.");
				while (true) {
					System.out.print("Date from (yyyy-MM-dd)\n");
					System.out.print("Enter date period start:");
					br = new BufferedReader(new InputStreamReader(System.in));
					from = br.readLine();
					if (!from.matches("^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")){
						System.out.print("Entered wrong parameter value.\n");
						continue;
					}
					date_from = sdf.parse(from);
					break;
				}
				
				while (true) {
					System.out.print("Date to (yyyy-MM-dd)\n");
					System.out.print("Enter date period end:");
					br = new BufferedReader(new InputStreamReader(System.in));
					to = br.readLine();
					if (!to.matches("^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")||
							date_from.after(sdf.parse(to))){
						System.out.print("Entered wrong parameter value.\n");
						continue;
					}
					break;
				}
				
				while (true) {
					System.out.print("Currency code: AUD;CAD;USD;GBP;etc..\n");
					System.out.print("Enter currency code:");
					br = new BufferedReader(new InputStreamReader(System.in));
					ccy_string = br.readLine();
					request_list = getRequestList(ccy_string,from,to);
					if (request_list.size() > 0) {
						break;
					}
				}
			} else { // If parameters were provided through console
				ccy_string = args[0];
				from = args[1];
				to = args[2];
				date_from = sdf.parse(from);
				if (date_from.after(sdf.parse(to))) {
					throw new Exception("End date value is smaller then start date.");
				}
				request_list = getRequestList(ccy_string,from,to);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return request_list;
	}
	
	
	public static ArrayList<Request> getRequestList(String ccy_string,String from,String to){
		// Separating currency code list and populating request list 
		ArrayList<String> ccy_list = getCcyParam(ccy_string);
		ArrayList<Request> request_list = new ArrayList<>();
		int cnt = 0; // only 10 ccy_list 
		for (String ccy : ccy_list) {
			if (ccy.matches("^[a-zA-Z]*$" ) &&
					ccy.length() == 3 ) {		
				try {
					request_list.add(new Request(ccy, from, to));
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.print("Wrong ccy parameter format: " + ccy + "\n");
			}
			
			if (cnt >= 9) {
				break;
			}
			cnt++;
		}
		return request_list;
	}
	
	public static ArrayList<String> getCcyParam(String ccy_params) {
		ArrayList<String> tp_list = new ArrayList<String>();
		for (String param : ccy_params.split(";")) {
			tp_list.add(param);
		}	
		return tp_list;
	}
	
	public static void main(String[] args) {
		try {
			System.out.print("Emonitor is a program which can be used for monitoring currency exchange rates data from Lbank.lt website.\n");
			if (args.length > 0) {
				if (args[0].equals("-h")) {
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
					sendRequests(generateRequests(null));
				} else {
					sendRequests(generateRequests(args));
				}
			} else {
				System.out.print("No parameters found, for help write parameter -h.\n"
						+ "Initiating manual input.\n");
				sendRequests(generateRequests(null));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}