/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JERVIS;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import TextBase.NoteLength;

public class WatsonSpeechRecogniser {

    static String result;
    static String[] tmp;
    static long RECORD_TIME;
    
    public static String recognise(NoteLength eLength){
        SpeechToText service = new SpeechToText();
        service.setUsernameAndPassword("a0e09a7e-92a5-4d62-a553-0ad220d12e8b", "9Q7VBwVcqP1x"); 

        switch(eLength){
            case eTITLE:
                RECORD_TIME = MicrophoneRecorder.RECORD_TITLE;
                break;
            case eSHORT:
                RECORD_TIME = MicrophoneRecorder.RECORD_SHORT;
                break;
            case eMEDIUM:
                RECORD_TIME = MicrophoneRecorder.RECORD_MEDIUM;
                break;
            case eLONG:
                RECORD_TIME = MicrophoneRecorder.RECORD_SHORT;
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

        // start recording
        MicrophoneRecorder.start();

        SpeechResults transcript = service.recognize(MicrophoneRecorder.wavFile);

        result = transcript.toString();
        tmp = result.split(",");
        tmp = tmp[2].split(":");
        String finalResult = tmp[2].replace("\"", "");
        
        return finalResult;
    }
}
