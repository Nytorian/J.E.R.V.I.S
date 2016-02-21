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

import TextBase.JervisStorage;
import TextBase.JervisStorage.Owner;
import java.io.IOException;
import javax.swing.JFrame;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javax.sound.sampled.LineUnavailableException;
import static TextBase.SpeechRecognisers.*;
import static java.lang.System.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import TextBase.NoteLength;
import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Jervis {
    
    private static JFrame mainFrame;
            
    private static final String Voice = "kevin16";
    private static VoiceManager vm;
    private static Voice voice;
    private static String utterance;
    
    private static Owner owner;
    private static FileOutputStream serialOutput;
    
    private static SpeechRecogniser speechRecogniser;
    
    static NoteLength noteLength;
    
    public static boolean bAnimationStart = false;
   
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
       
        ExecutorService execServise = Executors.newCachedThreadPool();
        execServise.execute(new WaveFormAnim());
        
        speechRecogniser = new SpeechRecogniser();
        speechRecogniser.startRecognition();
        
        vm = VoiceManager.getInstance();
        voice = vm.getVoice(Voice);
        voice.allocate();
        
        mainFrame = new JFrame("J.E.R.V.I.S");
        mainFrame.getContentPane().add(new JPanelWithBackground("img/JervisBG.jpg"));
        mainFrame.setResizable(false);
        mainFrame.setSize( 1000, 778 );
        //mainFrame.setLayout(null); 
        //mainFrame.add(WaveFormAnim.label);
        //WaveFormAnim.label.setPreferredSize(new Dimension(200, 200));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
                        
        System.setProperty("mbrola.base", "D://J.E.R.V.I.S//Downloaded//mbrola//");     
        
        DateGenerator.initDateGenerator();
        Translator enFrTranslator = new Translator("en", "fr");
        
        //ownerBuilder = Owner.newBuilder();
        
        while (true) {
            utterance = speechRecogniser.getResult();

            System.out.println(utterance);//debug

            Jervis.bAnimationStart = true;
            if (utterance.contains("go away") ||
                utterance.contains("you can go")){
                voice.speak("Ok, I am gone, sir, Goodbye");
                exit(0);
            }
            else if (utterance.equals("jervis")){
                //delays for the time he speaks
                voice.speak("Yes, sir?");
                owner = Owner.parseFrom(new FileInputStream("JervisStorage.ser"));
                Thread.sleep(300);
                
                utterance = speechRecogniser.getResult();
                
                if (
                     utterance.equals("hello jervis")    ||
                     utterance.contains("good morning")  ||
                     utterance.contains("good afternoon")||
                     utterance.contains("good evening")  ||
                     utterance.startsWith("how are")){
                    
                    if (utterance.contains("Hello")){
                        voice.speak("Hello! Sir, how is life?");
                        actionConvers();
                    }
                    else if (utterance.contains("morning")){
                        voice.speak("Good morning! Sir, how is life?");
                        actionConvers();
                    }
                    else if (utterance.contains("afternoon")){
                        voice.speak("Good afternoon! Sir, how is life?");
                        actionConvers();
                    }
                    else if (utterance.contains("evening")){
                        voice.speak("Good evening! Sir, how is life?");
                        actionConvers();
                    }
                    else if (utterance.contains("how are")){
                        voice.speak("Always great, thank you. What about yourself?");
                        actionConvers();
                    }     
                }
                else if (utterance.contains("what is my name")) {
                    
                    if(!owner.hasName() || owner.getName().equals(" ")){
                        voice.speak("I do not know your name sir, please say your name");
                        
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
                        voice.speak("Your name is" + owner.getName() + "sir");
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
                        voice.speak("I do not know your sex sir, please say it now");
                        
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
                            voice.speak("The sex is invalid, you can be either male or female");
                        }
                        
                        speechRecogniser.startRecognition();
                    }
                    else{
                        voice.speak("You are a" + owner.getSex() + "sir");
                    }
                }
                else if (utterance.contains("what is my profession")) {
                    
                    if(!owner.hasProfession() || owner.getProfession().equals(" ")){
                        voice.speak("I do not know your profession sir, please say it now");
                        
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
                        voice.speak("You are " + owner.getProfession() + "sir");
                    }
                }
                else if (utterance.contains("what is my email")) {
                    
                    if(!owner.hasEmail() || owner.getEmail().equals(" ")){
                        voice.speak("I do not know your email sir, please say it now");
                        
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
                        voice.speak("Your email is " + owner.getEmail() + "sir");
                    }
                }
                else if (utterance.contains("what is my location")) {
                    
                    if(!owner.hasLocation() || owner.getLocation().equals(" ")){
                        voice.speak("I do not know your location sir, please say it now");
                        
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
                        voice.speak("Your location is " + owner.getLocation() + "sir");
                    }
                }
                else if (utterance.contains("remember my name")) {
                    voice.speak("Please say your name sir");
                    
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
                    voice.speak("Please state your sex sir");
                    
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
                    voice.speak("Please state your profession sir");
                    
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
                    voice.speak("Please state your email sir");
                    
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
                    voice.speak("Please state your location sir");
                    
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
                    voice.speak("My name is Jervis, a digital being");
                    Thread.sleep(1000);
                }
                else if (utterance.contains("the date")) {
                    voice.speak("Today's date is " + DateGenerator.readTodayDate());
                    Thread.sleep(1000);
                }
                else if(utterance.contains("remember event")){
                    voice.speak("What date sir?");
                }
                else if(utterance.contains("translate")){
                    voice.speak("I am listening for the source text sir");
                    speechRecogniser.stopRecognition();
                    
                    String sourceText = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                    voice.speak(enFrTranslator.translate(sourceText));
                    speechRecogniser.startRecognition();
                }
                else if(utterance.contains("open a website")){
                    voice.speak("What is the address sir?");
                    speechRecogniser.stopRecognition();
                    
                    String url = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                    
                    url = url.replace("dot", ".");
                    url = url.replace(" ", "");
                            
                    try {
                        Desktop.getDesktop().browse(new URL("http://" + url).toURI());
                    } catch (URISyntaxException | IOException e) {
                    }
                    
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
                    
                    voice.speak("short, medium or long, sir");
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
                    voice.speak("What should be the title, sir?");
                    
                    noteTitle = WatsonSpeechRecogniser.recognise(NoteLength.eWord);
                    
                    voice.speak("Ok, sir, I am listening for the content");
                    content = WatsonSpeechRecogniser.recognise(noteLength);
                    
                    NotepadWrapper.writeData(noteTitle, content);
                    voice.speak("Task completed, sir");
                    
                    speechRecogniser.startRecognition();
                }
                else if(utterance.contains("open location")){
                    voice.speak("What is the location?"); 
                }   
            }

            else if (utterance.contains("that was funny") ||
                     utterance.contains("that's funny")   ||
                     utterance.contains("that's funny")){
                voice.speak("Thank you, sir");
            }
            else if(utterance.contains("thank you") ||
                    utterance.contains("thanks")){
                voice.speak("Anytime, sir");
            }
            System.out.println(utterance);//debug
        }
    }
    
    public static void actionWeather(String utterance) 
            throws IOException, InterruptedException, LineUnavailableException{
        
        if(utterance.contains("in my location")){

        }
        else{
            //if location unknown
            voice.speak("Where about, sir?");
        }

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(ePLACE_GRMR_RCGNSR);
        speechRecogniser.startRecognition();

        //utterance = speechRecogniser.getResult();
        voice.speak(InformationFinder.weatherForecast(InformationFinder.Place.ePORTSMOUTH, 
                InformationFinder.Period.eOUTLOOK).replace("c", "degree in celsius"));
        Thread.sleep(1000);

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
    }
    
    public static void actionConvers() throws LineUnavailableException{
        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eCNVRS_GRMR_REGNSR);
        speechRecogniser.startRecognition();

        utterance = speechRecogniser.getResult();

        System.out.println(utterance);
        voice.speak(InformationFinder.quotesFinder(utterance.replace(' ', '+')));

        speechRecogniser.stopRecognition();
        speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
        speechRecogniser.startRecognition();
    }
   
    
}
