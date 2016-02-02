/******************************************************************************/
/**
@file          SpeechRecogniser.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Speech recognsiser for Jervis.
*******************************************************************************/
package JERVIS;

import java.io.IOException;
import TextBase.Enumerations;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import javax.sound.sampled.LineUnavailableException;
import static TextBase.Enumerations.*;

public class SpeechRecogniser {
    
    /* Class variable declaration */
    private static final String ACOUSTIC_MODEL = 
            "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY_PATH =
            "resource:/TextBase/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH =
            "resource:/TextBase/";
    private static final String LANGUAGE_MODEL = 
            "resource:/edu/cmu/sphinx/demo/dialog/weather.lm";//multiple lm support fix
    
    Configuration configuration;
            
    LiveSpeechRecognizer currentRecogniser;
    LiveSpeechRecognizer initialGramRecognizer;
    LiveSpeechRecognizer lmRecognizer;
    
    /*  Constructor ************************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Constructor for SpeechRecogniser class
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
    ****************************************************************************/            
    public SpeechRecogniser() throws IOException, LineUnavailableException{
        //voice.speak("Hi,I am Jervis, I was created by Mataeoosh Mihalski.");
        //voice.speak("I would like to serve you in anything you need");
        
        configuration = new Configuration();
        
        /* Initialise the grammar model recogniser for jerv */
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("initial");
        initialGramRecognizer = new LiveSpeechRecognizer(configuration);
        initialGramRecognizer.closeRecognitionLine();
        
        /* Initialise the language model recogniser */
        configuration.setUseGrammar(false);
        configuration.setLanguageModelPath(LANGUAGE_MODEL);
        lmRecognizer = new LiveSpeechRecognizer(configuration);
        lmRecognizer.closeRecognitionLine();
        
        currentRecogniser = initialGramRecognizer;
        lmRecognizer.closeRecognitionLine();
    }
    
    /*  setRecogniser **********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: changes the currentRecogniser - multi recogniser extention
     * @param eRecogniser
    ***************************************************************************/
    public void setRecogniser(Enumerations eRecogniser){
        
        currentRecogniser.closeRecognitionLine();
                        
        switch(eRecogniser){
            case eINITIAL_GRAMMAR_RECOGNISER:
                currentRecogniser = initialGramRecognizer;
                break;
            case eLM_RECOGNISER:
                currentRecogniser = lmRecognizer;
                break;
        }
    }

    /*  startRecognition *******************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: starts the currentRecogniser
     * @throws javax.sound.sampled.LineUnavailableException
    ***************************************************************************/
    public void startRecognition() throws LineUnavailableException{
        currentRecogniser.openRecognitionLine();
        currentRecogniser.startRecognition(true);
    }
    
    /*  stopRecognition ********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: stops the currentReconiser
    ***************************************************************************/
    public void stopRecognition(){
        currentRecogniser.closeRecognitionLine();
        currentRecogniser.stopRecognition();
    }
    
    /*  getResult ********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: returns the result of recognition
     * @return 
    ***************************************************************************/
    public String getResult(){
        return currentRecogniser.getResult().getHypothesis();
    }
}
