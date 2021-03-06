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
import java.text.DateFormatSymbols;
import java.util.Collections;


public class DataFetch {

    private String stock;
    private StockPrice stockPrice = new StockPrice();
    // private ArrayList<OptionPrice> putOptionList = new ArrayList<OptionPrice>();
    // private ArrayList<OptionPrice> callOptionList = new ArrayList<OptionPrice>();
    private ArrayList<OptionMonth> optionMonthList = new ArrayList<OptionMonth>();

    public static void main(String[] args) throws ParseException {
        /*
         DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
         Date startDate = df.parse(startDate);
         */



        DataFetch tableEg = new DataFetch("AMD");
        tableEg.fetchStock("27/6/2007", "7/10/2012");
        
        tableEg.fetchOptionData(2013, 6);
        tableEg.fetchOptionData(2013, 7);
        double[] d = tableEg.getStockPrice().getClose();
    }

    public DataFetch(String _stock) {
        stock = _stock;
    }

    public void fetchOptionData(double year, double month) {
        //String html = "http://publib.boulder.ibm.com/infocenter/iadthelp/v7r1/topic/" +
        //    "com.ibm.etools.iseries.toolbox.doc/htmtblex.htm";
  
        OptionMonth optionMonth = new OptionMonth(stock,(int) year, (int) month, 22);

        getOptionMonthList().add(optionMonth);

        String html = "http://finance.yahoo.com/q/op?s=" + stock + "&m=" + year + "-" + (month+1);
        try {

            boolean is_call = true;
            Document doc = Jsoup.connect(html).get();

            for (Element table : doc.select("table[class=yfnc_datamodoutline1]")) {
                Elements tableHeaderEles = table.select("th");
                //System.out.println("headers");
                for (int i = 0; i < tableHeaderEles.size(); i++) {
                    // System.out.println(tableHeaderEles.get(i).text());
                }

                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.size() == 8) {
                        OptionPrice optionPrice = new OptionPrice();
                        optionPrice.setSybol(tds.get(1).text());
                        optionPrice.setStrike(Double.parseDouble(tds.get(0).text()));
                        optionPrice.setLast(Double.parseDouble(tds.get(2).text()));
                        optionPrice.setChange(Double.parseDouble(tds.get(3).text()));
                        try {
                            optionPrice.setBid(Double.parseDouble(tds.get(4).text()));
                        } catch (Exception e) {
                            optionPrice.setBid(0.0);
                        }
                        try {
                            optionPrice.setAsk(Double.parseDouble(tds.get(5).text()));
                        } catch (Exception e) {
                            optionPrice.setAsk(0.0);
                        }
                        try {
                            optionPrice.setVolume(Integer.parseInt(tds.get(6).text().replace(",", "")));
                        } catch (Exception e) {
                            optionPrice.setVolume(0);
                        }
                        try {
                            optionPrice.setOpenInt(Integer.parseInt(tds.get(7).text().replace(",", "")));
                        } catch (Exception e) {
                            optionPrice.setOpenInt(0);
                        }
                        //System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
                        if (is_call == true) {
                            //    getCallOptionList().add(optionPrice);
                            optionMonth.getOptionCallList().add(optionPrice);
                        } else {
                            //  getPutOptionList().add(optionPrice);
                            optionMonth.getOptionPutList().add(optionPrice);
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
                String http = "http://ichart.finance.yahoo.com/table.csv?s=" + stock + "&a=" + startDate.get(Calendar.MONTH) + "&b=" + startDate.get(Calendar.DAY_OF_MONTH) + "&c=" + startDate.get(Calendar.YEAR) + "&d=" + endDate.get(Calendar.MONTH) + "&e=" + endDate.get(Calendar.DAY_OF_MONTH) + "&f=" + endDate.get(Calendar.YEAR) + "&g=d&ignore=.csv";
                input = new URL(http).openStream();
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
                Collections.reverse(getStockPrice().open);
                Collections.reverse(getStockPrice().high);
                Collections.reverse(getStockPrice().low);
                Collections.reverse(getStockPrice().close);
                Collections.reverse(getStockPrice().volume);
                Collections.reverse(getStockPrice().adjClose);

                //    priceToMatalab();
                //
            } catch (IOException ex) {
                Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException ex) {
            Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

    // public MWStructArray getOptionCallListToMatlab() {
    //    return createOptionPriceStruct(callOptionList);
    // }
    // public MWStructArray getOptionPutListToMatlab() {
    //    return createOptionPriceStruct(putOptionList);
    // }
    private MWStructArray createOptionPriceStruct(ArrayList<OptionPrice> optionPriceArray) {

        int i = 1;
        final String[] pricesFieldsNames = {"symbol", "strike", "last", "change", "bid", "ask", "volume", "openInt"};
        MWStructArray matlabOptionPrice = new MWStructArray(optionPriceArray.size(), 1, pricesFieldsNames);


        for (OptionPrice optionPrice : optionPriceArray) {
            matlabOptionPrice.set("symbol", i, optionPrice.getSybol());
            matlabOptionPrice.set("strike", i, optionPrice.getStrike());
            matlabOptionPrice.set("last", i, optionPrice.getLast());
            matlabOptionPrice.set("change", i, optionPrice.getChange());
            matlabOptionPrice.set("bid", i, optionPrice.getBid());
            matlabOptionPrice.set("ask", i, optionPrice.getAsk());
            matlabOptionPrice.set("openInt", i, optionPrice.getOpenInt());
            matlabOptionPrice.set("volume", i, optionPrice.getVolume());
            i = i + 1;
        }
        return matlabOptionPrice;
        // MWNumericArray openNumericArray = new MWNumericArray(openArray, MWClassID.DOUBLE); 
    }

    /**
     * @return the putOptionList
     */
    // public ArrayList<OptionPrice> getPutOptionList() {
    //     return putOptionList;
    //}
    /**
     * @return the callOptionList
     */
    // public ArrayList<OptionPrice> getCallOptionList() {
    //     return callOptionList;
    // }
    /**
     * @param callOptionList the callOptionList to set
     */
    //public void setCallOptionList(ArrayList<OptionPrice> callOptionList) {
    //    this.callOptionList = callOptionList;
    // }
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

    /**
     * @return the optionMonthList
     */
    public ArrayList<OptionMonth> getOptionMonthList() {
        return optionMonthList;
    }

    /**
     * @param optionMonthList the optionMonthList to set
     */
    public void setOptionMonthList(ArrayList<OptionMonth> optionMonthList) {
        this.optionMonthList = optionMonthList;
    }
}
class OptionPrice {

    private String symbol;
    private Double strike, last, change, bid, ask;
    private Integer volume, openInt;

    /**
     * @return the sybol
     */
    public String getSybol() {
        return symbol;
    }

    /**
     * @param sybol the sybol to set
     */
    public void setSybol(String symbol) {
        this.symbol = symbol;
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
}

class OptionMonth {

    private ArrayList<OptionPrice> optionPutList = new ArrayList<OptionPrice>();
    private ArrayList<OptionPrice> optionCallList = new ArrayList<OptionPrice>();
    private String month;
    private Integer year;
    private Integer day_at_month;
    private String symbol;
    
    OptionMonth(String _symbol, int _year, int _month, int _day_at_month) {
        month = new DateFormatSymbols().getMonths()[_month];
        year = _year;
        day_at_month = _day_at_month;
        symbol = _symbol;
    }

    private MWStructArray createOptionPriceStruct() {

        int i ;
        final String[] pricesFieldsNames = {"strike", "last", "change", "bid", "ask", "volume", "openInt"};
        MWStructArray matlabOptionPrice ;//= new MWStructArray(optionPriceArray.size(), 1, pricesFieldsNames);


        final String[] pricesFieldsNames1 = {"symbol", "month", "calls", "puts"};
        
        
         MWStructArray optionStruct = new MWStructArray(1, 1, pricesFieldsNames1);
         optionStruct.set("symbol", 1, symbol);
         optionStruct.set("month", 1, month);
         
         // add put list
        i = 1;
        matlabOptionPrice = new MWStructArray(optionPutList.size(), 1, pricesFieldsNames);
        for (OptionPrice optionPrice : optionPutList) {
          //  matlabOptionPrice.set("symbol", i, optionPrice.getSybol());
            matlabOptionPrice.set("strike", i, optionPrice.getStrike());
            matlabOptionPrice.set("last", i, optionPrice.getLast());
            matlabOptionPrice.set("change", i, optionPrice.getChange());
            matlabOptionPrice.set("bid", i, optionPrice.getBid());
            matlabOptionPrice.set("ask", i, optionPrice.getAsk());
            matlabOptionPrice.set("openInt", i, optionPrice.getOpenInt());
            matlabOptionPrice.set("volume", i, optionPrice.getVolume());
            i = i + 1;
        }
        optionStruct.set("puts", 1, matlabOptionPrice);
        
         // add put list
        i = 1;
        matlabOptionPrice = new MWStructArray(optionCallList.size(), 1, pricesFieldsNames);
        for (OptionPrice optionPrice : optionCallList) {
          //  matlabOptionPrice.set("symbol", i, optionPrice.getSybol());
            matlabOptionPrice.set("strike", i, optionPrice.getStrike());
            matlabOptionPrice.set("last", i, optionPrice.getLast());
            matlabOptionPrice.set("change", i, optionPrice.getChange());
            matlabOptionPrice.set("bid", i, optionPrice.getBid());
            matlabOptionPrice.set("ask", i, optionPrice.getAsk());
            matlabOptionPrice.set("openInt", i, optionPrice.getOpenInt());
            matlabOptionPrice.set("volume", i, optionPrice.getVolume());
            i = i + 1;
        }
        optionStruct.set("calls", 1, matlabOptionPrice);
         
         
         
        return optionStruct;
        // MWNumericArray openNumericArray = new MWNumericArray(openArray, MWClassID.DOUBLE); 
    }

    public void optionPutList(OptionPrice optionPrice) {
        getOptionPutList().add(optionPrice);
    }

    public void optionCallList(OptionPrice optionPrice) {
        getOptionCallList().add(optionPrice);
    }

    /**
     * @return the month
     */
    public String getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * @return the optionPutList
     */
    public ArrayList<OptionPrice> getOptionPutList() {
        return optionPutList;
    }

    /**
     * @return the optionCallList
     */
    public ArrayList<OptionPrice> getOptionCallList() {
        return optionCallList;
    }

    public MWStructArray getOptionMonthToMatlab() {
        return createOptionPriceStruct();
    }

 
}
