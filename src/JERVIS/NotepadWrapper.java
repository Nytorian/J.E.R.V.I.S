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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    
    /*  writeNoteData **************************************************************
    **  16/02/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Writes specified data to to the notepad file
     * @param title
     * @return 
     * @throws java.io.FileNotFoundException
    ****************************************************************************/ 
    public static String readNoteData(String title) throws FileNotFoundException, IOException{
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
}
