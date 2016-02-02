/******************************************************************************/
/**
@file          DateGenerator.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  From numeric date generates text e.g. 02/02/2016 = 
* second of February two thousand sixteen.
*******************************************************************************/
package JERVIS;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateGenerator {
    
    /* class variable declarations */
    private static String todayDate = "", readTodayDate = "";
    
    private static final String[] tensNames = {
        "",
        " ten",
        " twenty",
        " thirty"
    };
    
    private static final String[] ordninalTensNames = {
        "",
        " tenth",
        " twentieth",
        " thirtieth"
    };

    private static final String[] numNames = {
        "",
        " one",
        " two",
        " three",
        " four",
        " five",
        " six",
        " seven",
        " eight",
        " nine",
        " ten",
        " eleven",
        " twelve",
        " thirteen",
        " fourteen",
        " fifteen",
        " sixteen",
        " seventeen",
        " eighteen",
        " nineteen"
    };
    
        private static final String[] ordinalNumNames = {
        "",
        " first",
        " second",
        " third",
        " fourth",
        " fifth",
        " sixth",
        " seventh",
        " eightth",
        " nineth",
        " tenth",
        " eleventh",
        " twelveth",
        " thirteenth",
        " fourteenth",
        " fifteenth",
        " sixteenth",
        " seventeenth",
        " eighteenth",
        " nineteenth"
    };

    /*  initDateGenerator ************************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Initialises the class 
    ****************************************************************************/ 
    public static void initDateGenerator(){
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();        
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        todayDate = df.format(today);
    }
    
    /*  getTodayDate ***********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Returns today's date in numeric form e.g. 02/02/2016
     * @return 
    ***************************************************************************/
    public static String getTodayDate(){
        return todayDate;
    }

    /*  readTodayDate ***********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Returns today's date in the readable form
     * @return 
    ***************************************************************************/
    public static String readTodayDate(){
        
        if(readTodayDate.isEmpty()){

            String finalDate[] = todayDate.split("/");
            finalDate[1] = finalDate[1].replace("0", "");
            
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();

            char[] charTmpArray = finalDate[0].toCharArray();
            
            if(charTmpArray[1] == '0'){
                finalDate[0] = 
                    ordninalTensNames[Character.getNumericValue(charTmpArray[0])];
            } 
            else {
                
                if (Integer.parseInt(finalDate[0]) <= 19){
                    finalDate[0] =
                        ordinalNumNames[Character.getNumericValue(charTmpArray[1])];
                } 
                else {
                    finalDate[0] = 
                        tensNames[Character.getNumericValue(charTmpArray[0])] +
                        " " +
                        ordinalNumNames[Character.getNumericValue(charTmpArray[1])];  
                }
            }
            
            int i = Integer.parseInt(finalDate[1]);

            if (i >= 1 && i <= 12 ) {
                finalDate[1] = months[i - 1];
            }
            
            readTodayDate = finalDate[0] + " of " + finalDate[1] + " " + finalDate[2];
        }
                
        return readTodayDate;
    }
}
