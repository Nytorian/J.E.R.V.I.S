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
import static TextBase.Enumerations.*;
import static java.lang.System.*;

public class Jervis {

    /*  main *******************************************************************
    **  15/01/2016  M.Michalski Initial Version
    **  15/01/2016  M.Michalski Added GUI support
    **  25/01/2016  M.Michalski Added grammar recogniser and FreeTTS support
    **  27/01/2016  M.Michalski Added language model recogniser
    **  02/02/2016  M.Michalski Moved speech recognisers to its own class
    **  02/02/2016  M.Michalski Added what's the date query 
    **  09/02/2016  M.Michalski Chat support (quote finder) 
    **  09/02/2016  M.Michalski Weather forecast support
    ***************************************************************************/
    /**Description: Main function for Jervis
     * @throws java.io.IOException
     * @param args *  
     * @throws javax.sound.sampled.LineUnavailableException  
     * @throws java.lang.InterruptedException  
    ****************************************************************************/
    public static void main(String[] args) throws IOException,
            LineUnavailableException, InterruptedException {
        
        JFrame mainFrame = new JFrame("J.E.R.V.I.S");
        mainFrame.getContentPane().add(new JPanelWithBackground("img/JervisBG.jpg"));
        mainFrame.setResizable(false);
        mainFrame.setSize( 1000, 778 );
        mainFrame.setVisible(true);
        
        String Voice = "kevin16";
        System.setProperty("mbrola.base", "D://J.E.R.V.I.S//Downloaded//mbrola//");
        VoiceManager vm;
        vm = VoiceManager.getInstance();
        Voice voice;
        voice = vm.getVoice(Voice);
        voice.allocate();
        
        SpeechRecogniser speechRecogniser = new SpeechRecogniser();
        speechRecogniser.startRecognition();
        String utterance;
        
        DateGenerator.initDateGenerator();
        
        while (true) {
            utterance = speechRecogniser.getResult();

            System.out.println(utterance);//debug

            if (utterance.contains("go away")){
                voice.speak("Ok, I am gone, sir, Goodbye");
                exit(0);
            }
                
            else if (utterance.equals("jervis")  ||
                     utterance.equals("hello")   ||
                     utterance.contains("good")  ||
                     utterance.startsWith("how are"))
            {
                switch (utterance) {
                    case "jervis":
                        //delays for the time he speaks
                        voice.speak("Yes, sir?");
                        break;
                    case "hello":
                    case "good" :
                        voice.speak("Good day! Sir, how is life?");
                        break;
                    case "how are":
                        voice.speak("Always great, thank you. What about yourself?");
                        break;
                    default:
                        break;
                }

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
            else if (utterance.contains("what is your name")) {
                voice.speak("My name is Jervis, a digital being");
                Thread.sleep(1000);
            }
            else if(utterance.contains("the weather")){
                //voice.speak("Where about, sir?");

                speechRecogniser.stopRecognition();
                speechRecogniser.setRecogniser(ePLACE_GRMR_RCGNSR);
                speechRecogniser.startRecognition();

                //utterance = speechRecogniser.getResult();
                voice.speak(InformationFinder.weatherForecast(InformationFinder.Place.ePORTSMOUTH, 
                        InformationFinder.Period.eOUTLOOK));
                Thread.sleep(1000);

                speechRecogniser.stopRecognition();
                speechRecogniser.setRecogniser(eINIT_GRMR_RCGNSR);
                speechRecogniser.startRecognition();
            }

            else if (utterance.contains("that was funny") ||
                     utterance.contains("that's funny")   ||
                     utterance.contains("that's funny")){
                voice.speak("Thank you, sir");
            }

            else if (utterance.contains("date")) {
                voice.speak("Today's date is " + DateGenerator.readTodayDate());
                Thread.sleep(1000);
            }
    
            else if (utterance.startsWith("recognise language model")) {
                voice.speak("Ok sir, language model will be now used for recognition");
                Thread.sleep(2000);

                speechRecogniser.setRecogniser(eLM_RECOGNISER);
                speechRecogniser.startRecognition();

                while (true) {
                    utterance = speechRecogniser.getResult();
                    Thread.sleep(1200);

                    System.out.println(utterance);
                }
            }
        }
    }   
}
