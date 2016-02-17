/*
 * Copyright 1999-2004 Carnegie Mellon University.  
 * Portions Copyright 2004 Sun Microsystems, Inc.  
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package JERVIS;

import javax.sound.sampled.*;
import java.io.*;

/**
 * InputStream adapter
 */
public class MicrophoneRecorder {
    
    // record durations, in milliseconds
    static final long RECORD_TITLE  = 3000; //3sec
    static final long RECORD_SHORT  = 5000; //5sec
    static final long RECORD_MEDIUM = 10000; //10sec
    static final long RECORD_LONG   = 15000; //15sec
    
    public static File wavFile = new File("RecordBuffer.wav");
    public static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    public static AudioInputStream audioInputStream;
    public static TargetDataLine dataLine;
    public static AudioFormat audioFormat;
 
    /**
     * Defines an audio af
     * @return 
     */
    public static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return af;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    public static void start() {
        try {
            audioFormat = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
 
            // checks if system supports the data dataLine
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("The data line is not supported");
                System.exit(0);
            }
            dataLine = (TargetDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat);
            dataLine.start();   // start capturing
 
            audioInputStream = new AudioInputStream(dataLine);
 
            System.out.println("Started recording!");
 
            // start recording
            AudioSystem.write(audioInputStream, fileType, wavFile);
 
        } catch (LineUnavailableException | IOException ex) {}
    }
 
    /**
     * Closes the target data dataLine to stop capturing and recording
     */
    public static void stop(){
        dataLine.stop();
        dataLine.close();
        System.out.println("Stopped recording!");
    }
}
