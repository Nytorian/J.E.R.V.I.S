/******************************************************************************/
/**
@file          DueEvents.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Stores events that are due within 24h from the current time
*******************************************************************************/
package JERVIS;

import java.util.ArrayList;
import java.util.List;


public class DueEvents {
    private static List<String> title = new ArrayList<>();
    private static List<String> dayMonth = new ArrayList<>();
    private static List<String> year = new ArrayList<>();
    private static List<String> time = new ArrayList<>();
    private static List<String> timeToRemind = new ArrayList<>();
    private static int iSize = 0;
    
    /*  setEvent ***************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds every parameter of an event to cache.
     * @param sTitle
     * @param sDayMonth
     * @param sYear
     * @param sTime
     * @param sTimeToRemind
    ***************************************************************************/
    public static synchronized void setEvent(String sTitle, String sDayMonth, 
            String sYear, String sTime, String sTimeToRemind){
        
        addTitle(sTitle);
        addDayMonth(sDayMonth);
        addYear(sYear);
        addTime(sTime);
        addTimeToRemind(sTimeToRemind);
    }
    
    /*  addTitle ***************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds title to cache.
     * @param sTitle
    ***************************************************************************/
    public static synchronized void addTitle(String sTitle){
        DueEvents.title.add(sTitle);
        iSize++;
    }
    
    /*  getTitle ***************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: gets title from cache.
     * @param iIntex
     * @return 
    ***************************************************************************/
    public static synchronized String getTitle(int iIntex){
        return title.get(iIntex);
    }
    
    /*  addDayMonth ************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds month to cache.
     * @param sDayMonth
    ***************************************************************************/
    public static synchronized void addDayMonth(String sDayMonth){
        dayMonth.add(sDayMonth);
    }
    
    /*  getDayMonth ************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: gets day and month from cache.
     * @param iIntex
     * @return 
    ***************************************************************************/
    public static synchronized String getDayMonth(int iIntex){
        return dayMonth.get(iIntex);
    }
    
    /*  addYear ****************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds year to cache.
     * @param sYear
    ***************************************************************************/
    public static synchronized void addYear(String sYear){
        year.add(sYear);
    }
    
    /*  getYear ****************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: gets year from cache.
     * @param iIntex
     * @return 
    ***************************************************************************/
    public static synchronized String getYear(int iIntex){
        return year.get(iIntex);
    }
    
    /*  addTime ****************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds time to cache.
     * @param sTime
    ***************************************************************************/
    public static synchronized void addTime(String sTime){
        time.add(sTime);
    }
    
    /*  getTime ****************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: gets year from cache.
     * @param iIntex
     * @return 
    ***************************************************************************/
    public static synchronized String getTime(int iIntex){
        return time.get(iIntex);
    }
    
    /*  addTimeToRemind ********************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds time to remind to cache.
     * @param sTimeToRemind
    ***************************************************************************/
    public static synchronized void addTimeToRemind(String sTimeToRemind){
        timeToRemind.add(sTimeToRemind);
    }
    
    /*  getTimeToRemind ********************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: adds time to remind to cache.
     * @param iIntex
     * @return 
    ***************************************************************************/
    public static synchronized String getTimeToRemind(int iIntex){
        return timeToRemind.get(iIntex);
    }
    
    /*  getDueEventsLength *****************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: return size of the lists that store parameters of the events.
     * @return 
    ***************************************************************************/
    public static synchronized int getDueEventsLength(){
        
        //Fix to null pointer - increased int with new addition
        return iSize;
    }
    
    /*  removeEvent ************************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: removes all parameters of the specified by index event
     * @param index
    ***************************************************************************/
    public static synchronized void removeEvent(int index){
        title.remove(index);
        dayMonth.remove(index);
        year.remove(index);
        time.remove(index);
        timeToRemind.remove(index);
        iSize--;
    }
    
    /*  todaySchedule **********************************************************
    **  29/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: retrieves today's schedule
    ***************************************************************************/
    public static synchronized void todaySchedule(){
        boolean bEventsDue = false;
        
        Jervis.jervisSpeak("Today's schedule is the following, sir: ");
        
        for(int i = 0; i < getDueEventsLength(); i++){
            Jervis.jervisSpeak(getTitle(i));
            Jervis.jervisSpeak("at, " + getTime(i));
            if(!getTitle(i).isEmpty()){
                bEventsDue = true;
            }
        }
        
        if(!bEventsDue){
            Jervis.jervisSpeak("Actually no events have been scheduled for today, sir");
        }
    }
}
