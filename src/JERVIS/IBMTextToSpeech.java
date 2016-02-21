/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JERVIS;

import java.util.List;
import TextBase.SensitiveData;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class IBMTextToSpeech {
    
    private String sVoice;
    
     public IBMTextToSpeech(String sVoice) throws LineUnavailableException, IOException{
        
        this.sVoice = sVoice;
        
        TextToSpeech service = new TextToSpeech();
        service.setUsernameAndPassword(SensitiveData.WATSON_TTS_LGN,
                                       SensitiveData.WATSON_TTS_PSWD
        );
              
        Clip clip = AudioSystem.getClip();
        InputStream inputStream = service.synthesize("Hello, I am Jervis", Voice.EN_LISA, "wav");
        clip.open((AudioInputStream) inputStream);
        clip.start();
    }   
}
