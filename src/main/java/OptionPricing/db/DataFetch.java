package OptionPricing.db;

import com.google.common.primitives.Doubles;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

import com.mathworks.extern.java.MWCellArray;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWStructArray;

public class DataFetch {

    private String stock;
    private StockPrice stockPrice = new StockPrice();
    private ArrayList<OptionPrice> putOptionList = new ArrayList<OptionPrice>();
    private ArrayList<OptionPrice> callOptionList = new ArrayList<OptionPrice>();

    private OptionData call;
    private OptionData put;
    public static void main(String[] args) throws ParseException {
        /*
         DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
         Date startDate = df.parse(startDate);
         */



        DataFetch tableEg = new DataFetch("AMD");
        tableEg.fetchStock("27/6/2007", "28/2/2010");
        double[] d = tableEg.getStockPrice().getOpen();
        tableEg.fetchOptionData();
    }

    public DataFetch(String _stock) {
        stock = _stock;
    }

    public void fetchOptionData() {
        //String html = "http://publib.boulder.ibm.com/infocenter/iadthelp/v7r1/topic/" +
        //    "com.ibm.etools.iseries.toolbox.doc/htmtblex.htm";

        String html = "http://finance.yahoo.com/q/op?s=" + stock + "+Options";
        try {

            boolean is_call = true;
            Document doc = Jsoup.connect(html).get();

            for (Element table : doc.select("table[class=yfnc_datamodoutline1]")) {


                Elements tableHeaderEles = table.select("th");
                System.out.println("headers");
                for (int i = 0; i < tableHeaderEles.size(); i++) {
                    System.out.println(tableHeaderEles.get(i).text());
                }

                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.size() == 8) {
                        OptionPrice optionPrice = new OptionPrice();

                        System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
                        if (is_call == true) {
                            getCallOptionList().add(optionPrice);
                            optionPrice.setSybol(tds.get(0).text());
                            //  callList.put(tds.get(1).text(), tds.get(0).text());
                        } else {
                            //   putList.put(tds.get(1).text(), tds.get(0).text());
                            getPutOptionList().add(optionPrice);
                            optionPrice.setSybol(tds.get(0).text());
                        }
                    }

                }
                is_call = false;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchStock(String date1, String date2) {

        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Calendar startDate = new GregorianCalendar();//df.parse(date1).
            Calendar endDate = Calendar.getInstance();// df.parse(date2);

            startDate.setTime(df.parse(date1));
            endDate.setTime(df.parse(date2));
            try {
                //try {
                Reader reader = null;
                InputStream input = null;
                input = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + stock + "&d=" + startDate.get(Calendar.DAY_OF_MONTH) + "&e=" + startDate.get(Calendar.MONTH) + "&f=" + startDate.get(Calendar.YEAR) + "&g=d&a=" + endDate.get(Calendar.DAY_OF_MONTH) + "&b=" + endDate.get(Calendar.MONTH) + "&c=" + endDate.get(Calendar.YEAR) + "&ignore=.csv").openStream();
                reader = new InputStreamReader(input, "UTF-8");
                BufferedReader brReadMe = new BufferedReader(reader);
                String strLine = brReadMe.readLine();
                strLine = brReadMe.readLine();
                //for all lines
                while (strLine != null) {
                    try {
                        // get data fro the line
                        String lineEle[] = strLine.split(",");
                        getStockPrice().open.add(Double.parseDouble(lineEle[1]));
                        getStockPrice().high.add(Double.parseDouble(lineEle[2]));
                        getStockPrice().low.add(Double.parseDouble(lineEle[3]));
                        getStockPrice().close.add(Double.parseDouble(lineEle[4]));
                        getStockPrice().volume.add(Double.parseDouble(lineEle[5]));
                        getStockPrice().adjClose.add(Double.parseDouble(lineEle[6]));

                        //update line
                        strLine = brReadMe.readLine();
                    } //end for
                    catch (IOException ex) {
                        Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } //end for
                reader.close();
                priceToMatalab();
                //
            } catch (IOException ex) {
                Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException ex) {
            Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

    /**
     * @return the putOptionList
     */
    public ArrayList<OptionPrice> getPutOptionList() {
        return putOptionList;
    }

    /**
     * @return the callOptionList
     */
    public ArrayList<OptionPrice> getCallOptionList() {
        return callOptionList;
    }

    /**
     * @param callOptionList the callOptionList to set
     */
    public void setCallOptionList(ArrayList<OptionPrice> callOptionList) {
        this.callOptionList = callOptionList;
    }

    /**
     * @return the stockPrice
     */
    public StockPrice getStockPrice() {
        return stockPrice;
    }

    /**
     * @param stockPrice the stockPrice to set
     */
    public void setStockPrice(StockPrice stockPrice) {
        this.stockPrice = stockPrice;
    }
}

class OptionPrice {

    private String sybol;
    private Double strike, last, change, bid, ask;
    private Integer volume, openInt;
    private MWStructArray prices;

    public void priceToMatalab() {

        final String[] pricesFieldsNames = {"open", "high", "low", "close", "volume", "adjClose"};
        setPrices(new MWStructArray(1, 1, pricesFieldsNames));
        //	MWNumericArray openNumericArray = new MWNumericArray(molarArray, MWClassID.DOUBLE); 
        //double[][] openArray = new double[1][open.size()];

        //prices.set ("symbol",1,symbol);
        getPrices().set("strike", 1, getStrike());
        getPrices().set("last", 1, getLast());
        getPrices().set("change", 1, getChange());
        getPrices().set("bid", 1, getBid());
        getPrices().set("ask", 1, getAsk());
        getPrices().set("openInt", 1, getOpenInt());



        // MWNumericArray openNumericArray = new MWNumericArray(openArray, MWClassID.DOUBLE); 
    }

    /**
     * @return the sybol
     */
    public String getSybol() {
        return sybol;
    }

    /**
     * @param sybol the sybol to set
     */
    public void setSybol(String sybol) {
        this.sybol = sybol;
    }

    /**
     * @return the strike
     */
    public Double getStrike() {
        return strike;
    }

    /**
     * @param strike the strike to set
     */
    public void setStrike(Double strike) {
        this.strike = strike;
    }

    /**
     * @return the last
     */
    public Double getLast() {
        return last;
    }

    /**
     * @param last the last to set
     */
    public void setLast(Double last) {
        this.last = last;
    }

    /**
     * @return the change
     */
    public Double getChange() {
        return change;
    }

    /**
     * @param change the change to set
     */
    public void setChange(Double change) {
        this.change = change;
    }

    /**
     * @return the bid
     */
    public Double getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(Double bid) {
        this.bid = bid;
    }

    /**
     * @return the ask
     */
    public Double getAsk() {
        return ask;
    }

    /**
     * @param ask the ask to set
     */
    public void setAsk(Double ask) {
        this.ask = ask;
    }

    /**
     * @return the volume
     */
    public Integer getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    /**
     * @return the openInt
     */
    public Integer getOpenInt() {
        return openInt;
    }

    /**
     * @param openInt the openInt to set
     */
    public void setOpenInt(Integer openInt) {
        this.openInt = openInt;
    }

    /**
     * @return the prices
     */
    public MWStructArray getPrices() {
        return prices;
    }

    /**
     * @param prices the prices to set
     */
    public void setPrices(MWStructArray prices) {
        this.prices = prices;
    }
}

class StockPrice {

    public ArrayList<Double> open = new ArrayList<Double>();
    public ArrayList<Double> high = new ArrayList<Double>();
    public ArrayList<Double> low = new ArrayList<Double>();
    public ArrayList<Double> close = new ArrayList<Double>();
    public ArrayList<Double> volume = new ArrayList<Double>();
    public ArrayList<Double> adjClose = new ArrayList<Double>();

    /**
     * @return the open
     */
    public double[] getOpen() {
        return Doubles.toArray(open);
    }

    /**
     * @return the high
     */
    public double[] getHigh() {
        return Doubles.toArray(high);
    }

    /**
     * @return the low
     */
    public double[] getLow() {
        return Doubles.toArray(low);
    }

    /**
     * @return the close
     */
    public double[] getClose() {
        return Doubles.toArray(close);
    }

    /**
     * @return the volume
     */
    public double[] getVoulme() {
        return Doubles.toArray(volume);
    }

    /**
     * @return the adjClose
     */
    public double[] getAdjClose() {
        return Doubles.toArray(adjClose);
    }

    /**
     * @return the prices
     */
    public MWStructArray getPrices() {
        return prices;
    }
}


 class OptionData {
    private Double strike,last,change;
    private String symbol;
    private Integer voulme,openInt;
 

    public void priceToMatalab() {

        final String[] pricesFieldsNames = {"open", "high", "low", "close", "volume", "adjClose"};
        MWStructArray prices = new MWStructArray(1, 1, pricesFieldsNames);
 //	MWNumericArray openNumericArray = new MWNumericArray(molarArray, MWClassID.DOUBLE); 
	//double[][] openArray = new double[1][open.size()];

        for (int i = 0; i < 1; i++) {
            prices.set("open", i+1, open.get(i));
            prices.set("high", i+1, high.get(i));
            prices.set("low", i+1, low.get(i));
            prices.set("close", i+1, close.get(i));
            prices.set("volume", i+1, volume.get(i));
            prices.set("adjClose", i+1, adjClose.get(i));

        }

     // MWNumericArray openNumericArray = new MWNumericArray(openArray, MWClassID.DOUBLE); 
    }
}
