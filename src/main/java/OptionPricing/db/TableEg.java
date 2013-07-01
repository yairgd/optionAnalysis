package OptionPricing.db;

import java.io.IOException;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TableEg {

    private Hashtable<String, String> callList = new Hashtable<String, String>();
    private Hashtable<String, String> putList = new Hashtable<String, String>();

    public static void main(String[] args) {
        TableEg tableEg = new TableEg();
        tableEg.fetch("AMD");
    }

    public void fetch(String stock) {
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
}