package OptionPricing.db;

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
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataFetch {

    private Hashtable<String, String> callList = new Hashtable<String, String>();
    private Hashtable<String, String> putList = new Hashtable<String, String>();
    private ArrayList<Double> open = new ArrayList<Double>();
    private String stock;

    public static void main(String[] args) {
        /*
         DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
         Date startDate = df.parse(startDate);
         */




        DataFetch tableEg = new DataFetch("AMD");
        // tableEg.fetchStock("06/27/2007","06/27/2010");
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
                        System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
                        if (is_call == true) {
                            callList.put(tds.get(1).text(), tds.get(0).text());
                        } else {
                            putList.put(tds.get(1).text(), tds.get(0).text());
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
            DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
            Date startDate = df.parse(date1);
            Date endDate = df.parse(date2);
            try {
                //try {
                Reader reader = null;
                InputStream input = null;
                input = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + stock + "&d="+startDate.getDay()+"&e=30&f=2013&g=d&a=2&b=21&c=1983&ignore=.csv").openStream();
                reader = new InputStreamReader(input, "UTF-8");
                BufferedReader brReadMe = new BufferedReader(reader);
                String strLine = brReadMe.readLine();
                strLine = brReadMe.readLine();
                //for all lines
                while (strLine != null) {
                    try {
                        //if line contains "(see also"
                        String lineEle[] = strLine.split(",");
                        getOpen().add(Double.parseDouble(lineEle[1]));


                        //   if (strLine.toLowerCase().contains("(see also")) {
                        //       //write line from "(see also" to ")"
                        //       int iBegin = strLine.toLowerCase().indexOf("(see also");
                        //       String strTemp = strLine.substring(iBegin);
                        //       int iLittleEnd = strTemp.indexOf(")");
                        //       System.out.println(strLine.substring(iBegin, iBegin + iLittleEnd));
                        //   }

                        //update line
                        strLine = brReadMe.readLine();
                    } //end for
                    catch (IOException ex) {
                        Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } //end for
                reader.close();
                //
            } catch (IOException ex) {
                Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException ex) {
            Logger.getLogger(DataFetch.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

    /**
     * @return the callList
     */
    public Hashtable<String, String> getCallList() {
        return callList;
    }

    /**
     * @return the putList
     */
    public Hashtable<String, String> getPutList() {
        return putList;
    }

    /**
     * @return the open
     */
    public ArrayList<Double> getOpen() {
        return open;
    }

    /**
     * @return the stock
     */
    public String getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(String stock) {
        this.stock = stock;
    }
}
