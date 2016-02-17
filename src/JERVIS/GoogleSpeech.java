/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JERVIS;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GoogleResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javaFlacEncoder.FLACFileWriter;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author Mateusz
 */
public class GoogleSpeech implements Runnable{
    
    public static String utterance;
    public static boolean bStartRecognition = false;
    private final String API_KEY = "AIzaSyAFpfD8ZeKM13tolXOGLV5tG0ic7Z10VHQ";
    
    @Override
    public void run() {
    GSpeechDuplex dup = new GSpeechDuplex(API_KEY);//Instantiate the API
    dup.addResponseListener((GoogleResponse gr) -> {
        utterance = gr.getResponse();
    } // Adds the listener
    );
    Microphone mic = new Microphone(FLACFileWriter.FLAC); 
    // it record FLAC file.
    File file = new File("FlacBuffer.flac");//The File to record the buffer to. 
    while(true){
        try{
            mic.captureAudioToFile(file);//Begins recording
            mic.close();//Stops recording
            //Sends 10 second voice recording to Google
            byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());//Saves data into memory.
                    dup.recognize(data, (int)mic.getAudioFormat().getSampleRate());
            mic.getAudioFile().delete();//Deletes Buffer file
            
            Thread.sleep(1000);
        }
        catch(LineUnavailableException | InterruptedException | IOException ex){
        }
    }
}

public static String displayResponse(){
    if(utterance == null){
        System.out.println((String)null);
        return "";
    }
    return utterance;
}   
}
