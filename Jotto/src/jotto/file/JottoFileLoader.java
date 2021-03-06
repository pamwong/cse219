package jotto.file;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import jotto.Jotto.JottoPropertyType;
import properties_manager.PropertiesManager;

/**
 * The JottoFileLoader class provides utilities for loading different
 * types of files. Static methods for loading images, text files,
 * and html files are provided. Note that these are just one and done
 * functions, this class has no instance or static variables.
 * 
 * @author Richard McKenna
 * @co-author Pamela Wong
 */
public class JottoFileLoader
{
    /**
     * Loads and returns the fully loaded imageFile image
     * 
     * @param window The frame that will be ultimately holding the
     * image. We need this for image loading because a JFrame can
     * serve as an image-loading monitor.
     * 
     * @param imageFile Image file name, without the full path, of the
     * image resource to be loaded.
     * 
     * @return A constructed, fully loaded image.
     * 
     * @throws IOException is thrown when the image cannot be loaded,
     * which is likely due to an improper file name or path.
     */
    public static Image loadImage(  String imageFile,
                                    JFrame window) throws IOException
    {
        // FIRST BUILD THE PATH TO THE IMAGE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        imageFile = props.getProperty(JottoPropertyType.IMG_PATH) + imageFile;

        // START THE IMAGE LOADING - NOTE THAT THIS PROCESS HAPPENS
        // ASYNCHRONOUSLY, MEANING THE IMAGE LOADS AS A BACKGROUND
        // PROCESS IN PARALLEL, SO IF WE'RE NOT CAREFUL, THE IMAGE
        // WILL NOT BE LOADED YET WHEN WE NEED IT.
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage(imageFile);
        
        // WE'LL USE A MEDIA TRACKER TO MAKE SURE THE IMAGE IS
        // FULLY LOADED BEFORE WE LET OUR APP MOVE ON
        MediaTracker tracker = new MediaTracker(window);
        try
        {
            // THESE ARE IMPORTANT, WE ARE TELLING SWING'S MediaTracker
            // OBJECT THAT THIS METHOD SHOULD WAIT FOR ALL THE IMAGE
            // DATA TO BE LOADED FROM THE FILE BEFORE THIS METHOD CONTINUES
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        }
        catch(InterruptedException ie)
        {
            // LET'S REFLECT THIS EXCEPTION BACK AS AN IOException
            throw new IOException(ie.getMessage());
        }
        
        // IF THE IMAGE NEVER LOADED, WE'LL THROW AN EXCEPTION
        if ((img == null) || (img.getWidth(null) <= 0))
        {
            String errorMessage = props.getProperty(JottoPropertyType.IMAGE_LOADING_ERROR_TEXT);
            throw new IOException(errorMessage);
        }

        // IF NO ERROR HAPPENED, RETURN THE FULLY LOADED IMAGE, WHICH CAN NOW BE USED
        return img;
    }
    
    /**
     * This method loads the complete contents of the textFile argument into
     * a String and returns it.
     * 
     * @param textFile The name of the text file to load. Note that the path
     * will be added by this method.
     * 
     * @return All the contents of the text file in a single String.
     * 
     * @throws IOException This exception is thrown when textFile is an invalid
     * file or there is some problem in accessing the file.
     */
    public static String loadTextFile(  String textFile) throws IOException
    {
        // ADD THE PATH TO THE FILE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        textFile = props.getProperty(JottoPropertyType.DATA_PATH) + textFile;
        
        // WE'LL ADD ALL THE CONTENTS OF THE TEXT FILE TO THIS STRING
        String textToReturn = "";
       
        // OPEN A STREAM TO READ THE TEXT FILE
        FileReader fr = new FileReader(textFile);
        BufferedReader reader = new BufferedReader(fr);
            
        // READ THE FILE, ONE LINE OF TEXT AT A TIME
        String inputLine = reader.readLine();
        while (inputLine != null)
        {
            // APPEND EACH LINE TO THE STRING
            textToReturn += inputLine + "\n";
            
            // READ THE NEXT LINE
            inputLine = reader.readLine();        
        }
        
        // RETURN THE TEXT
        return textToReturn;
    }    

    /**
     * Loads the HTML contents of the htmlFile argument into doc, which
     * manages an HTML in a tree data structure (i.e. a DOM).
     * 
     * @param htmlFile The HTML file to be loaded.
     * 
     * @param doc The Document Object Model this function will be loading
     * the html into.
     * 
     * @throws IOException is thrown when there is an error loading the
     * html document.
     */
    public static void loadHTMLDocument(    String htmlFile, 
                                            HTMLDocument doc) throws IOException
    {
        // OPEN THE STREAM
        FileReader fr = new FileReader(htmlFile);
        BufferedReader br = new BufferedReader(fr);
        
        // WE'LL USE A SWING PARSER FOR PARSING THE
        // HTML DOCUMENT SUCH THAT IT PROPERLY LOADS
        // THE DOCUMENT
        HTMLEditorKit.Parser parser = new ParserDelegator();
        
        // THE CALLBACK KEEPS TRACK OF WHEN IT COMPLETES LOADING
        HTMLEditorKit.ParserCallback callback = doc.getReader(0);
        
        // LOAD AND PARSE THE WEB PAGE
        parser.parse(br, callback, true);
    }
}