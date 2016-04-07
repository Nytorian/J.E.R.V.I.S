/******************************************************************************/
/**
@file          SpeechRecogniser.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Speech recognsiser extention for Jervis.
*******************************************************************************/
package JERVIS;

import java.io.IOException;
import TextBase.SpeechRecognisers;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import javax.sound.sampled.LineUnavailableException;

public class SpeechRecogniser {
    
    /* Class variable declaration */
    private static final String ACOUSTIC_MODEL = 
            "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY_PATH =
            "resource:/TextBase/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH =
            "resource:/TextBase/";
    
    Configuration configuration;
            
    LiveSpeechRecognizer currentRecogniser, initialGramRecognizer, 
            placeGramRecognizer, conversationGramRecognizer,
            commandGramRecognizer, dateGramRecognizer, timeGramRecognizer, 
            yearGramRecognizer, minutesGramRecognizer;
    
    /*  Constructor ************************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Constructor for SpeechRecogniser class
     * @throws java.io.IOException
     * @throws javax.sound.sampled.LineUnavailableException
    ****************************************************************************/            
    public SpeechRecogniser() throws IOException, LineUnavailableException{
        
        configuration = new Configuration();
        
        /* Initialise the grammar model recognisers */
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("initial");
        initialGramRecognizer = new LiveSpeechRecognizer(configuration);
        initialGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("places");
        placeGramRecognizer = new LiveSpeechRecognizer(configuration);
        placeGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("conversation");
        conversationGramRecognizer = new LiveSpeechRecognizer(configuration);
        conversationGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("commands");
        commandGramRecognizer = new LiveSpeechRecognizer(configuration);
        commandGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("dateRecogniser");
        dateGramRecognizer = new LiveSpeechRecognizer(configuration);
        dateGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("timeRecogniser");
        timeGramRecognizer = new LiveSpeechRecognizer(configuration);
        timeGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("yearRecogniser");
        yearGramRecognizer = new LiveSpeechRecognizer(configuration);
        yearGramRecognizer.closeRecognitionLine();
        
        configuration.setUseGrammar(true);
        configuration.setGrammarName("minutesRecogniser");
        minutesGramRecognizer = new LiveSpeechRecognizer(configuration);
        minutesGramRecognizer.closeRecognitionLine();
        
        currentRecogniser = initialGramRecognizer;
    }
    
    /*  setRecogniser **********************************************************
    **  02/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: changes the currentRecogniser - multi recogniser extention
     * @param eRecogniser
    ***************************************************************************/
    public void setRecogniser(SpeechRecognisers eRecogniser){
        
        currentRecogniser.closeRecognitionLine();
                        
        switch(eRecogniser){
            case eINIT_GRMR_RCGNSR:
                currentRecogniser = initialGramRecognizer;
                break;
            case ePLACE_GRMR_RCGNSR:
                currentRecogniser = placeGramRecognizer;
                break;
            case eCNVRS_GRMR_RCGNSR:
                currentRecogniser = conversationGramRecognizer;
                break;
            case eCMD_GRMR_RCGNSR:
                currentRecogniser = commandGramRecognizer;
                break;
            case eDTE_GRMR_RCGNSR:
                currentRecogniser = dateGramRecognizer;
                break;
            case eTME_GRMR_RCGNSR:
                currentRecogniser = timeGramRecognizer;
                break;
            case eYR_GRMR_RCGNSR:
                currentRecogniser = yearGramRecognizer;
                break;
            case eMNT_GRMR_RCGNSR:
                currentRecogniser = minutesGramRecognizer;    
                break;
                
            default:
                System.out.println("Error in setRecogniser - no such recogniser");
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
