/******************************************************************************/
/**
@file          MicrophoneRecorder.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Microphone Recorder - records input from the default microphone 
*              and encodes it in WAV format.
*******************************************************************************/

package JERVIS;

import javax.sound.sampled.*;
import java.io.*;

public class MicrophoneRecorder {
    
    // record durations, in milliseconds
    static final long RECORD_TITLE  = 3000; //3sec
    static final long RECORD_SHORT  = 5000; //5sec
    static final long RECORD_MEDIUM = 10000; //10sec
    static final long RECORD_LONG   = 15000; //15sec
    
    public static File wavFile = new File("RecordBuffer.wav");
    
    private static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private static AudioInputStream audioInputStream;
    private static TargetDataLine dataLine;
    private static AudioFormat audioFormat;
 
    /*  startRecording *********************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Records the microphone input using WAV encoding
    ***************************************************************************/  
    public static void startRecording() {
        try {
            audioFormat = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
 
            if (!AudioSystem.isLineSupported(info)){
                System.out.println("The data line is not supported");
                System.exit(0);
            }
            
            dataLine = (TargetDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat);
            dataLine.start();
 
            audioInputStream = new AudioInputStream(dataLine);
 
            System.out.println("Recording Started!");//debug
 
            //start recording from the input stream
            AudioSystem.write(audioInputStream, fileType, wavFile);
 
        } catch (LineUnavailableException | IOException ex) {}
    }
    
    /*  getAudioFormat *********************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Defines and returns audio format
     * @return 
    ***************************************************************************/  
    private static AudioFormat getAudioFormat(){
        
        float sampleRate  = 16000;
        int sampleSize    = 8;
        int nofChannels   = 1;//mono (no stereo required and easier to process)
        boolean signed    = true;
        boolean bigEndian = true;
        
        AudioFormat format = new AudioFormat(sampleRate, sampleSize, 
                nofChannels, signed, bigEndian);
        
        return format;
    }
 
    /*  getAudioFormat *********************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Closes the data line hence stops microphone recording
    ***************************************************************************/
    public static void stop(){
        dataLine.stop();
        dataLine.close();
        System.out.println("Recording Stopped!");//debug
    }
}
