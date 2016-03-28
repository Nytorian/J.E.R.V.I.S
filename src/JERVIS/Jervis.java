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
import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class Jervis {
    
    private static mainGUI mainFrame = new mainGUI();;
            
    private static final String Voice = "kevin16";
    private static VoiceManager vm;
    private static Voice voice;
    private static String utterance;
    
    private static Owner owner;
    private static Command command;
    private static boolean startAnimation = false;
    
    private static SpeechRecogniser speechRecogniser;  
    
    static NoteLength noteLength;
    
    public static FileOutputStream serialOutput;
    public static boolean bAnimationStart = false;
    public static boolean bListening = true;
   
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
    ***************************************************************************/
    /**Description: Main function for Jervis
     * @throws java.io.IOException
     * @param args *  
     * @throws javax.sound.sampled.LineUnavailableException  
     * @throws java.lang.InterruptedException  
    ****************************************************************************/
    public static void main(String[] args) throws IOException,
            LineUnavailableException, InterruptedException {

        speechRecogniser = new SpeechRecogniser();
        speechRecogniser.startRecognition();
        
        vm = VoiceManager.getInstance();
        voice = vm.getVoice(Voice);
        voice.allocate();
        
        ExecutorService execServise = Executors.newCachedThreadPool();
        execServise.execute(new WaveFormAnim());

                        
        System.setProperty("mbrola.base", "D://J.E.R.V.I.S//Downloaded//mbrola//");     
        
        DateGenerator.initDateGenerator();
        Translator enFrTranslator = new Translator("en", "fr");
        
        //ownerBuilder = Owner.newBuilder();
        
        while (true) {
            if(getListening()){
                utterance = speechRecogniser.getResult();

                System.out.println(utterance);//debug

                Jervis.bAnimationStart = true;
              
                if (utterance.equals("jervis")){

                    jervisSpeak("Yes, sir?");

                    owner = Owner.parseFrom(new FileInputStream("JervisStorage.ser"));
                    command = Command.parseFrom(new FileInputStream("CustomCmd.ser"));
                    Thread.sleep(200);

                    utterance = speechRecogniser.getResult();
                    
                    if (utterance.contains("go away") ||
                        utterance.contains("you can go")){
                        jervisSpeak("Ok, I am gone, sir, Goodbye");
                        exit(0);
                    }

                    else if (
                        utterance.contains("hello")    ||
                        utterance.contains("good afternoon")||
                        utterance.contains("good evening")  ||
                        utterance.startsWith("how are")){
  
                        if (utterance.contains("Hello")){
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
                    else if(utterance.contains("open location") ||
                            utterance.contains("open a location")){
                        
                        jervisSpeak("Sure, sir, which location?");
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
                        
                        Runtime.getRuntime().exec("explorer.exe /select," + dir);
                        
                        speechRecogniser.stopRecognition();
                        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
                        speechRecogniser.startRecognition();
                    }
                    else if (utterance.contains("what is my name")) {

                        if(!owner.hasName() || owner.getName().equals(" ")){
                            jervisSpeak("I do not know your name sir, please say your name");

                            speechRecogniser.stopRecognition();
                            String name = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedObject = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setName(name) 
                                    .build();

                            serialOutput = new FileOutputStream("JervisStorage.ser");
                            editedObject.writeTo(serialOutput);
                            serialOutput.close();

                            speechRecogniser.startRecognition(); 
                        }
                        else{
                            jervisSpeak("Your name is" + owner.getName() + "sir");
                            Thread.sleep(300);
                        }
                        //*/

                        /* debug - loading an initial object
                        Owner pusheen= Owner.newBuilder()
                        .setName(" ") 
                        .setSex(" ") 
                        .setProfession(" ")
                        .setEmail(" ")
                        .setLocation(" ")
                        .build();

                        serialOutput = new FileOutputStream("JervisStorage.ser");
                        pusheen.writeTo(serialOutput);
                        serialOutput.close();

                        owner = Owner.parseFrom(new FileInputStream("JervisStorage.ser"));
                        System.out.println(owner.getName());
                       */
                    }
                    else if (utterance.contains("what is my sex")) {

                        if(!owner.hasSex() || owner.getSex().equals(" ")){
                            jervisSpeak("I do not know your sex sir, please say it now");

                            speechRecogniser.stopRecognition();
                            String sex = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            if(sex.equals("male")  ||
                               sex.equals("female")||
                               sex.equals("Male")  ||
                               sex.equals("Female")){
                                Owner editedObject = Owner.newBuilder()
                                        .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                        .setSex(sex) 
                                        .build();

                                serialOutput = new FileOutputStream("JervisStorage.ser");
                                editedObject.writeTo(serialOutput);
                                serialOutput.close();
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
                    else if (utterance.contains("what is my profession")) {

                        if(!owner.hasProfession() || owner.getProfession().equals(" ")){
                            jervisSpeak("I do not know your profession sir, please say it now");

                            speechRecogniser.stopRecognition();
                            String profession = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedObject = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setProfession(profession)
                                    .build();

                            serialOutput = new FileOutputStream("JervisStorage.ser");
                            editedObject.writeTo(serialOutput);
                            serialOutput.close();

                            speechRecogniser.startRecognition(); 
                        }
                        else{
                            jervisSpeak("You are " + owner.getProfession() + "sir");
                            Thread.sleep(300);
                        }
                    }
                    else if (utterance.contains("what is my email")) {

                        if(!owner.hasEmail() || owner.getEmail().equals(" ")){
                            jervisSpeak("I do not know your email sir, please say it now");

                            speechRecogniser.stopRecognition();
                            String email = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedObject = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setEmail(email)
                                    .build();

                            serialOutput = new FileOutputStream("JervisStorage.ser");
                            editedObject.writeTo(serialOutput);
                            serialOutput.close();

                            speechRecogniser.startRecognition(); 
                        }
                        else{
                            jervisSpeak("Your email is " + owner.getEmail() + "sir");
                            Thread.sleep(300);
                        }
                    }
                    else if (utterance.contains("what is my location")) {

                        if(!owner.hasLocation() || owner.getLocation().equals(" ")){
                            jervisSpeak("I do not know your location sir, please say it now");

                            speechRecogniser.stopRecognition();
                            String location = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                            Owner editedObject = Owner.newBuilder()
                                    .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                    .setLocation(location)
                                    .build();

                            serialOutput = new FileOutputStream("JervisStorage.ser");
                            editedObject.writeTo(serialOutput);
                            serialOutput.close();

                            speechRecogniser.startRecognition(); 
                        }
                        else{
                            jervisSpeak("Your location is " + owner.getLocation() + "sir");
                            Thread.sleep(300);
                        }
                    }
                    else if (utterance.contains("remember my name")) {
                        jervisSpeak("Please say your name sir");

                        speechRecogniser.stopRecognition();

                        String name = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                        Owner editedOwner = Owner.newBuilder()
                                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                .setName(name) 
                                .build();

                        serialOutput = new FileOutputStream("JervisStorage.ser");
                        editedOwner.writeTo(serialOutput);
                        serialOutput.close();

                        speechRecogniser.startRecognition(); 
                    }
                    else if (utterance.contains("remember my sex")) {
                        jervisSpeak("Please state your sex sir");

                        speechRecogniser.stopRecognition();

                        String sex = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                        Owner editedOwner = Owner.newBuilder()
                                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                .setSex(sex) 
                                .build();

                        serialOutput = new FileOutputStream("JervisStorage.ser");
                        editedOwner.writeTo(serialOutput);
                        serialOutput.close();

                        speechRecogniser.startRecognition(); 
                    }
                    else if (utterance.contains("remember my profession")) {
                        jervisSpeak("Please state your profession sir");

                        speechRecogniser.stopRecognition();

                        String profession = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                        Owner editedOwner = Owner.newBuilder()
                                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                .setProfession(profession) 
                                .build();

                        serialOutput = new FileOutputStream("JervisStorage.ser");
                        editedOwner.writeTo(serialOutput);
                        serialOutput.close();

                        speechRecogniser.startRecognition(); 
                    }
                    else if (utterance.contains("remember my email")) {
                        jervisSpeak("Please state your email sir");

                        speechRecogniser.stopRecognition();

                        String email = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                        Owner editedOwner = Owner.newBuilder()
                                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                .setEmail(email) 
                                .build();

                        serialOutput = new FileOutputStream("JervisStorage.ser");
                        editedOwner.writeTo(serialOutput);
                        serialOutput.close();

                        speechRecogniser.startRecognition(); 
                    }
                    else if (utterance.contains("remember my location")) {
                        jervisSpeak("Please state your location sir");

                        speechRecogniser.stopRecognition();

                        String location = WatsonSpeechRecogniser.recognise(NoteLength.eWord);

                        Owner editedOwner = Owner.newBuilder()
                                .mergeFrom(new FileInputStream("JervisStorage.ser"))
                                .setLocation(location) 
                                .build();

                        serialOutput = new FileOutputStream("JervisStorage.ser");
                        editedOwner.writeTo(serialOutput);
                        serialOutput.close();

                        speechRecogniser.startRecognition(); 
                    }
                    else if (utterance.contains("what is your name")) {
                        jervisSpeak("My name is Jervis, a digital being");
                        Thread.sleep(1000);
                    }
                    else if (utterance.contains("the date")) {
                        jervisSpeak("Today's date is " + DateGenerator.readTodayDate());
                        Thread.sleep(1000);
                    }
                    else if(utterance.contains("remember event")){
                        jervisSpeak("What date sir?");
                        //TODO: add the functionality
                    }
                    else if(utterance.contains("set new commands")){
                        jervisSpeak("On it sir");
                        commandGUI cmdGUI = new commandGUI();
                    }
                    else if(utterance.contains("translate")){
                        jervisSpeak("I am listening for the source text sir");
                        speechRecogniser.stopRecognition();

                        String sourceText = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                        jervisSpeak(enFrTranslator.translate(sourceText));
                        speechRecogniser.startRecognition();
                    }
                    else if(utterance.contains("open a website")){
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
                    else if(utterance.contains("the weather")){
                        actionWeather(utterance);
                    }     
                    else if(utterance.contains("make a note")){
                        //Future<String> future = (Future<String>) execServise.submit(new GoogleSpeech());

                        /*while(GoogleSpeech.bStartRecognition){
                            GoogleSpeech.displayResponse();
                            if(GoogleSpeech.displayResponse().contains("note finished"))
                                future.cancel(true);
                        }*/

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

                        NotepadWrapper.writeNoteData(noteTitle, content);
                        jervisSpeak("Task completed, sir");

                        speechRecogniser.startRecognition();
                    }
                    else if(utterance.contains("open location")){
                        jervisSpeak("What is the location?"); 
                        Thread.sleep(600);
                    }   
                }

                else if (utterance.contains("that was funny") ||
                         utterance.contains("that's funny")   ||
                         utterance.contains("that's funny")){
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
    
    public static void actionWeather(String utterance) 
            throws IOException, InterruptedException, LineUnavailableException{
        
        /*if(utterance.contains("in my location")){

        }
        else{
            //if location unknown
            jervisSpeak("Where about, sir?");
        }*/

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(ePLACE_GRMR_RCGNSR);
        speechRecogniser.startRecognition();

        //utterance = speechRecogniser.getResult();
        jervisSpeak(InformationFinder.weatherForecast(InformationFinder.Place.ePORTSMOUTH, 
                InformationFinder.Period.eOUTLOOK).replace("c", "degree in celsius"));
        Thread.sleep(1000);

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
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
    
    private static void jervisSpeak(String text){
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
   
    
}
