# Emonitor
Emonitor is a small tool which can be used for monitoring official currency exchange rates of "Lietuvos Bankas"(lb.lt website).

## What data does Lbank.lt provide?
Lbank.lt provides currency exchange rate data in XML and CSV file formats.

### The data provided is:
- DATE - Date of the exchange rate value (from 2014-09-30);
- CCY - Currency code (USD,AUD,etc);
- AMT - Value of the currency at the given date.

### What URL parameters are used when requesting the data?
- tp - Exchange data type;
- ccy - Currency code (ISO 4217);
- dtFrom - Exchange rate date period start (ISO 8601);
- dtTo - Exchange rate date period end (ISO 8601).

## How to run the program:
	To get info about Emonitor use -h parameter.
	java Emonitor -h 
	
	To get available currency codes list use -l parameter.
	java Emonitor -l
### Passing parameters through console:
	java Emonitor <ccy_list> <start_date> <end_date>
	
	Parameters:
	<ccy_list> - currency codes separated by semicolons(AUD;USD;etc..)
	<start_date> - date period start in form yyyy-MM-dd 
	<end_date> - date period end in form yyyy-MM-dd
	
	Examples:
	java EMonitor USD;AUD;GBP 2019-09-01 2019-09-20
	java EMonitor USD 2019-08-01 2019-09-20

#### Running program for manual parameter input:
	java Emonitor
