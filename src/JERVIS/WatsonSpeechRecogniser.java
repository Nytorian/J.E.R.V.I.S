/******************************************************************************/
/**
@file          WatsonSpeechRecogniser.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Speech to Text Watson API wrapper for Jervis.
*******************************************************************************/
package JERVIS;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import javax.json.*;
import TextBase.NoteLength;
import java.io.StringReader;

public class WatsonSpeechRecogniser {

    private static String result;
    private static long RECORD_TIME;
    private static final String sUserName = TextBase.SensitiveData.WATSON_STT_LGN;
    private static final String sPassword = TextBase.SensitiveData.WATSON_STT_PSWD;
    
    public static String sManualInput;

    private static String[] tmp;
    
    /*  recognise **************************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Starts recognition
     * @param eLength
     * @return 
    ****************************************************************************/
    public static String recognise(NoteLength eLength){
        SpeechToText service = new SpeechToText();
        RecognizeOptions options = new RecognizeOptions().continuous(true);
        service.setUsernameAndPassword(sUserName, sPassword); 

        switch(eLength){
            case eWord:
                RECORD_TIME = MicrophoneRecorder.RECORD_TITLE;
                break;
            case eSHORT_NOTE:
                RECORD_TIME = MicrophoneRecorder.RECORD_SHORT;
                break;
            case eMEDIUM_NOTE:
                RECORD_TIME = MicrophoneRecorder.RECORD_MEDIUM;
                break;
            case eLONG_NOTE:
                RECORD_TIME = MicrophoneRecorder.RECORD_LONG;
                break;
            default:
                break;
        }

        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(RECORD_TIME);
            } catch (InterruptedException ex) {}

            MicrophoneRecorder.stop();
        });

        stopper.start();

        // startRecording recording
        MicrophoneRecorder.startRecording();
        
        SpeechResults transcript;

        try{
            transcript = service.recognize(MicrophoneRecorder.wavFile, options);
        } catch(Exception ex){
            Jervis.jervisSpeak("Watson Speech to Text engine seems to be down, please provide the input manually");
            textInputGUI TextInputGUI = new textInputGUI();
            
            return sManualInput;
        }
        
        String finalResult = "";
        
        try (JsonReader reader = Json.createReader(new StringReader(transcript.toString()))){
            JsonObject IBMresponse = reader.readObject();
            JsonArray resultsArray = IBMresponse.getJsonArray("results");
            try{
                JsonObject transcriptObject = resultsArray.getJsonObject(0);
                JsonArray alternativesArray = transcriptObject.getJsonArray("alternatives");
                JsonObject alternativesObject = alternativesArray.getJsonObject(0);
                finalResult = alternativesObject.getString("transcript");
            } catch(IndexOutOfBoundsException ex) {
                Jervis.jervisSpeak("Unfortunately I didn't get that, plese try again");
            }
        }
        
        return finalResult;
    }
}

