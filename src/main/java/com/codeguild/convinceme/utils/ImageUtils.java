package com.codeguild.convinceme.utils;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * <p>Title: ImageUtils </p>
 * <p>Description: Utilities for working with images </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Codeguild</p>
 *
 * @author Patti Schank
 * @version 1.0
 */
public class ImageUtils {

    /**
     * a debug switch for finding images local files rather than in jar.
     * jar is used for production; default = false (reset to true in debug launch)
     */
    public static boolean sGetImagesLocally = false;

    private static ImageUtils sInstance = new ImageUtils();
    private static JPanel sDummyComponent = new JPanel();

    public static ImageUtils getInstance() {
        return sInstance;
    }

    public ImageUtils() {
        super();
    }

    /* Wait for an image to load
     * @param the component loading the image
     * @param the image to load
     */
    public static void waitForImage(Component component, Image image) {
        MediaTracker tracker = new MediaTracker(component);
        try {
            tracker.addImage(image, 0);
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            Debug.println("Interruption in loading image");
        }
    }

    /*
     * Get an image from a URL
     * @param the string for the URL
     * @return Image captured
     */
    public static Image getImageFromURL(String s) {
        Image image = null;
        try {
            URL url = new URL(s);
            image = Toolkit.getDefaultToolkit().getImage(url);
        } catch (MalformedURLException evt) {
            JOptionPane.showMessageDialog(new Frame(), "Invalid URL.", "Sorry", JOptionPane.ERROR_MESSAGE);
            Debug.println("Invalid image URL.");
        }
        return image;
    }

    /*
     * Get an image from a filename
     * @param the string for the filename
     * @return Image captured
     */
    public static Image getImageFromFilename(String s) {
        Image image = null;
        try {
            File f = new File(s);
            image = Toolkit.getDefaultToolkit().getImage(s);
            Debug.println("Local image file.");
        } catch (Exception e) {
            Debug.println("Bad file path");
        }
        return image;
    }


    /**
     * Retrieves an icon for an image in a resource.
     * A simple wrapper for {@link #getIconFromResource(String)}.
     *
     * @param resource The path to the resource.
     **/
    public static Icon getIconFromResource(String resource) {
        return new ImageIcon(Objects.requireNonNull(getImageFromResource(resource)));
    }

    /**
     * Retrieves an image from a resource.
     *
     * @param resource Path to the resource.
     * @return {@link Image} if the file corresponds to an image, null otherwise.
     */
    public static Image getImageFromResource(String resource) {
        try {
            return ImageIO.read(ImageUtils.class.getResource(resource));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
	 	
