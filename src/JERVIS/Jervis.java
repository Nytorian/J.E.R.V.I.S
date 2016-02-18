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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Jervis {
    
    private static JFrame mainFrame;
            
    private static final String Voice = "kevin16";
    private static VoiceManager vm;
    private static Voice voice;
    private static String utterance;
    
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
        
        while (true) {
            utterance = speechRecogniser.getResult();

            System.out.println(utterance);//debug

            Jervis.bAnimationStart = true;
            if (utterance.contains("go away")){
                voice.speak("Ok, I am gone, sir, Goodbye");
                exit(0);
            }
            if (utterance.equals("jervis")){
                //delays for the time he speaks
                voice.speak("Yes, sir?");
                Thread.sleep(400);
                
                if (
                     utterance.equals("hello jervis")   ||
                     utterance.contains("good morning")  ||
                     utterance.contains("good afternoon")  ||
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
                utterance = speechRecogniser.getResult();
                
                if (utterance.contains("what is your name")) {
                    voice.speak("My name is Jervis, a digital being");
                    Thread.sleep(1000);
                }
                
                else if (utterance.contains("the date")) {
                    voice.speak("Today's date is " + DateGenerator.readTodayDate());
                    Thread.sleep(1000);
                }
                
                else if(utterance.contains("remember")){
                    voice.speak("What date sir?");
                }
                
                else if(utterance.contains("open a website")){
                    voice.speak("What is the address sir?");
                    speechRecogniser.stopRecognition();
                    
                    String url = WatsonSpeechRecogniser.recognise(NoteLength.eTITLE);
                    
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
                        noteLength = NoteLength.eSHORT;
                    }
                    else if(utterance.contains("medium")){
                        noteLength = NoteLength.eMEDIUM;
                    }
                    else if(utterance.contains("long")){
                        noteLength = NoteLength.eLONG;
                    }
                    
                    speechRecogniser.stopRecognition();
                    voice.speak("What should be the title, sir?");
                    
                    noteTitle = WatsonSpeechRecogniser.recognise(NoteLength.eTITLE);
                    
                    voice.speak("Ok, sir, I am listening for the content");
                    content = WatsonSpeechRecogniser.recognise(noteLength);
                    
                    NotepadWriter.write(noteTitle, content);
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
            else if(utterance.contains("thank you")){
                voice.speak("Anytime, sir");
            }
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
