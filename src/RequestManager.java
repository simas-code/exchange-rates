import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class RequestManager {
	private static final String DATE_FORMAT_REGEX = "^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
	private List<Request> requestList;
	
	RequestManager(){
		this.requestList = new ArrayList<Request>();
	}
	
	RequestManager(List<Request> requestList){
		setRequestList(requestList);
	}
	
	public void setRequestList(List<Request> requestList) {
		this.requestList.clear();
		this.requestList = requestList;
	}
	
	private List<Request> getRequestList(){
		return this.requestList;
	}
	
	public void sendRequests() {
		HttpURLConnection con = null;
		URL obj = null;
		try {
			for (Request r : getRequestList()) {
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
			    printRequests(doc,r.getReTypeFlg());
			}
		} catch(Exception e) {
		    System.out.println(e);
		}
	}
	
	private BigDecimal getStartDateRate(Node node) {
		Element ele = (Element) node;
		return new BigDecimal(ele.getElementsByTagName("Amt").item(1).getTextContent()); // Exchange rate at the from date
	}
	
	public void printRequests(Document doc, int reTypeFlg) {
		switch (reTypeFlg) {
			case 1:
				if ((doc.getElementsByTagName("Err")).getLength() > 0) {
			    	System.out.println((doc.getElementsByTagName("Desc")).item(0).getTextContent());
			    } else {
			    	NodeList nodeList = doc.getElementsByTagName("FxRate");
			    	BigDecimal startDateRate = getStartDateRate(nodeList.item(nodeList.getLength()-1));
			    	
				    System.out.println("------------------------------------------------");
		            System.out.printf("|%10s|%6s|%6s|%6s|%6s|%7s|\n", "DATE","CUR","AMT","CUR","AMT","DIFF");
		            System.out.println("------------------------------------------------");
				    
		            for (int i = 0; i < nodeList.getLength(); i++) {
				        
		            	Node nodeFxRate = nodeList.item(i);
		            	Element eleFxRate = (Element) nodeFxRate;
		            	String dt = eleFxRate.getElementsByTagName("Dt").item(0).getTextContent();
		            	String ccyBase = eleFxRate.getElementsByTagName("Ccy").item(0).getTextContent();
		            	String ccyCurr = eleFxRate.getElementsByTagName("Ccy").item(1).getTextContent();
		            	BigDecimal amtBase = new BigDecimal(eleFxRate.getElementsByTagName("Amt").item(0).getTextContent());
            			BigDecimal amtCurr = new BigDecimal(eleFxRate.getElementsByTagName("Amt").item(1).getTextContent());
            			BigDecimal amtDiff = startDateRate.subtract(amtCurr);
		            	
				        if (nodeFxRate.getNodeType() == Node.ELEMENT_NODE ) {
				        	System.out.printf ("|%10s|%6s|%.4f|%6s|%.4f|%+.4f|\n", dt, ccyBase, amtBase, ccyCurr, amtCurr, amtDiff);
				        }
				    }
				    System.out.println("------------------------------------------------\n");
			    }
				break;
			case 2 :
				NodeList nodeList = doc.getElementsByTagName("CcyNtry");
		    	for (int i = 0; i < nodeList.getLength(); i++) {
		    		Node nodeCcyNtry = nodeList.item(i);
		    		Element eleCcyNtry = (Element) nodeCcyNtry;
		    		String ccyCode = eleCcyNtry.getElementsByTagName("Ccy").item(0).getTextContent();
		    		String ccyDesc = eleCcyNtry.getElementsByTagName("CcyNm").item(1).getTextContent();
		    		System.out.printf (ccyCode + " - " + ccyDesc + "\n");
		    	}
		    	break;
		}
	}
	
	public void generateRequests(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateFrom = null;
		String ccyString = null;
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
					if (!from.matches(DATE_FORMAT_REGEX)){
						System.out.print("Entered wrong parameter value.\n");
						continue;
					}
					dateFrom = sdf.parse(from);
					break;
				}
				
				while (true) {
					System.out.print("Date to (yyyy-MM-dd)\n");
					System.out.print("Enter date period end:");
					br = new BufferedReader(new InputStreamReader(System.in));
					to = br.readLine();
					if (!to.matches(DATE_FORMAT_REGEX)||
							dateFrom.after(sdf.parse(to))){
						System.out.print("Entered wrong parameter value.\n");
						continue;
					}
					break;
				}
				
				while (true) {
					System.out.print("Currency code: AUD;CAD;USD;GBP;etc..\n");
					System.out.print("Enter currency code:");
					br = new BufferedReader(new InputStreamReader(System.in));
					ccyString = br.readLine();
					setRequestList(getRequestList(ccyString,from,to));
					if (!getRequestList().isEmpty()) {
						break;
					}
				}
			} else { // If parameters were provided through console
				ccyString = args[0];
				from = args[1];
				to = args[2];
				dateFrom = sdf.parse(from);
				if (dateFrom.after(sdf.parse(to))) {
					throw new Exception("End date value is smaller then start date.");
				}
				setRequestList(getRequestList(ccyString,from,to));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Request> getRequestList(String ccyString,String from,String to){
		// Separating currency code list and populating request list 
		ArrayList<String> ccy_list = (ArrayList<String>)getCcyParam(ccyString);
		ArrayList<Request> requestList = new ArrayList<>();
		int cnt = 0; // only 10 ccy_list 
		for (String ccy : ccy_list) {
			if (ccy.matches("^[a-zA-Z]*$" ) &&
					ccy.length() == 3 ) {		
				try {
					requestList.add(new Request(ccy, from, to,1));
				} catch(InvalidParameterException e) {
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
		return requestList;
	}
	
	public List<String> getCcyParam(String ccy_params) {
		ArrayList<String> tp_list = new ArrayList<String>();
		for (String param : ccy_params.split(";")) {
			tp_list.add(param);
		}	
		return tp_list;
	}
	
	
}
