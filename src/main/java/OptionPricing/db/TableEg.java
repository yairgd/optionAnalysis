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
         Elements callOptionTable = doc.select("table.yfnc_datamodoutline1");
     
         Elements tableElements = callOptionTable.select("table.yfnc_datamodoutline1");//callOptionTable.select("table.yfnc_datamodoutline1") ;//doc.select("table.yfnc_datamodoutline1");

         Elements tableHeaderEles = tableElements.select("thead tr th");
         System.out.println("headers");
         for (int i = 0; i < tableHeaderEles.size(); i++) {
            System.out.println(tableHeaderEles.get(i).text());
         }
         System.out.println();

         Elements tableRowElements = tableElements.select(":not(thead) tr");

         for (int i = 0; i < tableRowElements.size(); i++) {
             System.out.print("row #"+i+"\n--------\n");
            Element row = tableRowElements.get(i);
            Elements rowItems = row.select("td");
            for (int j = 1; j < rowItems.size(); j++) {
               System.out.println(rowItems.get(j).text());
            }
            System.out.println();
            System.out.println();
         }

      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}