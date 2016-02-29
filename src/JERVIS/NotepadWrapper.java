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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class NotepadWrapper {
    
    /*  writeNoteData **************************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Writes specified data to to the notepad file
     * @param title
     * @param content
    ****************************************************************************/ 
    public static void writeNoteData(String title, String content){
        BufferedWriter writer = null;
        try{
            File file = new File(title + ".txt");

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
}
