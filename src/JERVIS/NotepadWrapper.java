/******************************************************************************/
/**
@file          NotepadWrapper.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Wrapper for Notepad operations 
*******************************************************************************/
package JERVIS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NotepadWrapper {
    
    /*  writeNoteData **************************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Writes passed data to the specified notepad file
     * @param title
     * @param content
    ****************************************************************************/ 
    public synchronized static void writeNoteData(String title, String content){
        BufferedWriter writer = null;
        try{
            File file = new File(title);

            // This will output the full path where the file will be written to...
            System.out.println(file.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } 
        catch (Exception e) {} 
        finally{
            try{
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {}
        }
    }
    
    /*  readNoteData **************************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Reads data from the file specified by the argument
     * @param title
     * @return 
     * @throws java.io.FileNotFoundException
    ****************************************************************************/ 
    public synchronized static String readNoteData(String title) throws FileNotFoundException, IOException{
        try(BufferedReader br = new BufferedReader(new FileReader(title))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            
            return sb.toString();
        }
    }
    
    /*  addToGram **************************************************************
    **  03/04/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Extends specified JSGF file by entires passed as sContent
     * argument.
     * @param sTitle
     * @param sContent
     * @throws java.io.FileNotFoundException
    ****************************************************************************/ 
    public synchronized static void addToGram(String sTitle, String sContent) throws FileNotFoundException, IOException{
        String sGramContent = readNoteData(sTitle);
        
        String [] arrGramContent = sGramContent.split("\\);");
        arrGramContent[0] += "| " + sContent + System.getProperty("line.separator");
        arrGramContent[0] += ");" + System.getProperty("line.separator");
        
        writeNoteData(sTitle, arrGramContent[0] + arrGramContent[1]);
    }
}
