/******************************************************************************/
/**
@file          Organiser.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Implements the organiser thread running in the background.
*******************************************************************************/
package JERVIS;

import static JERVIS.Jervis.serialOutput;
import java.io.FileInputStream;
import TextBase.Organiser.Event;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Organiser implements Runnable{
    
    public static Event events;
    public static String sCurrentYear;
    public static String sCurrentDayMonth;
    
    /* collectEvents ***********************************************************
    ** 29/03/2015  M.Michalski Initial Version
    ***************************************************************************/
    /** Description: Collects events due within 24h
    ***************************************************************************/
    public static void collectEvents(){
        Calendar now = Calendar.getInstance();
        sCurrentYear = Integer.toString(now.get(Calendar.YEAR));
        
        System.out.println("sCurrentYear: " + sCurrentYear);//debug
        
        String sMonth = Integer.toString(now.get(Calendar.MONTH) + 1); // Note: zero based!
        String sDay = Integer.toString(now.get(Calendar.DAY_OF_MONTH));
        
        sCurrentDayMonth = sDay + "/" + sMonth;
        
        System.out.println("sCurrentDayMonth: " + sCurrentDayMonth);//debug
        
        try {
            events = TextBase.Organiser.Event.parseFrom(new FileInputStream("Organiser.ser"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Organiser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Organiser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int i = 0; 
        
        for (String year : events.getYearList()) {
            if(year.equals(sCurrentYear)){
                if(events.getDayMonth(i).equals(sCurrentDayMonth)){
                    
                    DueEvents.setEvent(events.getTitle(i), events.getDayMonth(i), 
                        events.getYear(i), events.getTime(i), events.getTimeToRemind(i));
                }
            } i++;
        }
    }
    
    /* run *********************************************************************
    ** 29/03/2015  M.Michalski Initial Version
    ***************************************************************************/
    /** Description: Task for the Organiser thread
    ***************************************************************************/
    @Override
    public void run(){
        
        collectEvents();
        
        while(true){
            String sCurrentTime = DateGenerator.getCurrentTime();
            
            if(sCurrentTime.equals("24:1")){
                collectEvents();
            }
            
            for(int index = 0; index < DueEvents.getDueEventsLength(); index++){
                
                if(!DueEvents.getTime(index).equals(" ") ||
                        !DueEvents.getTimeToRemind(index).equals(" ")){
                    
                    String sTimeToRemind = DateGenerator.subTime(
                            DueEvents.getTime(index),
                            DueEvents.getTimeToRemind(index)
                    );
                    
                    if(DateGenerator.checkDueEvents(sTimeToRemind, sCurrentTime) 
                            == DateGenerator.EventCode.eEVENT_DUE){
                        
                        Jervis.jervisSpeak("The event " + DueEvents.getTitle(index) 
                                + " is due in " + DueEvents.getTimeToRemind(index) + " minutes" );
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Organiser.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    else if(DateGenerator.checkDueEvents(DueEvents.getTime(index), sCurrentTime) 
                            == DateGenerator.EventCode.eEVENT_PAST_DUE){
                        
                        Jervis.jervisSpeak("The event " + DueEvents.getTitle(index) 
                                + " was due at " + DueEvents.getTime(index) );
                        
                        DueEvents.removeEvent(index);
                        
                        try {
                        Event editedEvent = Event.newBuilder()
                                    .mergeFrom(new FileInputStream("Organiser.ser"))
                                    .setTitle(index, "x")
                                    .setDayMonth(index, "x")
                                    .setYear(index, "x")
                                    .setTime(index, "x")
                                    .setTimeToRemind(index, "x")
                                    .build();
                            
                            Jervis.serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("Organiser.ser");
                                    editedEvent.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    Jervis.serialOutputLock.unlock();
                                }
                            
                        } catch (IOException ex) {
                            Logger.getLogger(Organiser.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Organiser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
}

