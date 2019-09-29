public class Request {
	private static final String REQUEST_URL_RLIST = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getFxRatesForCurrency?tp=EU&ccy=%s&dtFrom=%s&dtTo=%s";
	private static final String REQUEST_URL_CLIST = "http://www.lb.lt/webservices/fxrates/FxRates.asmx/getCurrencyList";
	private static final String DATE_FORMAT_REGEX = "^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
	private static final String ALPHA_REGEX = "^[a-zA-Z]*$";
	
	private String ccy;
	private String from;
	private String to;
	private String url;
	private int reTypeFlg; // 1 - Currency exchange rates list request; 2 - Available currency list request;
	
	Request(String ccy, String from, String to, int reTypeFlg) throws InvalidParameterException{
		setCcy(ccy);
		setFrom(from);
		setTo(to);
		setReTypeFlg(reTypeFlg);
		setUrl(reTypeFlg);	
	}
	
	private void setCcy(String ccy) throws InvalidParameterException {
		if (ccy != null) {
			if (ccy.length() != 3 || !ccy.matches(ALPHA_REGEX)){
				throw new InvalidParameterException("Wrong \"ccy\" parammeter value.");
			} else {
				this.ccy = ccy.toUpperCase();
			}
		} else {
			this.ccy = "";
		}
	}
	
	private void setFrom(String from) throws InvalidParameterException {
		if (from != null) {
			if (!from.matches(DATE_FORMAT_REGEX)) {
				throw new InvalidParameterException("Wrong \"from\" parammeter value.");
			} else {
				this.from = from;
			}
		} else {
			this.from = "";
		}
	}
	
	private void setTo(String to) throws InvalidParameterException {
		if (to != null) {
			if (!to.matches(DATE_FORMAT_REGEX)) {
				throw new InvalidParameterException("Wrong \"to\" parammeter value.");
			} else {
				this.to = to;
			}
		} else {
			this.to = "";
		}
	}
	
	private void setUrl(int reTypeFlg) throws InvalidParameterException {
		switch (reTypeFlg) {
			case 1:
				this.url = String.format(REQUEST_URL_RLIST, this.ccy, this.from, this.to);
				break;
			case 2:
				this.url = REQUEST_URL_CLIST;
				break;
			default:
				throw new InvalidParameterException("Wrong \"reTypeFlg\" parammeter value.");
		}
	}
	
	private void setReTypeFlg(int reTypeFlg) {
		this.reTypeFlg = reTypeFlg;
	}

	public String getCcy() {
		return this.ccy;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public int getReTypeFlg() {
		return this.reTypeFlg;
	}
	
}
