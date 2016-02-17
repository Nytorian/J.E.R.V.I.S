/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JERVIS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Mateusz
 */
public class NotepadWriter {
    
    public static void write(String title, String content){
        BufferedWriter writer = null;
        try {
            File logFile = new File(title + ".txt");

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(content);
        } catch (Exception e) {
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }  
}
