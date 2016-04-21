/******************************************************************************/
/**
@file          Jervis.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Main/root file for Jervis.
*******************************************************************************/

package JERVIS;

import TextBase.JervisStorage.Owner;
import TextBase.CustomCmd.Command;
import java.io.IOException;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javax.sound.sampled.LineUnavailableException;
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

import static TextBase.SpeechRecognisers.*;
import static java.lang.System.*;

public class Jervis {
    
    public static boolean bInit = true;
    private static mainGUI mainFrame;
            
    private static final String Voice = "kevin16";
    private static String sSalutation = "sir";
    private static String utterance;
    private static VoiceManager vm;
    private static Voice voice;
    private static Translator enFrTranslator;
    
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
        "Yes,",
        "How can I help,",
        "How can I assist you,",
        "can I help you,",
        "I am listening,"
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
    **  17/04/2016  M.Michalski Code optimisation
    **  17/04/2016  M.Michalski Added salutation depending on sex remembered
    ***************************************************************************/
    /**Description: Main function for Jervis. Implements the Tree Search algorithm
     * and links all the subcomponents.
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
        
        DateGenerator.initDateGenerator();
        InformationFinder.initInformationFinder();
        
        enFrTranslator = new Translator("en", "fr");
        
        execServise.execute(new WaveformAnim());
        execServise.execute(new Organiser());
        
        //initial for salutation - will need to repeat in the ST loop
        owner = Owner.parseFrom(new FileInputStream("JervisStorage.ser"));
        if(owner.getSex().equals("female"))
            sSalutation = "madam";
        else
            sSalutation = "sir"; 
        
        jervisSpeak("I am ready to serve you " + sSalutation);
        Thread.sleep(1000);
                
        while (true) {
            
            utterance = speechRecogniser.getResult();
            System.out.println(utterance);//debug
            Jervis.bAnimationStart = true;
            
            if(getListening()){
                if (utterance.equals("jervis") ||
                    utterance.equals("hey jervis")   ){

                    jervisSpeak(sJervInit[new Random().nextInt(sJervInit.length)] + sSalutation);

                    owner = Owner.parseFrom(new FileInputStream("JervisStorage.ser"));
                    command = Command.parseFrom(new FileInputStream("CustomCmd.ser"));
                    events = Event.parseFrom(new FileInputStream("Organiser.ser"));
                    Thread.sleep(200);

                    utterance = speechRecogniser.getResult();
                    
                    if(utterance.contains("stop listening")){
                        setListening(false);
                        mainGUI.chkListen.setSelected(false);
                    }
                    else if(utterance.contains("out of the way")){
                        mainFrame.setState(mainGUI.ICONIFIED);
                    }
                    else if(utterance.contains("come back")){
                        mainFrame.setState(mainGUI.NORMAL);
                    }
                    else if (utterance.contains("go away") ||
                        utterance.contains("you can go")){
                        jervisSpeak("Ok, I am gone, " + sSalutation + ", Goodbye");
                        exit(0);
                    }
                    else if (utterance.contains("add")){
                        
                        if(utterance.contains("weather places")){
                            jervisSpeak("On it " + sSalutation);
                            Thread.sleep(1500);
                            weatherGUI weatherGUI = new weatherGUI();
                        }
                        else if(utterance.contains("new commands")){
                            jervisSpeak("On it " + sSalutation);
                            Thread.sleep(1500);
                            commandGUI cmdGUI = new commandGUI();
                        }
                    }
                    else if (
                        utterance.contains("hello")||
                        utterance.contains("good") ||
                        utterance.startsWith("how are")){
  
                        if (utterance.contains("hello")){
                            jervisSpeak("Hello! " + sSalutation + ", how is life?");
                            jervisConvers();
                        }
                        else if (utterance.contains("morning")){
                            jervisSpeak("Good morning! " + sSalutation + ", how is life?");
                            jervisConvers();
                        }
                        else if (utterance.contains("afternoon")){
                            jervisSpeak("Good afternoon! " + sSalutation + ", how is life?");
                            jervisConvers();
                        }
                        else if (utterance.contains("evening")){
                            jervisSpeak("Good evening! " + sSalutation + ", how is life?");
                            jervisConvers();
                        }
                        else if (utterance.contains("how are")){
                            jervisSpeak("Always great, thank you. What about yourself?");
                            jervisConvers();
                        }     
                    }
                    else if (utterance.contains("remember")) {
                        if(utterance.contains("my location")){
                            jervisRememberLocation(); 
                        }
                        else if(utterance.contains("my email")){
                            jervisRememberEmail();
                        }
                        else if(utterance.contains("my profession")){
                            jervisRememberProfession();
                        }
                        else if(utterance.contains("my sex")){
                            jervisRememberSex();
                        }
                        else if(utterance.contains("my name")){
                            jervisRememberName();
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
                                jervisWeather(true);
                        }
                        else if(utterance.contains("the weather") ||
                                utterance.contains("weather")){
                                jervisWeather(false);
                        }
                        else if (utterance.contains("my name")) {
                            jervisStateName();
                        }
                        else if (utterance.contains("my sex")) {
                            jervisStateSex();
                        }
                        else if (utterance.contains("my profession")) {
                            jervisStateProfession();
                        }
                        else if (utterance.contains("my email")) {
                            jervisStateEmail();
                        }
                        else if (utterance.contains("my location")) {
                            jervisStateLocation();  
                        }
                    }
                    else if(utterance.contains("set")){
                        if(utterance.contains("new commands")){
                            jervisSpeak("On it " + sSalutation);
                            Thread.sleep(1500);
                            commandGUI cmdGUI = new commandGUI();
                        }
                        else if(utterance.contains("an event")       ||
                                utterance.contains("a meeting")      ||
                                utterance.contains("an appointment") ||
                                utterance.contains("event")          ||
                                utterance.contains("meeting")        ||
                                utterance.contains("appointment")){
                            jervisEvent();
                        }
                    }
                    else if(utterance.contains("translate")){
                        jervisTranslate();
                    }
                    else if(utterance.contains("open")){
                        if(utterance.contains("help")){
                            helpGUI helpGUI = new helpGUI();
                        }
                        else if(utterance.contains("a website")){
                            jervisOpenWebsite();
                        }
                        else if(utterance.contains("location") ||
                                utterance.contains("a location")){
                            jervisOpenLocation();
                        }  
                    }     
                    else if(utterance.contains("make")){
                        if(utterance.contains("research") ||
                           utterance.contains("a research")){
                            jervisResearch();
                        }
                        else if(utterance.contains("a note")){
                            jervisNote(utterance);
                        }
                        else if(utterance.contains("an event")       ||
                                utterance.contains("a meeting")      ||
                                utterance.contains("an appointment") ||
                                utterance.contains("event")          ||
                                utterance.contains("meeting")        ||
                                utterance.contains("appointment")){
                            jervisEvent();
                        }
                    } 
                }
                    else if (utterance.contains("that was funny") ||
                             utterance.contains("that's funny")   ||
                             utterance.contains("that's funny")   ||
                             utterance.contains("you're stupid")){
                        jervisSpeak("Thank you, " + sSalutation);
                        Thread.sleep(600);
                    }
                    else if(utterance.contains("thank you") ||
                            utterance.contains("thanks")){
                        jervisSpeak("Anytime, " + sSalutation);
                        Thread.sleep(600);
                    }
                
                    System.out.println(utterance);//debug
            }
        }
    }
    /*  jervisConvers **********************************************************
    **  11/01/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Used for conducting conversations with the user.
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisConvers() throws LineUnavailableException{
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
    
    /*  jervisSpeak ************************************************************
    **  15/01/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Utilises the speech synthesiser as well as runs the GUI
     * animation by setting the startAnimation flag. The method is used by
     * multiple threads, hence it's synchronised.
     * @param text
    ***************************************************************************/
    public static synchronized void jervisSpeak(String text){
        startAnimation = true;
        voice.speak(text);
        startAnimation = false;
    }
    
    /*  getStartAnim ***********************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: returns the startAnimation flag. Used by the WaveformAnim
     * Runnable.
     * @return 
    ***************************************************************************/
    public static boolean getStartAnim(){
        return startAnimation;
    }
    
    /*  getListening ***********************************************************
    **  09/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: returns bListening flag.
     * @return 
    ***************************************************************************/
    public static boolean getListening(){
        return Jervis.bListening;
    }
        
    /*  setListening ***********************************************************
    **  09/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: sets the bListening flag.
     * @param bListening
    ***************************************************************************/
    public static void setListening(boolean bListening){
        Jervis.bListening = bListening;
    }
    
    /*  customCmd **************************************************************
    **  21/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: used by commandGUI. Allows to add new commands to Jervis
     * @param sCmd
     * @param sDir
     * @throws java.lang.InterruptedException
    ***************************************************************************/
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
            
            NotepadWrapper.addToGram("src/TextBase/commands.gram", sCmd);
            
            Jervis.jervisSpeak("Please restart me in order for the changes to take place");
            Thread.sleep(1000);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(commandGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(commandGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*  customWeather **********************************************************
    **  09/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Allows to add new weather places (UK support only).
     * @param sPlace
     * @param sPostcode
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
    ***************************************************************************/
    public static void customWeather(String sPlace, String sPostcode) throws InterruptedException, IOException{
        
        String fileContent = NotepadWrapper.readNoteData("src/TextBase/postcodes.txt");
        
        fileContent += sPlace + ", " + sPostcode + "," +
                System.getProperty("line.separator");
        
        NotepadWrapper.writeNoteData(
                "src/TextBase/postcodes.txt",
                fileContent);
        
        NotepadWrapper.addToGram("src/TextBase/places.gram", sPlace);
            
        Jervis.jervisSpeak("Please restart me in order for the changes to take place");
        Thread.sleep(1000);
    }
    
    /*  jervisResearch *********************************************************
    **  09/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Allows to add new weather places (UK support only).
     * @throws java.lang.InterruptedException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisResearch() throws InterruptedException, LineUnavailableException{
        
        speechRecogniser.stopRecognition();
        
        jervisSpeak("Sure " + sSalutation + ", what shall I look up?");
        Thread.sleep(200);
        
        String sResearch = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
        InformationFinder.definitionFinder(sResearch);
        speechRecogniser.startRecognition();
    }
    
    /*  jervisResearch *********************************************************
    **  27/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Implementation of the Note Taking feature.
     * @param utterance
     * @throws java.lang.InterruptedException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisNote(String utterance) throws InterruptedException, LineUnavailableException{
        
        String noteTitle, content;

        jervisSpeak("short, medium or long, " + sSalutation);
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
        jervisSpeak("What should be the title, "  + sSalutation + " ?");

        noteTitle = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

        jervisSpeak("Ok, "  + sSalutation + " I am listening for the content");
        content = WatsonSpeechRecogniser.recognise(noteLength);

        NotepadWrapper.writeNoteData(noteTitle + ".txt", content);
        jervisSpeak("Task completed, " + sSalutation);

        speechRecogniser.startRecognition();
    }
    
    /*  jervisResearch *********************************************************
    **  24/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Allows utilisation of the commands stored in protobufs.
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.io.IOException
    ***************************************************************************/
    public static void jervisOpenLocation() throws LineUnavailableException, IOException{
        
        jervisSpeak("Sure, "  + sSalutation + ", what is the location name?");
        
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
            jervisSpeak("Unfortunately, I was unable to find " + 
                        "a location related to this command");
            jervisSpeak("please add the command using the custom command option");
        }

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
    }
    
    /*  jervisResearch *********************************************************
    **  24/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Opens a website with the specified address.
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisOpenWebsite() throws LineUnavailableException{
        jervisSpeak("What is the address "  + sSalutation + " ?");
        speechRecogniser.stopRecognition();

        String url = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

        url = url.replace("dot", ".");
        url = url.replace(" ", "");

        try {
            Desktop.getDesktop().browse(new URL("http://" + url).toURI());
        } catch (URISyntaxException | IOException e) {}
        
        jervisSpeak("The website should pop up, " + sSalutation);
        speechRecogniser.startRecognition();
    }
    
    /*  jervisTranslate ********************************************************
    **  02/03/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Translates input text in French. This is beta 
     * feature (not required).
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisTranslate() throws LineUnavailableException{
        jervisSpeak("I am listening for the source text"  + sSalutation);
        speechRecogniser.stopRecognition();

        String sourceText = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
        jervisSpeak(enFrTranslator.translate(sourceText));
        speechRecogniser.startRecognition();
    }
    
    /*  jervisRememberLocation *************************************************
    **  21/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Takes user location and serialises it into 
     * JervisStorage.ser file.
     * feature (not required).
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisRememberLocation() throws IOException, LineUnavailableException{
        jervisSpeak("Please state your location, "  + sSalutation);

        speechRecogniser.stopRecognition();

        String location = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

        Owner editedOwner = Owner.newBuilder()
                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                .setLocation(location) 
                .build();

        serialOutputLock.lock();
            try {
                serialOutput = new FileOutputStream("JervisStorage.ser");
                editedOwner.writeTo(serialOutput);
                serialOutput.close();
            } finally {
                serialOutputLock.unlock();
            }

        jervisSpeak("Your new location is " + location + sSalutation);

        speechRecogniser.startRecognition(); 
    }
    
    /*  jervisRememberEmail ****************************************************
    **  21/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Takes user location and serialises it into 
     * JervisStorage.ser file.
     * feature (not required).
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.io.FileNotFoundException
    ***************************************************************************/
    public static void jervisRememberEmail() throws IOException, LineUnavailableException,
            IOException, FileNotFoundException, FileNotFoundException{
        
        jervisSpeak("Please state your email " + sSalutation);

        speechRecogniser.stopRecognition();

        String email = WatsonSpeechRecogniser.recognise(NoteLength.eSHORT_NOTE);

        Owner editedOwner = Owner.newBuilder()
                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                .setEmail(email.replace("out", "at")) 
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
        
        jervisSpeak("I will remember your email as " + email + sSalutation);
    }
    
    /*  jervisRememberProfession ***********************************************
    **  21/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Takes user profession and serialises it into 
     * JervisStorage.ser file.
     * feature (not required).
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisRememberProfession() throws IOException, LineUnavailableException {
        
        jervisSpeak("Please state your profession " + sSalutation);

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

        jervisSpeak("Ok, I will remember that you are " + profession + sSalutation);
        
        speechRecogniser.startRecognition();
    }
    
    /*  jervisRememberSex ******************************************************
    **  21/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Takes user profession and serialises it into 
     * JervisStorage.ser file.
     * feature (not required).
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisRememberSex() throws IOException, LineUnavailableException{
        
        speechRecogniser.stopRecognition();
        jervisSpeak("Please state your sex " + sSalutation);

        speechRecogniser.setRecogniser(eCMD_GRMR_RCGNSR);
        speechRecogniser.startRecognition();

        String sex = speechRecogniser.getResult();
        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);

        if(sex.equals("male") || sex.equals("female") || sex.equals("Male") ||
            sex.equals("Female")){

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
            if(sex.equals("female"))
                sSalutation = "madam";
            else
                sSalutation = "sir";
            
            jervisSpeak("Ok, I will remember that you are a " + sex + " " + sSalutation);
        }
        else{
            jervisSpeak("The sex is invalid, you can be either male or female");
        }    

        speechRecogniser.startRecognition();
    }
    
    /*  jervisRememberName *****************************************************
    **  21/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Takes user name and serialises it into 
     * JervisStorage.ser file.
     * feature (not required).
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisRememberName() throws LineUnavailableException, IOException{
        
            jervisSpeak("Please say your name " + sSalutation);

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
                
            jervisSpeak("Ok, I will remember you as " + name + sSalutation);

            speechRecogniser.startRecognition();
    }
    
    /*  jervisWeather **********************************************************
    **  09/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Top level implementation for the Weather Forecasting feature.
     * @param myLocation
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
    ***************************************************************************/
    public static void jervisWeather(boolean myLocation) throws LineUnavailableException, IOException, InterruptedException{
        
        String sPlace = "";
        
        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(ePLACE_GRMR_RCGNSR);

        if(myLocation){
            sPlace = owner.getLocation();
        }
        else{
            jervisSpeak("Please state the city " + sSalutation);
            Thread.sleep(600);
            speechRecogniser.startRecognition();
            
            sPlace = speechRecogniser.getResult();
            speechRecogniser.stopRecognition();
            
            if(sPlace.equals("my location")){
                sPlace = owner.getLocation().toLowerCase().replace(" ", "");
            }
        }

        jervisSpeak(InformationFinder
                .weatherForecast(sPlace.toLowerCase())
                .replace("c", "degree in celsius"));

        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
    }
    
    /*  jervisStateName ********************************************************
    **  22/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Retrieves user's name.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public static void jervisStateName() throws IOException, InterruptedException, LineUnavailableException{
        if(!owner.hasName() || owner.getName().equals(" ")){
            
            jervisSpeak("I do not know your name " + sSalutation + ", please say your name");

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
            jervisSpeak("Your name is" + owner.getName() + sSalutation);
            Thread.sleep(300);
        }
    }
    
    /*  jervisStateSex *********************************************************
    **  22/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Retrieves user's sex.
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.lang.InterruptedException
    ***************************************************************************/
    public static void jervisStateSex() throws IOException, LineUnavailableException, InterruptedException{
        if(!owner.hasSex() || owner.getSex().equals(" ")){
            
            jervisSpeak("I do not know your sex, please say it now");

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
            jervisSpeak("You are a" + owner.getSex() + " " + sSalutation);
            Thread.sleep(300);
        }
    }
    
    /*  jervisStateProfession **************************************************
    **  22/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Retrieves user's profession.
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.lang.InterruptedException
    ***************************************************************************/
    public static void jervisStateProfession() throws IOException,
            LineUnavailableException, InterruptedException{
        
        if(!owner.hasProfession() || owner.getProfession().equals(" ")){
            
            jervisSpeak("I do not know your profession " + sSalutation + ", please say it now");

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
            jervisSpeak("You are " + owner.getProfession() + sSalutation);
            Thread.sleep(300);
        }
    }
    
    /*  jervisStateEmail *******************************************************
    **  22/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Retrieves user's e-mail.
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.lang.InterruptedException
    ***************************************************************************/
    public static void jervisStateEmail() throws IOException, LineUnavailableException, InterruptedException{
        if(!owner.hasEmail() || owner.getEmail().equals(" ")){
            
            jervisSpeak("I do not know your email " + sSalutation + ", please say it now");

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
            jervisSpeak("Your email is " + owner.getEmail() + sSalutation);
            Thread.sleep(300);
        }
    }
    
    /*  jervisStateLocation ****************************************************
    **  22/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Retrieves user's location.
     * @throws java.io.FileNotFoundException
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.lang.InterruptedException
    ***************************************************************************/
    public static void jervisStateLocation() throws FileNotFoundException,
            IOException, InterruptedException, LineUnavailableException{
        if(!owner.hasLocation() || owner.getLocation().equals(" ")){
            
            jervisSpeak("I do not know your location " + sSalutation + ", please say it now");

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
            jervisSpeak("Your location is " + owner.getLocation() + sSalutation);
            Thread.sleep(300);
        }
    }
    
    /*  jervisEvent ************************************************************
    **  22/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Retrieves user's location.
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws java.io.IOException
    ***************************************************************************/
    public static void jervisEvent() throws LineUnavailableException, IOException{
        
        String sDaysMonthsNum = "";
        String sYearNum = "";
        String sTimeNum = "";
        String sMinutesToRemindNum = "";
            
        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eDTE_GRMR_RCGNSR);

        jervisSpeak("What is the title of the event, " + sSalutation + " ?");
        String sTitle = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

        System.out.println(sTitle);

        jervisSpeak("What is the day and month, " + sSalutation + " ?");
        speechRecogniser.startRecognition();
        String sDaysMonthsText = speechRecogniser.getResult();

        System.out.println(sDaysMonthsText);

        speechRecogniser.stopRecognition();
        jervisSpeak("What is the year of the event, " + sSalutation + " ?");
        speechRecogniser.setRecogniser(eYR_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
        String sYearText = speechRecogniser.getResult();

        System.out.println(sYearText);

        speechRecogniser.stopRecognition();
        jervisSpeak("What is the time of the event, " + sSalutation + " ?");
        speechRecogniser.setRecogniser(eTME_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
        String sHourText = speechRecogniser.getResult();

        System.out.println(sHourText);

        speechRecogniser.stopRecognition();
        jervisSpeak("How many minutes before the event shall I remind you, " + sSalutation + " ?");
        speechRecogniser.setRecogniser(eMNT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
        String sTimeToRemind = speechRecogniser.getResult();

        System.out.println("Text Time to remind: " + sTimeToRemind);//debug                           

        if(!sDaysMonthsText.isEmpty() && !sYearText.isEmpty() && !sHourText.isEmpty()
                && !sTimeToRemind.isEmpty()){
            
            sDaysMonthsNum = DateGenerator.daysMonthsToNumeric(sDaysMonthsText);
            sYearNum = DateGenerator.yearToNumeric(sYearText);
            sTimeNum = DateGenerator.timeToNumeric(sHourText);
            sMinutesToRemindNum = DateGenerator.minuteToNumeric(sTimeToRemind); 
        }
        else{
            jervisSpeak("Unfortunately, I didn't catch one of the parameters, pl"
                    + "ease try to set an event again");
            return;
        }

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
        jervisSpeak("at " + sTimeNum);
        jervisSpeak(sTimeToRemind + " minutes before the event is due");

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
    }
}