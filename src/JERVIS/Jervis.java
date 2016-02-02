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

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import java.io.IOException;
import javax.swing.JFrame;
import static java.lang.System.exit;
import javax.sound.sampled.LineUnavailableException;

public class Jervis {
    
    private static final String ACOUSTIC_MODEL = 
            "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY_PATH =
            "resource:/JERVIS/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH =
            "resource:/JERVIS/";
    private static final String LANGUAGE_MODEL = 
            "resource:/edu/cmu/sphinx/demo/dialog/weather.lm";

    /*  main *******************************************************************
    **  15/01/2016  M.Michalski Initial Version
    **  15/01/2016  M.Michalski Added GUI support
    **  25/01/2016  M.Michalski Added grammar recogniser and FreeTTS support
    **  27/01/2016  M.Michalski Added language model recogniser
    ***************************************************************************/
    /**Description: Main function for Jervis
     * @throws java.io.IOException
     * @param args *  
     * @throws java.lang.InterruptedException  
     * @throws javax.sound.sampled.LineUnavailableException  
    ****************************************************************************/
    public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException {
        JFrame mainFrame = new JFrame("J.E.R.V.I.S");
        mainFrame.getContentPane().add(new JPanelWithBackground("img/JervisBG.jpg"));
        mainFrame.setSize( 1000, 778 );
        mainFrame.setVisible(true);
        
        String Voice = "kevin16";
        System.setProperty("mbrola.base", "D://J.E.R.V.I.S//Downloaded//mbrola//");
        VoiceManager vm;
        vm = VoiceManager.getInstance();
        Voice voice;
        voice = vm.getVoice(Voice);
        voice.allocate();
        
        //voice.speak("Hi,I am Jervis, I was created by Mataeoosh Mihalski.");
        //voice.speak("I would like to serve you in anything you need");
        
        Configuration configuration = new Configuration();
        
        /* Initialise the grammar model recogniser for jerv */
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        String utterance, prevUtterance = "";
        configuration.setUseGrammar(true);
        configuration.setGrammarName("jerv");
        LiveSpeechRecognizer jsgfRecognizer =
            new LiveSpeechRecognizer(configuration);
        jsgfRecognizer.closeRecognitionLine();
        
        /* Initialise the language model recogniser */
        configuration.setUseGrammar(false);
        configuration.setLanguageModelPath(LANGUAGE_MODEL);
        LiveSpeechRecognizer lmRecognizer =
            new LiveSpeechRecognizer(configuration);
        lmRecognizer.closeRecognitionLine();
        
        jsgfRecognizer.openRecognitionLine();
        jsgfRecognizer.startRecognition(true);
        
        while (true) {
            utterance = jsgfRecognizer.getResult().getHypothesis();

            System.out.println(utterance);//debug

            if (utterance.contains("go away")){
                voice.speak("Ok, I am gone, sir, Goodbye");
                exit(0);
            }
            else if (utterance.equals("JERVIS")) {//delays for the time he speaks
                voice.speak("Yes, sir?");
                Thread.sleep(1000);
            }
            else if (utterance.equals("HELLO")) {
                voice.speak("Good day! Sir, how is life?");
                Thread.sleep(1000);
            }
            else if (utterance.startsWith("how are")) {
                voice.speak("I am great, thank you. What about yourself?");
                Thread.sleep(1000);
            }
            else if (utterance.contains("what is your name")) {
                voice.speak("My name is Jervis, a digital being");
                Thread.sleep(1000);
            }
            else if (utterance.startsWith("who do you")) {
                voice.speak("I love Roxanica");
                Thread.sleep(1000);
            }
            else if (utterance.startsWith("recognise language model")) {
                voice.speak("Ok sir, language model will be now used for recognition");
                Thread.sleep(1200);

                jsgfRecognizer.closeRecognitionLine();
                jsgfRecognizer.stopRecognition();
                lmRecognizer.openRecognitionLine();
                lmRecognizer.startRecognition(true);

                while (true) {
                    utterance = lmRecognizer.getResult().getHypothesis();
                    Thread.sleep(1200);

                    System.out.println(utterance);
                }
            }
        }
    }   
}
