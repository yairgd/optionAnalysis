package OptionPricing.db;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TableEg {

    public static void main(String[] args) {
        //String html = "http://publib.boulder.ibm.com/infocenter/iadthelp/v7r1/topic/" +
        //    "com.ibm.etools.iseries.toolbox.doc/htmtblex.htm";

        String html = "http://finance.yahoo.com/q/op?s=AMD+Options";
        try {


            Document doc = Jsoup.connect(html).get();

            for (Element table : doc.select("table[class=yfnc_datamodoutline1]")) {
                

                Elements tableHeaderEles = table.select("th");
                System.out.println("headers");
                for (int i = 0; i < tableHeaderEles.size(); i++) {
                    System.out.println(tableHeaderEles.get(i).text());
                }

                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.size() > 6) {
                        System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
                    }
                }
            }

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}