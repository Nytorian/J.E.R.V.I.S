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

import static JERVIS.Jervis.jervisSpeak;
import TextBase.SensitiveData;
import java.io.IOException;
import java.util.Random;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class InformationFinder {
    
    private static String[] sPlacesPostcodes;
    
    /*  initInformationFinder **************************************************
    **  02/02/2016  M.Michalski Initial Version
    *   09/02/2016  M.Michalski allocated hard coded source (brainyquote)
    ***************************************************************************/
    /**Description: Initialises initInformationFinder, required for full
     * functionality.
     * @throws java.io.IOException
    ***************************************************************************/
    public static void initInformationFinder() throws IOException{
        
        sPlacesPostcodes = NotepadWrapper.readNoteData("src/TextBase/postcodes.txt").split(",");
    }
    
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
                doc = Jsoup.connect(SensitiveData.sQuotes + key)
                          .userAgent("Mozilla")
                          .cookie("auth", "token")
                          .timeout(3000)
                          .get();
                resultText = doc.select("span[class=bqQuoteLink]").get(new Random().nextInt(3)).text();
            } catch (IOException e) {
            }
            return(resultText);
        }
        return "";
    }
    
    /*  definitionFinder *******************************************************
    **  03/02/2016  M.Michalski Initial Version
    *   09/02/2016  M.Michalski allocated hard coded source (brainyquote)
    ***************************************************************************/
    /**Description: changes the currentRecogniser - multi recogniser extention
     * @param sQuery
     * @return 
     * @throws java.lang.InterruptedException 
    ***************************************************************************/
    public static String definitionFinder(String sQuery) throws InterruptedException{
        
        if(sQuery != null){
            Document document;
            String resultText = "";

            try {
                document = Jsoup.connect(SensitiveData.sDefinitions + sQuery)
                          .userAgent("Mozilla")
                          .cookie("auth", "token")
                          .timeout(3000)
                          .get();
                resultText = document.select("p").first().text();
                System.out.println(resultText);
                
                jervisSpeak(resultText);
                
                if(resultText.contains("may refer to")){
                    Elements liElements = document.select("li:not([class])");
                    
                    int i = 0; 
                    for(Element element : liElements){
                        if(i > 4)
                            break;
                        resultText = element.text().toLowerCase().replaceAll("\\[()", "").replaceAll("\\]/", "");
                        jervisSpeak(resultText);
                        Thread.sleep(400);//Pause between the list
                        i++;
                    }
                }
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
     * @param sPlace
     * @return 
     * @throws java.io.IOException 
    ***************************************************************************/
    public static String weatherForecast(String sPlace) throws IOException{
        
        Document doc = null;
        String resultText = "";
        boolean bForecastFound = false;
        
        for(int i = 0; i < sPlacesPostcodes.length; i++){
            if(sPlacesPostcodes[i].contains(sPlace.replace(" ", ""))){
                doc = Jsoup.connect(SensitiveData.sWeather + sPlacesPostcodes[i + 0x1])
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(3000)
                .get();
                
                bForecastFound = true;
            }
        }
        
        if(doc != null){
            resultText  = doc.select("p.media-heading").text();
            resultText += doc.select("p.temperature").text();
        }
        
        if(!bForecastFound){
            jervisSpeak("Unfortunately I cannot find anything, please make sure the post code was given to me");
        }
        
        return(resultText);
    }
}
