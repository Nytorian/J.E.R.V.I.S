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
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Jervis {

    /*  main *******************************************************************
    **  15/01/2016  M.Michalski Initial Version
    **  15/01/2016  M.Michalski Added GUI support
    **  25/01/2016  M.Michalski Added grammar recogniser and FreeTTS support
    **  27/01/2016  M.Michalski Added language model recogniser
    **  02/02/2016  M.Michalski Moved speech recognisers to its own class
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
        
        while (true) {
            utterance = speechRecogniser.getResult();

            System.out.println(utterance);//debug

            if (utterance.contains("go away")){
                voice.speak("Ok, I am gone, sir, Goodbye");
                exit(0);
            }
            else if (utterance.equals("jervis")) {//delays for the time he speaks
                voice.speak("Yes, sir?");
                Thread.sleep(1000);
            }
            else if (utterance.equals("hello")) {
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
            
            else if (utterance.contains("date")) {
                 
                // Create an instance of SimpleDateFormat used for formatting 
                // the string representation of date (month/day/year)
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                // Get the date today using Calendar object.
                Date today = Calendar.getInstance().getTime();        
                // Using DateFormat format method we can create a string 
                // representation of a date with the defined format.
                String reportDate = df.format(today);
                reportDate = reportDate.replace("0", "");
                String finalDate[] = reportDate.split("/");
                       
                DateFormatSymbols dfs = new DateFormatSymbols();
                String[] months = dfs.getMonths();
        
                int i = Integer.parseInt(finalDate[1]);
                if (i >= 1 && i <= 12 ) {
                    finalDate[1] = months[i - 1];
                }
                
                voice.speak("Today's date is " + finalDate[0]+ finalDate[1] +
                        finalDate[2]);
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
