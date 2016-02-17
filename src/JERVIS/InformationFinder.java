/******************************************************************************/
/**
@file          InformationFinder.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Information finder for Jervis.
*******************************************************************************/
package JERVIS;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class InformationFinder {
    
    public enum Period { eOUTLOOK, eTODAY, eTOMORROW, eTwoDays };
    public enum Place { ePORTSMOUTH, eLONDON, eSOUTHAMPTON };
    
    /*  quotesFinder **********************************************************
    **  02/02/2016  M.Michalski Initial Version
    *   09/02/2016  M.Michalski allocated hard coded source (brainyquote)
    ***************************************************************************/
    /**Description: changes the currentRecogniser - multi recogniser extention
     * @param key
     * @return 
    ***************************************************************************/
    public static String quotesFinder(String key){
        
        if(key != null){
            Document doc;
            String resultText = "";

            try {
                doc = Jsoup.connect("http://www.brainyquote.com/search_results.html?q="+ key)
                          .userAgent("Mozilla")
                          .cookie("auth", "token")
                          .timeout(3000)
                          .get();
                resultText = doc.select("span[class=bqQuoteLink]").first().text();
            } catch (IOException e) {
            }
            return(resultText);
        }
        return "";
    }
    
    /*  weatherForecast ********************************************************
    **  09/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: changes the currentRecogniser - multi recogniser extention
     * @param ePlace
     * @param ePeriod
     * @return 
     * @throws java.io.IOException 
    ***************************************************************************/
    public static String weatherForecast(Place ePlace, Period ePeriod) throws IOException{
        
        Document doc;
        String resultText = "";
        
        //http://www.worldweatheronline.com/portsmouth-weather/hampshire/gb.aspx
        
        if(ePeriod == Period.eOUTLOOK){
            doc = Jsoup.connect("http://www.worldweatheronline.com/portsmouth-weather/hampshire/gb.aspx")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
        }
        else{
            doc = Jsoup.connect("http://www.worldweatheronline.com/portsmouth-weather/hampshire/gb.aspx?day="+ ePeriod)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
        }
        
        resultText  = doc.select("p.media-heading").text();
        resultText += doc.select("p.temperature").text();
        
        return(resultText);
    }
}
