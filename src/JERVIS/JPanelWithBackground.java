package JERVIS;

/******************************************************************************/
/**
@file          JPanelWithBackground.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Child for JPanel. Introduces background extention.
*******************************************************************************/
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class JPanelWithBackground extends JPanel{

    private final Image backgroundImage;

    /*  JPanelWithBackground ***************************************************
    **  15/01/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Constructor of JPanelWithBackground
     * @param fileName
     * @throws java.io.IOException
    ****************************************************************************/
    public JPanelWithBackground(String fileName) throws IOException {
        backgroundImage = ImageIO.read(new File(fileName));
    }

    /*  paintComponent ***************************************************
    **  15/01/2016  M.Michalski Initial Version
    ***************************************************************************/
    /**Description: Constructor of JPanelWithBackground
     * @param g
    ****************************************************************************/
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image.
        g.drawImage(backgroundImage, 0, 0, this);
    }
}
