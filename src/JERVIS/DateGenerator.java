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
    public static enum EventCode { eNO_EVENT_DUE, eEVENT_DUE, eEVENT_PAST_DUE };
 
    /*  initDateGenerator ******************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Initialises the DateGenerator class 
    ***************************************************************************/ 
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

    /*  readTodayDate **********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Returns today's date as standard date format
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
                sDateNumeric += DataTables.dateMonths[1][i];
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
       
        String[] sTmpTime = sTimeText.split(" next ");
        String sTmpHours;
        String sTmpMinutes;
        String sTimeNumeric = "";
        
        sTmpHours = sTmpTime[0];
        sTmpMinutes = sTmpTime[1];
       
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
    
    /*  minuteToNumeric ********************************************************
    **  27/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Produces numeric minutes from text
     * @param sMinutesText
     * @return 
    ***************************************************************************/
    public static String minuteToNumeric(String sMinutesText){
       
        String sMinutesNumeric = "";
        
        for(int i = 0; i <= DataTables.dateTime[0].length - 1; i++){
            if(DataTables.dateTime[0][i].equals(sMinutesText)){
                sMinutesNumeric = DataTables.dateTime[1][i];
            }
        }
    
        return sMinutesNumeric;
    }
    
    /*  subTime ****************************************************************
    **  30/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Subtracts minutes from time passed as parameter.
     * @param sTime
     * @param sMinutes
     * @return 
    ***************************************************************************/
    public static String subTime(String sTime, String sMinutes){
       
        String sTimeProcessed = "", sXhour = "", sXminute = "";
        int iMinutes, iTimeMinutes, iTimeHours, iTimeProcessed;
        
        String[] tmpTime = sTime.split(":");
        
        iMinutes = Integer.parseInt(sMinutes);
        iTimeMinutes = Integer.parseInt(tmpTime[1]);
        
        if(iTimeMinutes >= iMinutes){
            iTimeProcessed = iTimeMinutes - iMinutes;
            
            if(Integer.parseInt(tmpTime[0]) < 10)
                sXhour = "0";
            if(iTimeProcessed < 10)
                sXminute = "0";
            
            sTimeProcessed = sXhour + tmpTime[0] + ":" + sXminute + iTimeProcessed;
        }
        else{
            iTimeHours = Integer.parseInt(tmpTime[0]);
            iTimeHours -= 1;
            iTimeMinutes += 60;
            iTimeProcessed = iTimeMinutes - iMinutes;
            
            if(iTimeHours < 10)
                sXhour = "0";
            if(iTimeProcessed < 10)
                sXminute = "0";
            
            sTimeProcessed = sXhour + iTimeHours + ":" + sXminute + iTimeProcessed;
        }
     
        return sTimeProcessed;
    }
    /*  checkDueEvents **********************************************************
    **  02/04/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Checks if any events are due or past due
     * @param sScheduleTime
     * @param sCurrentTime
     * @return 
    ***************************************************************************/
    public static EventCode checkDueEvents(String sScheduleTime, String sCurrentTime){
       
        EventCode eDueEvent = EventCode.eNO_EVENT_DUE;
        int iScheduleHours, iScheduleMinutes, iCurrentHours, iCurrentMinutes;
        
        String[] tmpScheduleTime = sScheduleTime.split(":");
        
        iScheduleHours = Integer.parseInt(tmpScheduleTime[0]);
        iScheduleMinutes = Integer.parseInt(tmpScheduleTime[1]);
        
        String[] tmpCurrentTime = sCurrentTime.split(":");
        
        iCurrentHours = Integer.parseInt(tmpCurrentTime[0]);
        iCurrentMinutes = Integer.parseInt(tmpCurrentTime[1]);
        
        if(iScheduleHours < iCurrentHours){
            eDueEvent = EventCode.eNO_EVENT_DUE;
        }
        else if((iScheduleHours == iCurrentHours) && (iScheduleMinutes < iCurrentMinutes)){
            eDueEvent = EventCode.eEVENT_PAST_DUE;
        }
        else if((iScheduleHours == iCurrentHours) && (iScheduleMinutes == iCurrentMinutes)){
            eDueEvent = EventCode.eEVENT_DUE;
        }
     
        return eDueEvent;
    }
    /*  getCurrentTime *********************************************************
    **  04/04/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: return current time in numeric form
     * @return 
    ***************************************************************************/
    public synchronized static String getCurrentTime(){
        Calendar now = Calendar.getInstance();
        String sHour = Integer.toString(now.get(Calendar.HOUR_OF_DAY));
        String sMinute = Integer.toString(now.get(Calendar.MINUTE));
        
        if(sHour.equals("0"))
            sHour = "24";
        
        return sHour + ":" + sMinute;
    }
}
