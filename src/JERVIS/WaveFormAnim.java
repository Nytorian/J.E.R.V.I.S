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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
//http://stackoverflow.com/questions/17480765/refreshing-a-jlabel-icon-image
public class WaveFormAnim implements Runnable{
    
    public static JLabel label;

    private static List<BufferedImage> images;
    private static int currentPic = 0;
     
    public enum animationStates {eINIT_GRMR_RCGNSR };
    /* run *********************************************************************
    ** 09/11/2015  M.Michalski Initial Version
    ***************************************************************************/
    /** Description: Task for the threads in the Masked Brute Force functionality 
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
            
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER); 
        
        
        while(true){
            
            if(Jervis.bAnimationStart)
                currentPic++;
            else
                try {
                    Thread.sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaveFormAnim.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (currentPic >= images.size()) {
                currentPic = 0;
            }
            label.setIcon(new ImageIcon(images.get(currentPic)));
            try {
                Thread.sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaveFormAnim.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
