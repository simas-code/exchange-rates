
public class Request {
	private static int re_cnt = 0;
	private String ccy;
	private String from;
	private String to;
	private String url;
	private int id;
	
	Request(String ccy, String from, String to) throws Exception{
		try {
			setCcy(ccy);
			setFrom(from);
			setTo(to);
			setUrl();
			setId();
			re_cnt++;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void setCcy(String ccy) throws Exception {
		if (ccy.length() != 3 || !ccy.matches("^[a-zA-Z]*$" )){
			throw new Exception("Wrong \"ccy\" parammeter value.");
		} else {
			this.ccy = ccy.toUpperCase();
		}
	}
	
	public void setFrom(String from) throws Exception {
		if (!from.matches("^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")) {
			throw new Exception("Wrong \"from\" parammeter value.");
		} else {
			this.from = from;
		}
	}
	
	public void setTo(String to) throws Exception {
		if (!to.matches("^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")) {
			throw new Exception("Wrong \"to\" parammeter value.");
		} else {
			this.to = to;
		}
	}
	
	public void setUrl() {
		if (this.ccy != null &&
			this.from != null &&
			this.to != null) {
			this.url = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getFxRatesForCurrency?tp=EU&ccy=" + this.ccy + "&dtFrom=" + this.from + "&dtTo=" + this.to;
		} else if (this.ccy != null &&
				this.from == null && 
				this.to != null ) {
			this.url = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getFxRatesForCurrency?tp=EU&ccy=" + this.ccy + "&dtFrom=" + "&dtTo=" + this.to;
		} else if (this.ccy != null &&
				this.from == null && 
				this.to == null ) {
			this.url = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getFxRatesForCurrency?tp=EU&ccy=" + this.ccy + "&dtFrom=" + "&dtTo=" ;
		} else if (this.ccy == null &&
				this.from == null && 
				this.to != null ) {
			this.url = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getFxRatesForCurrency?tp=EU&ccy=" + this.ccy + "&dtFrom=" + "&dtTo=" ;
		}
	}
	
	public void setId() {
		this.id = re_cnt;
	}

	public String getCcy() {
		return this.ccy;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	
}
