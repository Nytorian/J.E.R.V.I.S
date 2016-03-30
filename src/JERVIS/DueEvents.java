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
    private static List<String> dayMonth = new ArrayList<>();;
    private static List<String> year = new ArrayList<>();;
    private static List<String> time = new ArrayList<>();;
    private static List<String> timeToRemind = new ArrayList<>();;
    private static int iSize = 0;
    
    public static void initDueEvents(){
        //init empty list - avoid null pointer
        setEvent(" ", " ", " ", " ", " ");
    }
    
    public static synchronized void setEvent(String sTitle, String sDayMonth, 
            String sYear, String sTime, String sTimeToRemind){
        
        addTitle(sTitle);
        addDayMonth(sDayMonth);
        addYear(sYear);
        addTime(sTime);
        addTimeToRemind(sTimeToRemind);
    }
    
    public static synchronized void addTitle(String sTitle){
        DueEvents.title.add(sTitle);
        iSize++;
    }
    
    public static synchronized String getTitle(int iIntex){
        return title.get(iIntex);
    }
    
    public static synchronized void addDayMonth(String sDayMonth){
        dayMonth.add(sDayMonth);
    }
    
    public static synchronized String getDayMonth(int iIntex){
        return dayMonth.get(iIntex);
    }
    
    public static synchronized void addYear(String sYear){
        year.add(sYear);
    }
    
    public static synchronized String getYear(int iIntex){
        return year.get(iIntex);
    }
    
    public static synchronized void addTime(String sTime){
        time.add(sTime);
    }
    
    public static synchronized String getTime(int iIntex){
        return time.get(iIntex);
    }
    
    public static synchronized void addTimeToRemind(String sTimeToRemind){
        timeToRemind.add(sTimeToRemind);
    }
    
    public static synchronized String getTimeToRemind(int iIntex){
        return timeToRemind.get(iIntex);
    }
    
    public static synchronized int getDueEventsLength(){
        
        //Fix to null pointer - increased int with new addition
        return iSize;
    }
}
