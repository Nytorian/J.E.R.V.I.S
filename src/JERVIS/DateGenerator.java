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

import TextBase.DataTables;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateGenerator {
    
    /* class variable declarations */
    private static String todayDate = "", readTodayDate = "";
    
    private static String[] tmpDate;
 
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
        System.out.println(todayDate);
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
                     
            System.out.println(Integer.parseInt(finalDate[0]));
            finalDate[0] = 
                    DataTables.dateDays[0][Integer.parseInt(finalDate[0])];
            
            int i = Integer.parseInt(finalDate[1]);

            if (i >= 1 && i <= 12 ) {
                finalDate[1] = months[i - 1];
            }
            
            readTodayDate = finalDate[0] + " of " + finalDate[1] + " " + finalDate[2];
        }
                
        return readTodayDate;
    }
    
    /*  setEvent ***************************************************************
    **  27/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Produces numeric date from text
     * @param sDaysMonthsText
     * @param sYearText
     * @param sHourText
    ***************************************************************************/
    public static String setEvent(String sDaysMonthsText,
            String sYearText, String sHourText){
        
        String sDateNumeric;
        String sTimeNumeric;
        
        sDateNumeric  = daysMonthsToNumeric(sDaysMonthsText);
        sDateNumeric += yearToNumeric(sYearText);
        sTimeNumeric = timeToNumeric(sHourText);
        
        return sDateNumeric + " " + sTimeNumeric;
    }
    
    /*  daysMonthsToNumeric ****************************************************
    **  27/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Produces numeric date of days and months from text
     * @param sDaysMonthsText
     * @return 
    ***************************************************************************/
    public static String daysMonthsToNumeric(String sDaysMonthsText){
       
        String[] sTmpDate = sDaysMonthsText.replace(" ", "").split("of");
        String sDateNumeric = "";

        for(int i = 0; i <= DataTables.dateDays[0].length - 1; i++){
            if(DataTables.dateDays[0][i].equals(sTmpDate[0])){
                sDateNumeric = DataTables.dateDays[1][i] + "/";
            }
        }
        
        for(int i = 0; i <= DataTables.dateMonths[0].length - 1; i++){
            if(DataTables.dateMonths[0][i].equals(sTmpDate[1])){
                sDateNumeric += DataTables.dateMonths[1][i] + "/";
            }
        }
    
        return sDateNumeric;
    }
    
    /*  yearToNumeric **********************************************************
    **  27/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Produces numeric year
     * @param sYearText from text
     * @return 
    ***************************************************************************/
    public static String yearToNumeric(String sYearText){
        
        String sYearNumeric = "";
        
        for(int i = 0; i <= DataTables.dateYear[0].length - 1; i++){
            if(DataTables.dateYear[0][i].equals(sYearText)){
                sYearNumeric = DataTables.dateYear[1][i];
            }
        }
    
        return sYearNumeric;
    }
    
    /*  timeToNumeric **********************************************************
    **  27/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Produces numeric time from text
     * @param sTimeText
     * @return 
    ***************************************************************************/
    public static String timeToNumeric(String sTimeText){
       
        String[] sTmpTime = sTimeText.split(" ");
        String sTmpHours;
        String sTmpMinutes;
        String sTimeNumeric = "";
        
        if(sTmpTime.length > 3){
            sTmpHours = sTmpTime[0] + " " + sTmpTime[1];
            sTmpMinutes = sTmpTime[2] + " " + sTmpTime[3];
        }
        else if(sTmpTime.length > 2){
            sTmpHours = sTmpTime[0] + " ";
            sTmpMinutes = sTmpTime[1] + " " + sTmpTime[2];
        }
        else{
            sTmpHours = sTmpTime[0] + " ";
            sTmpMinutes = sTmpTime[1];
        }
       
        for(int i = 0; i <= DataTables.dateTime[0].length - 1; i++){
            if(DataTables.dateTime[0][i].equals(sTmpHours)){
                sTimeNumeric = DataTables.dateTime[1][i] + ":";
            }
        }
        
        for(int i = 0; i <= DataTables.dateTime[0].length - 1; i++){
            if(DataTables.dateTime[0][i].equals(sTmpMinutes)){
                sTimeNumeric += DataTables.dateTime[1][i];
            }
        }
    
        return sTimeNumeric;
    }
}
