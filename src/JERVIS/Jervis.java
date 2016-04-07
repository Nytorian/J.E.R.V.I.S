/******************************************************************************/
/**
@file          Jervis.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Main file for Jervis.
*******************************************************************************/

package JERVIS;

import TextBase.JervisStorage.Owner;
import TextBase.CustomCmd.Command;
import java.io.IOException;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javax.sound.sampled.LineUnavailableException;
import static TextBase.SpeechRecognisers.*;
import static java.lang.System.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import TextBase.NoteLength;
import TextBase.Organiser.Event;
import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Jervis {
    
    public static boolean bInit = true;
    private static mainGUI mainFrame;
            
    private static final String Voice = "kevin16";
    private static VoiceManager vm;
    private static Voice voice;
    private static String utterance;
    
    private static Owner owner;
    private static Command command;
    private static Event events;
    private static boolean startAnimation = false;
    
    static NoteLength noteLength;
    
    public static SpeechRecogniser speechRecogniser;  
    public static FileOutputStream serialOutput;
    public static boolean bAnimationStart = false;
    public static boolean bListening = true;
    
    public static final Lock serialOutputLock = new ReentrantLock();
    
    private static final String[] sJervInit = {
        "Yes, sir?",
        "How can I help, sir?",
        "How can I assist you, sir?",
        "can I help you, sir?",
        "I am listening, sir"
    };
        
    /*  main *******************************************************************
    **  15/01/2016  M.Michalski Initial Version
    **  15/01/2016  M.Michalski Added GUI support
    **  25/01/2016  M.Michalski Added grammar recogniser and FreeTTS support
    **  27/01/2016  M.Michalski Added language model recogniser
    **  02/02/2016  M.Michalski Moved speech recognisers to its own class
    **  02/02/2016  M.Michalski Added what's the date query 
    **  09/02/2016  M.Michalski Chat support (quote finder) 
    **  09/02/2016  M.Michalski Weather forecast support
    **  16/02/2016  M.Michalski Note taking functionality added
    **  18/02/2016  M.Michalski Web browsing functionality
    **  21/02/2016  M.Michalski Learnng about the user
    **  28/03/2016  M.Michalski Organiser feature
    **  02/04/2016  M.Michalski Made Jervis's responses random
    ***************************************************************************/
    /**Description: Main function for Jervis
     * @throws java.io.IOException
     * @param args *  
     * @throws javax.sound.sampled.LineUnavailableException  
     * @throws java.lang.InterruptedException  
    ****************************************************************************/
    public static void main(String[] args) throws IOException,
            LineUnavailableException, InterruptedException {
        
        mainFrame = new mainGUI();
        
        speechRecogniser = new SpeechRecogniser();
        speechRecogniser.startRecognition();
        
        vm = VoiceManager.getInstance();
        voice = vm.getVoice(Voice);
        voice.allocate();
        
        ExecutorService execServise = Executors.newCachedThreadPool();
        execServise.execute(new WaveFormAnim());
        execServise.execute(new Organiser());

                        
        System.setProperty("mbrola.base", "D://J.E.R.V.I.S//Downloaded//mbrola//");     
        
        DateGenerator.initDateGenerator();
        InformationFinder.initInformationFinder();
        
        Translator enFrTranslator = new Translator("en", "fr");
        
        jervisSpeak("I am ready to serve you sir!");
        Thread.sleep(1000);
                
        while (true) {
            
            utterance = speechRecogniser.getResult();
            System.out.println(utterance);//debug
            Jervis.bAnimationStart = true;
            
            if(getListening()){
                if (utterance.equals("jervis")){

                    jervisSpeak(sJervInit[new Random().nextInt(sJervInit.length)]);

                    owner = Owner.parseFrom(new FileInputStream("JervisStorage.ser"));
                    command = Command.parseFrom(new FileInputStream("CustomCmd.ser"));
                    events = Event.parseFrom(new FileInputStream("Organiser.ser"));
                    Thread.sleep(200);

                    utterance = speechRecogniser.getResult();
                    
                    if(utterance.contains("stop listening")){
                        setListening(false);
                        mainFrame.chkListen.setSelected(false);
                    }
                    else if(utterance.contains("out of the way")){
                        mainFrame.setState(mainFrame.ICONIFIED);
                    }
                    else if(utterance.contains("come back")){
                        mainFrame.setState(mainFrame.NORMAL);
                    }
                    else if (utterance.contains("go away") ||
                        utterance.contains("you can go")){
                        jervisSpeak("Ok, I am gone, sir, Goodbye");
                        exit(0);
                    }
                    else if (utterance.contains("add weather places")){
                        jervisSpeak("On it sir");
                        weatherGUI wthrGUI = new weatherGUI();
                    }
                    else if (
                        utterance.contains("hello")||
                        utterance.contains("good") ||
                        utterance.startsWith("how are")){
  
                        if (utterance.contains("hello")){
                            jervisSpeak("Hello! Sir, how is life?");
                            actionConvers();
                        }
                        else if (utterance.contains("morning")){
                            jervisSpeak("Good morning! Sir, how is life?");
                            actionConvers();
                        }
                        else if (utterance.contains("afternoon")){
                            jervisSpeak("Good afternoon! Sir, how is life?");
                            actionConvers();
                        }
                        else if (utterance.contains("evening")){
                            jervisSpeak("Good evening! Sir, how is life?");
                            actionConvers();
                        }
                        else if (utterance.contains("how are")){
                            jervisSpeak("Always great, thank you. What about yourself?");
                            actionConvers();
                        }     
                    }
                    else if (utterance.contains("remember")) {
                        if(utterance.contains("my location")){
                            jervisSpeak("Please state your location followed by word city, sir");

                            speechRecogniser.stopRecognition();

                            String location = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                            
                            String[] processedLoc = location.toLowerCase().split(" city");

                            Owner editedOwner = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setLocation(processedLoc[0]) 
                                    .build();
                            
                            serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("JervisStorage.ser");
                                    editedOwner.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    serialOutputLock.unlock();
                                }

                            speechRecogniser.startRecognition(); 
                        }
                        else if(utterance.contains("my email")){
                            jervisSpeak("Please state your email sir");

                            speechRecogniser.stopRecognition();

                            String email = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedOwner = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setEmail(email) 
                                    .build();
                            
                            serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("JervisStorage.ser");
                                    editedOwner.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    serialOutputLock.unlock();
                                }

                            speechRecogniser.startRecognition(); 
                        }
                        else if(utterance.contains("my profession")){
                            jervisSpeak("Please state your profession sir");

                            speechRecogniser.stopRecognition();

                            String profession = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedOwner = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setProfession(profession) 
                                    .build();

                            serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("JervisStorage.ser");
                                    editedOwner.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    serialOutputLock.unlock();
                                }

                            speechRecogniser.startRecognition();
                        }
                        else if(utterance.contains("my sex")){
                            jervisSpeak("Please state your sex sir");

                            speechRecogniser.stopRecognition();

                            String sex = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedOwner = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setSex(sex) 
                                    .build();

                            serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("JervisStorage.ser");
                                    editedOwner.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    serialOutputLock.unlock();
                                }

                            speechRecogniser.startRecognition();
                        }
                        else if(utterance.contains("my name")){
                            jervisSpeak("Please say your name sir");

                            speechRecogniser.stopRecognition();

                            String name = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedOwner = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setName(name) 
                                    .build();

                            serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("JervisStorage.ser");
                                    editedOwner.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    serialOutputLock.unlock();
                                }

                            speechRecogniser.startRecognition();
                        }
                    }

                    else if (utterance.contains("what is") ||
                             utterance.contains("what's")) {
                        
                        if(utterance.contains("the time")){
                            jervisSpeak("Current time is: " + DateGenerator.getCurrentTime());
                            Thread.sleep(1000);
                        }
                        else if(utterance.contains("your name")){
                            jervisSpeak("My name is Jervis, a digital being");
                            Thread.sleep(1000);
                        }
                        else if (utterance.contains("my schedule")) {
                            DueEvents.todaySchedule();
                            Thread.sleep(1000);
                        }
                        else if (utterance.contains("the date")) {
                            jervisSpeak("Today's date is " + DateGenerator.readTodayDate());
                            Thread.sleep(1000);
                        }
                        else if(utterance.contains("the weather in my location") ||
                                utterance.contains("weather in my location")){
                                speechRecogniser.stopRecognition();

                                String sPlace = owner.getLocation();
                                
                                System.out.println(sPlace);//debug 
                                
                                jervisSpeak(InformationFinder
                                        .weatherForecast(sPlace)
                                        .replace("c", "degree in celsius"));
                                
                                Thread.sleep(600);

                                speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
                                speechRecogniser.startRecognition();
                        }
                        else if(utterance.contains("the weather") ||
                                utterance.contains("weather")){
                                speechRecogniser.stopRecognition();
                                speechRecogniser.setRecogniser(ePLACE_GRMR_RCGNSR);
                                jervisSpeak("Please state the city sir");
                                speechRecogniser.startRecognition();
                                
                                String sPlace = speechRecogniser.getResult();
                                
                                System.out.println(sPlace);
                                
                                speechRecogniser.stopRecognition();
                                if(sPlace.contains("my location")){
                                    sPlace = owner.getLocation();
                                }
                                
                                jervisSpeak(InformationFinder
                                        .weatherForecast(sPlace)
                                        .replace("c", "degree in celsius"));
                                
                                Thread.sleep(600);

                                speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
                                speechRecogniser.startRecognition();
                        }
                        else if (utterance.contains("my name")) {

                            if(!owner.hasName() || owner.getName().equals(" ")){
                                jervisSpeak("I do not know your name sir, please say your name");

                                speechRecogniser.stopRecognition();
                                String name = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                                Owner editedOwner = Owner.newBuilder()
                                        .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                        .setName(name) 
                                        .build();

                                serialOutputLock.lock();
                                    try {
                                        serialOutput = new FileOutputStream("JervisStorage.ser");
                                        editedOwner.writeTo(serialOutput);
                                        serialOutput.close();
                                    } finally {
                                        serialOutputLock.unlock();
                                    }

                                speechRecogniser.startRecognition(); 
                            }
                            else{
                                jervisSpeak("Your name is" + owner.getName() + "sir");
                                Thread.sleep(300);
                            }
                        }
                        else if (utterance.contains("my sex")) {

                            if(!owner.hasSex() || owner.getSex().equals(" ")){
                                jervisSpeak("I do not know your sex sir, please say it now");

                                speechRecogniser.stopRecognition();
                                String sSex = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                                if(sSex.equals("male")  ||
                                   sSex.equals("female")||
                                   sSex.equals("Male")  ||
                                   sSex.equals("Female")){
                                    Owner editedOwner = Owner.newBuilder()
                                            .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                            .setSex(sSex) 
                                            .build();

                                    serialOutputLock.lock();
                                        try {
                                            serialOutput = new FileOutputStream("JervisStorage.ser");
                                            editedOwner.writeTo(serialOutput);
                                            serialOutput.close();
                                        } finally {
                                            serialOutputLock.unlock();
                                        }
                                }
                                else{
                                    jervisSpeak("The sex is invalid, you can be either male or female");
                                }

                                speechRecogniser.startRecognition();
                            }
                            else{
                                jervisSpeak("You are a" + owner.getSex() + "sir");
                                Thread.sleep(300);
                            }
                        }
                        else if (utterance.contains("my profession")) {

                            if(!owner.hasProfession() || owner.getProfession().equals(" ")){
                                jervisSpeak("I do not know your profession sir, please say it now");

                                speechRecogniser.stopRecognition();
                                String sProfession = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                                Owner editedOwner = Owner.newBuilder()
                                        .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                        .setProfession(sProfession)
                                        .build();

                                serialOutputLock.lock();
                                        try {
                                            serialOutput = new FileOutputStream("JervisStorage.ser");
                                            editedOwner.writeTo(serialOutput);
                                            serialOutput.close();
                                        } finally {
                                            serialOutputLock.unlock();
                                        }
                                speechRecogniser.startRecognition(); 
                            }
                            else{
                                jervisSpeak("You are " + owner.getProfession() + "sir");
                                Thread.sleep(300);
                            }
                        }
                        else if (utterance.contains("my email")) {

                            if(!owner.hasEmail() || owner.getEmail().equals(" ")){
                                jervisSpeak("I do not know your email sir, please say it now");

                                speechRecogniser.stopRecognition();
                                String sEmail = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                                Owner editedOwner = Owner.newBuilder()
                                        .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                        .setEmail(sEmail)
                                        .build();

                                serialOutputLock.lock();
                                    try {
                                        serialOutput = new FileOutputStream("JervisStorage.ser");
                                        editedOwner.writeTo(serialOutput);
                                        serialOutput.close();
                                    } finally {
                                        serialOutputLock.unlock();
                                    }
                                    
                                speechRecogniser.startRecognition();

                                speechRecogniser.startRecognition(); 
                            }
                            else{
                                jervisSpeak("Your email is " + owner.getEmail() + "sir");
                                Thread.sleep(300);
                            }
                        }
                        else if (utterance.contains("my location")) {

                            if(!owner.hasLocation() || owner.getLocation().equals(" ")){
                                jervisSpeak("I do not know your location sir, please say it now");

                                speechRecogniser.stopRecognition();
                                String sLocation = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                                Owner editedOwner = Owner.newBuilder()
                                        .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                        .setLocation(sLocation)
                                        .build();

                                serialOutputLock.lock();
                                    try {
                                        serialOutput = new FileOutputStream("JervisStorage.ser");
                                        editedOwner.writeTo(serialOutput);
                                        serialOutput.close();
                                    } finally {
                                        serialOutputLock.unlock();
                                    }

                                speechRecogniser.startRecognition(); 
                            }
                            else{
                                jervisSpeak("Your location is " + owner.getLocation() + "sir");
                                Thread.sleep(300);
                            }
                        }
                    }
                    else if(utterance.contains("set")){
                        if(utterance.contains("new commands")){
                            jervisSpeak("On it sir");
                            commandGUI cmdGUI = new commandGUI();
                        }
                        else if(utterance.contains("an event")       ||
                                utterance.contains("a meeting")      ||
                                utterance.contains("an appointment") ||
                                utterance.contains("event")          ||
                                utterance.contains("meeting")        ||
                                utterance.contains("appointment")){
                            
                            speechRecogniser.stopRecognition();
                            speechRecogniser.setRecogniser(eDTE_GRMR_RCGNSR);
                            
                            jervisSpeak("What is the title of the event, sir?");
                            String sTitle = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                            
                            //TODO protocol buff
                            System.out.println(sTitle);
                            
                            jervisSpeak("What is the day and month, sir?");
                            speechRecogniser.startRecognition();
                            String sDaysMonthsText = speechRecogniser.getResult();
                            
                            //TODO protocol buff
                            System.out.println(sDaysMonthsText);
                            
                            speechRecogniser.stopRecognition();
                            jervisSpeak("What is the year of the event, sir?");
                            speechRecogniser.setRecogniser(eYR_GRMR_RCGNSR);
                            speechRecogniser.startRecognition();
                            String sYearText = speechRecogniser.getResult();
                            
                            //TODO protocol buff
                            System.out.println(sYearText);
                            
                            speechRecogniser.stopRecognition();
                            jervisSpeak("What is the time of the event, sir?");
                            speechRecogniser.setRecogniser(eTME_GRMR_RCGNSR);
                            speechRecogniser.startRecognition();
                            String sHourText = speechRecogniser.getResult();
                            
                            //TODO protocol buff
                            System.out.println(sHourText);
                            
                            speechRecogniser.stopRecognition();
                            jervisSpeak("How many minutes before the event shall I remind you, sir?");
                            speechRecogniser.setRecogniser(eMNT_GRMR_RCGNSR);
                            speechRecogniser.startRecognition();
                            String sTimeToRemind = speechRecogniser.getResult();
                            
                            System.out.println("Text Time to remind: " + sTimeToRemind);//debug                           
                            
                            String sDaysMonthsNum = DateGenerator.daysMonthsToNumeric(sDaysMonthsText);
                            String sYearNum = DateGenerator.yearToNumeric(sYearText);
                            String sTimeNum = DateGenerator.timeToNumeric(sHourText);
                            String sMinutesToRemindNum = DateGenerator.minuteToNumeric(sTimeToRemind);       
                            
                            System.out.println("Numeric sDaysMonthsNum: " + sDaysMonthsNum);//debug
                            System.out.println("Numeric sYearNum: " + sYearNum);//debug
                            System.out.println("Numeric sTimeNum: " + sTimeNum);//debug
                            System.out.println("Numeric Time to remind: " + sMinutesToRemindNum);//debug
                            
                            Event editedEveny = Event.newBuilder()
                                        .mergeFrom(new FileInputStream("Organiser.ser"))
                                        .addTitle(sTitle)
                                        .addDayMonth(sDaysMonthsNum)
                                        .addYear(sYearNum)
                                        .addTime(sTimeNum)
                                        .addTimeToRemind(sMinutesToRemindNum)
                                        .build();
                            
                            serialOutputLock.lock();
                                try {
                                    serialOutput = new FileOutputStream("Organiser.ser");
                                    editedEveny.writeTo(serialOutput);
                                    serialOutput.close();
                                } finally {
                                    serialOutputLock.unlock();
                                }
                            
                            if(sYearNum.equals(Organiser.sCurrentYear)){
                                if(sDaysMonthsNum.equals(Organiser.sCurrentDayMonth)){
                                    DueEvents.setEvent(sTitle, sDaysMonthsNum, 
                                        sYearNum, sTimeNum, sMinutesToRemindNum);
                                }
                            }
                            
                            jervisSpeak("The event " + sTitle + "has been set");
                            jervisSpeak("I will remind you about it on " + sDaysMonthsText);
                            jervisSpeak(sTimeToRemind + " minutes before the event is due");
                            
                            speechRecogniser.stopRecognition();
                            speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
                            speechRecogniser.startRecognition();
                            
                            //reminder feature here
                        }
                    }
                    else if(utterance.contains("translate")){
                        jervisSpeak("I am listening for the source text sir");
                        speechRecogniser.stopRecognition();

                        String sourceText = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                        jervisSpeak(enFrTranslator.translate(sourceText));
                        speechRecogniser.startRecognition();
                    }
                    else if(utterance.contains("open")){
                        if(utterance.contains("help")){
                            helpGUI helpGUI = new helpGUI();
                        }
                        else if(utterance.contains("a website")){
                            jervisSpeak("What is the address sir?");
                            speechRecogniser.stopRecognition();

                            String url = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            url = url.replace("dot", ".");
                            url = url.replace(" ", "");

                            try {
                                Desktop.getDesktop().browse(new URL("http://" + url).toURI());
                            } catch (URISyntaxException | IOException e) {
                            }
                            jervisSpeak("The website should pop up, sir");
                            speechRecogniser.startRecognition();
                        }
                        else if(utterance.contains("location") ||
                                utterance.contains("a location")){
                            
                            jervisSpeak("Sure, sir, what is the location name?");
                            speechRecogniser.stopRecognition();
                            speechRecogniser.setRecogniser(eCMD_GRMR_RCGNSR);
                            speechRecogniser.startRecognition();

                            utterance = speechRecogniser.getResult();

                            String dir = ""; 
                            int i = 0; 

                            for (String cmd : command.getCmdList()) {
                                if(cmd.equals(utterance)){
                                    dir = command.getDir(i);
                                } i++;
                            }
                            
                            if(!utterance.contains("<unk>"))
                                Runtime.getRuntime().exec("explorer.exe /open," + dir);
                            
                            else{
                                jervisSpeak("Unfortunately, I was unable to find "+ 
                                            "a location related to this command");
                                jervisSpeak("please add the command using the custom command option");
                            }

                            speechRecogniser.stopRecognition();
                            speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
                            speechRecogniser.startRecognition();
                        }  
                    }     
                    else if(utterance.contains("make")){
                        //Future<String> future = (Future<String>) execServise.submit(new GoogleSpeech());

                        /*while(GoogleSpeech.bStartRecognition){
                            GoogleSpeech.displayResponse();
                            if(GoogleSpeech.displayResponse().contains("note finished"))
                                future.cancel(true);
                        }*/
                        if(utterance.contains("research") ||
                           utterance.contains("a research")){
                            speechRecogniser.stopRecognition();
                            jervisSpeak("Sure sir, what shall I look up?");
                            Thread.sleep(200);
                            String sResearch = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                            InformationFinder.definitionFinder(sResearch);
                            speechRecogniser.startRecognition();
                        }
                            
                        
                        if(utterance.contains("a note")){

                            String noteTitle, content;

                            jervisSpeak("short, medium or long, sir");
                            Thread.sleep(1000);
                            utterance = speechRecogniser.getResult();

                            if(utterance.contains("short")){
                                noteLength = NoteLength.eSHORT_NOTE;
                            }
                            else if(utterance.contains("medium")){
                                noteLength = NoteLength.eMEDIUM_NOTE;
                            }
                            else if(utterance.contains("long")){
                                noteLength = NoteLength.eLONG_NOTE;
                            }

                            speechRecogniser.stopRecognition();
                            jervisSpeak("What should be the title, sir?");

                            noteTitle = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            jervisSpeak("Ok, sir, I am listening for the content");
                            content = WatsonSpeechRecogniser.recognise(noteLength);

                            NotepadWrapper.writeNoteData(noteTitle + ".txt", content);
                            jervisSpeak("Task completed, sir");

                            speechRecogniser.startRecognition();
                        }
                    } 
                }
                    else if (utterance.contains("that was funny") ||
                             utterance.contains("that's funny")   ||
                             utterance.contains("that's funny")   ||
                             utterance.contains("you're stupid")
                            ){
                        jervisSpeak("Thank you, sir");
                        Thread.sleep(600);
                    }
                    else if(utterance.contains("thank you") ||
                            utterance.contains("thanks")){
                        jervisSpeak("Anytime, sir");
                        Thread.sleep(600);
                    }
                    System.out.println(utterance);//debug
            }
        }
    }
    
    public static void actionConvers() throws LineUnavailableException{
        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eCNVRS_GRMR_RCGNSR);
        speechRecogniser.startRecognition();

        utterance = speechRecogniser.getResult();

        System.out.println(utterance);
        jervisSpeak(InformationFinder.quotesFinder(utterance.replace(' ', '+')));

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
    }
    
    //Run from multiple threads
    public static synchronized void jervisSpeak(String text){
        startAnimation = true;
        voice.speak(text);
        startAnimation = false;
    }
    
    public static boolean getStartAnim(){
        return startAnimation;
    }
    
    public static boolean getListening(){
        return Jervis.bListening;
    }
        
    public static void setListening(boolean bListening){
        Jervis.bListening = bListening;
    }
    
    public static void customCmd(String sCmd, String sDir) throws InterruptedException{
        
        Command editedObject;
        
        try {
            editedObject = Command.newBuilder()
                    .mergeFrom(new FileInputStream("CustomCmd.ser"))
                    .addCmd(sCmd)
                    .addDir(sDir)
                    .build();
            
            serialOutput = new FileOutputStream("CustomCmd.ser");
            editedObject.writeTo(serialOutput);
            serialOutput.close();
            
            NotepadWrapper.addToGram("D:\\J.E.R.V.I.S\\J.E.R.V.I.S\\GitHub\\src\\TextBase\\commands.gram", sCmd);
            
            Jervis.jervisSpeak("Please restart me in order for the changes to take place");
            Thread.sleep(1000);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(commandGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(commandGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void customWeather(String sPlace, String sPostcode) throws InterruptedException, IOException{
        
        String fileContent = NotepadWrapper.readNoteData("D:\\J.E.R.V.I.S\\J.E.R.V.I.S\\GitHub\\src\\TextBase\\postcodes.txt");
        
        fileContent += sPlace + ", " + sPostcode + "," +
                System.getProperty("line.separator");
        
        NotepadWrapper.writeNoteData(
                "D:\\J.E.R.V.I.S\\J.E.R.V.I.S\\GitHub\\src\\TextBase\\postcodes.txt",
                fileContent);
        
        NotepadWrapper.addToGram("D:\\J.E.R.V.I.S\\J.E.R.V.I.S\\GitHub\\src\\TextBase\\places.gram", sPlace);
            
        Jervis.jervisSpeak("Please restart me in order for the changes to take place");
        Thread.sleep(1000);
    }
   
    
}
