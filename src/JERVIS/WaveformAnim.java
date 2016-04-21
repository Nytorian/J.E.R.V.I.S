/******************************************************************************/
/**
@file          WaveFormAnim.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Speech recognsiser for Jervis.
*******************************************************************************/
package JERVIS;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class WaveformAnim implements Runnable{

    private static List<BufferedImage> images;
    private static int currentPic = 0;
    public static MappedByteBuffer interTaskCom;

    /* run *********************************************************************
    ** 09/11/2015  M.Michalski Initial Version
    ***************************************************************************/
    /** Description: Task for the wave form animation
    ***************************************************************************/
    @Override
    public void run(){
        
        images = new ArrayList<>(30);
        
        try {
            images.add(ImageIO.read(new File("img/Voice Animation/s0.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s1.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s2.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s3.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s4.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s5.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s6.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s7.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s8.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s9.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s10.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s11.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s12.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s13.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s14.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s15.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s16.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s17.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s18.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s19.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s20.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s21.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s22.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s23.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s24.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s25.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s26.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s27.jpg")));
            images.add(ImageIO.read(new File("img/Voice Animation/s28.jpg")));
            
        } catch (IOException exp) {} 
        
        while(true){
            
            if(Jervis.getStartAnim())
                currentPic++;
            else
                currentPic = 0;
            
            if (currentPic >= images.size()) {
                currentPic = 0;
            }
            mainGUI.lblAnim.setIcon(new ImageIcon(images.get(currentPic)));
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaveformAnim.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
