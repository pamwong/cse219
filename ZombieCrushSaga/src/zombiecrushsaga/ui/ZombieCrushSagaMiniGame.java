package zombiecrushsaga.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import mini_game.MiniGame;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import zombiecrushsaga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import zombiecrushsaga.data.ZombieCrushSagaLevelRecord;
import zombiecrushsaga.file.ZombieCrushSagaFileManager;
import zombiecrushsaga.data.ZombieCrushSagaRecord;
import zombiecrushsaga.events.*;


/**
 * This is the actual mini game, as extended from the mini game framework. It
 * manages all the UI elements.
 * 
 * @author Richard McKenna
 */
public class ZombieCrushSagaMiniGame extends MiniGame
{
    // THE PLAYER RECORD FOR EACH LEVEL, WHICH LIVES BEYOND ONE SESSION
    private ZombieCrushSagaRecord record;
    
    // HANDLES ERROR CONDITIONS
    private ZombieCrushSagaErrorHandler errorHandler;
    
    // MANAGES LOADING OF LEVELS AND THE PLAYER RECORDS FILES
    private ZombieCrushSagaFileManager fileManager;
    
    // THE SCREEN CURRENTLY BEING PLAYED
    private String currentScreenState;
    
    // ACCESSOR METHODS
        // - getPlayerRecord
        // - getErrorHandler
        // - getFileManager
        // - isCurrentScreenState
    
    /**
     * Accessor method for getting the player record object, which
     * summarizes the player's record on all levels.
     * 
     * @return The player's complete record.
     */
    public ZombieCrushSagaRecord getPlayerRecord() 
    { 
        return record; 
    }

    /**
     * Accessor method for getting the application's error handler.
     * 
     * @return The error handler.
     */
    public ZombieCrushSagaErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Accessor method for getting the app's file manager.
     * 
     * @return The file manager.
     */
    public ZombieCrushSagaFileManager getFileManager()
    {
        return fileManager;
    }

    /**
     * Used for testing to see if the current screen state matches
     * the testScreenState argument. If it mates, true is returned,
     * else false.
     * 
     * @param testScreenState Screen state to test against the 
     * current state.
     * 
     * @return true if the current state is testScreenState, false otherwise.
     */
    public boolean isCurrentScreenState(String testScreenState)
    {
        return testScreenState.equals(currentScreenState);
    }

    // SERVICE METHODS
        // - displayStats
        // - savePlayerRecord
        // - switchToGameScreen
        // - switchToSplashScreen
        // - updateBoundaries
   
    /**
     * This method displays makes the stats dialog display visible,
     * which includes the text inside.
     */
    public void displayStats()
    {
        // MAKE SURE ONLY THE PROPER DIALOG IS VISIBLE
        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(STATS_DIALOG_TYPE).setState(VISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(TRY_AGAIN_TYPE).setState(INVISIBLE_STATE);

    }
    
    /**
     * This method forces the file manager to save the current player record.
     */
    public void savePlayerRecord()
    {
        // THIS CURRENTLY DOES NOTHING, INSTEAD, IT MUST SAVE ALL THE
        // PLAYER RECORDS IN THE SAME FORMAT IT IS BEING LOADED
        
        ZombieCrushSagaRecord recordToSave = getPlayerRecord();
        try {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
        String recordPath = dataPath + props.getProperty(ZombieCrushSagaPropertyType.RECORD_FILE_NAME);
        File fileToSave = new File(recordPath);
        byte[] bytes = recordToSave.toByteArray();
        
        FileOutputStream fos = new FileOutputStream(fileToSave);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(bytes);
        bos.close();
        }
        catch (Exception e) {
            // STUFF
        }
        
    }
    
    /**
     * This method switches the application to the game screen, making
     * all the appropriate UI controls visible & invisible.
     * 
     * THIS SCREEN IS REALLY THE SAGA SCREEN!!
     */
    public void switchToGameScreen()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(GAME_SCREEN_STATE);
        
        // DISABLE SPLASH SCREEN, WE CAN NEVER GO BACK
        guiButtons.get(PLAY_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_GAME_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(RESET_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(RESET_GAME_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(QUIT_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(QUIT_GAME_BUTTON_TYPE).setEnabled(false);
       
        // AND CHANGE THE SCREEN STATE
        currentScreenState = GAME_SCREEN_STATE;
      
    }
    
    public void switchToSagaScreen()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // CHANGE THE BACKGROUND
        guiDecor.get(SAGA_TYPE).setState(SAGA_SCREEN_STATE);
        guiDecor.get(TOOLBAR_TYPE).setState(VISIBLE_STATE);
        
        // DISABLE SPLASH SCREEN, WE CAN NEVER GO BACK
        guiButtons.get(PLAY_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_GAME_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(RESET_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(RESET_GAME_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(QUIT_GAME_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(QUIT_GAME_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCROLL_UP_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCROLL_UP_TYPE).setEnabled(true);
        guiButtons.get(SCROLL_DOWN_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCROLL_DOWN_TYPE).setEnabled(true);
       
       
       
       
        // AND CHANGE THE SCREEN STATE
        currentScreenState = SAGA_SCREEN_STATE;
    }
    
    public void scrollUp()
    {

        if(SAGA_Y < 0)
        SAGA_Y = SAGA_Y + 50;
        guiDecor.get(SAGA_TYPE).setY(SAGA_Y);
    }
    
    public void scrollDown()
    {
   
        if(SAGA_Y > -2900)
        SAGA_Y = SAGA_Y - 50;
        guiDecor.get(SAGA_TYPE).setY(SAGA_Y);    
        
    }
    /**
     * This method switches the application to the splash screen, making
     * all the appropriate UI controls visible & invisible.
     */    
    public void switchToSplashScreen()
    {
        // THIS NEVER EXISTS 
    }
    
    /**
     * This method switches to application to the chosen level screen
     */
    public void switchToLevelScreen()
    {
        
    }
    
    /**
     * This method updates the game grid boundaries, which will depend
     * on the level loaded.
     */    
    public void updateBoundaries()
    {
        // NOTE THAT THE ONLY ONES WE CARE ABOUT ARE THE LEFT & TOP BOUNDARIES
        float totalWidth = ((ZombieCrushSagaDataModel)data).getGridColumns() * TILE_IMAGE_WIDTH;
        float halfTotalWidth = totalWidth/2.0f;
        float halfViewportWidth = data.getGameWidth()/2.0f;
        boundaryLeft = halfViewportWidth - halfTotalWidth;

        // THE LEFT & TOP BOUNDARIES ARE WHERE WE START RENDERING TILES IN THE GRID
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        float topOffset = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_TOP_OFFSET.toString()));
        float totalHeight = ((ZombieCrushSagaDataModel)data).getGridRows() * TILE_IMAGE_HEIGHT;
        float halfTotalHeight = totalHeight/2.0f;
        float halfViewportHeight = (data.getGameHeight() - topOffset)/2.0f;
        boundaryTop = topOffset + halfViewportHeight - halfTotalHeight;
    }
    
    // METHODS OVERRIDDEN FROM MiniGame
        // - initAudioContent
        // - initData
        // - initGUIControls
        // - initGUIHandlers
        // - reset
        // - updateGUI

    @Override
    /**
     * Initializes the sound and music to be used by the application.
     */
    public void initAudioContent()
    {

    }

    /**
     * This helper method loads the audio file associated with audioCueType,
     * which should have been specified via an XML properties file.
     */
    private void loadAudioCue(ZombieCrushSagaPropertyType audioCueType) 
            throws  UnsupportedAudioFileException, IOException, LineUnavailableException, 
                    InvalidMidiDataException, MidiUnavailableException
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String audioPath = props.getProperty(ZombieCrushSagaPropertyType.AUDIO_PATH);
        String cue = props.getProperty(audioCueType.toString());
        audio.loadAudio(audioCueType.toString(), audioPath + cue);        
    }
    
    /**
     * Initializes the game data used by the application. Note
     * that it is this method's obligation to construct and set
     * this Game's custom GameDataModel object as well as any
     * other needed game objects.
     */
    @Override
    public void initData()
    {        
        // INIT OUR ERROR HANDLER
        errorHandler = new ZombieCrushSagaErrorHandler(window);
        
        // INIT OUR FILE MANAGER
        fileManager = new ZombieCrushSagaFileManager(this);

        // LOAD THE PLAYER'S RECORD FROM A FILE
        record = fileManager.loadRecord();
        
        // INIT OUR DATA MANAGER
        data = new ZombieCrushSagaDataModel(this);

        // LOAD THE GAME DIMENSIONS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        int gameWidth = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_WIDTH.toString()));
        int gameHeight = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_HEIGHT.toString()));
        data.setGameDimensions(gameWidth, gameHeight);

        // THIS WILL CHANGE WHEN WE LOAD A LEVEL
        boundaryLeft = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_LEFT_OFFSET.toString()));
        boundaryTop = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_TOP_OFFSET.toString()));
        boundaryRight = gameWidth - boundaryLeft;
        boundaryBottom = gameHeight;
    }
    
    /**
     * Initializes the game controls, like buttons, used by
     * the game application. Note that this includes the tiles,
     * which serve as buttons of sorts.
     */
    @Override
    public void initGUIControls()
    {
        // WE'LL USE AND REUSE THESE FOR LOADING STUFF
        BufferedImage img;
        float x, y;
        SpriteType sT;
        Sprite s;
 
        // FIRST PUT THE ICON IN THE WINDOW
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(ZombieCrushSagaPropertyType.IMG_PATH);        
        String windowIconFile = props.getProperty(ZombieCrushSagaPropertyType.WINDOW_ICON);
        img = loadImage(imgPath + windowIconFile);
        window.setIconImage(img);

        // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
        canvas = new ZombieCrushSagaPanel(this, (ZombieCrushSagaDataModel)data);
        
        // LOAD THE BACKGROUNDS, WHICH ARE GUI DECOR
        currentScreenState = SPLASH_SCREEN_STATE;
        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.SPLASH_SCREEN_IMAGE_NAME));
        sT = new SpriteType(BACKGROUND_TYPE);
        sT.addState(SPLASH_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, SPLASH_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);
        
        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.SAGA_SCREEN_IMAGE_NAME));
        sT = new SpriteType(SAGA_TYPE);
        sT.addState(SAGA_SCREEN_STATE, img);
        s = new Sprite(sT, SAGA_X, SAGA_Y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(SAGA_TYPE, s);
        

        
        
        // ADD SPLASH SCREEN BUTTONS
        // PLAY BUTTON
        String playButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_BUTTON_IMAGE_NAME);
        sT = new SpriteType(PLAY_GAME_BUTTON_TYPE);
	img = loadImage(imgPath + playButton);
        sT.addState(VISIBLE_STATE, img);
        String playMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + playMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, PLAY_BUTTON_X, PLAY_BUTTON_Y, 0, 0, VISIBLE_STATE);
        guiButtons.put(PLAY_GAME_BUTTON_TYPE, s);
        
        // RESET BUTTON
        String resetButton = props.getProperty(ZombieCrushSagaPropertyType.RESET_BUTTON_IMAGE_NAME);
        sT = new SpriteType(RESET_GAME_BUTTON_TYPE);
	img = loadImage(imgPath + resetButton);
        sT.addState(VISIBLE_STATE, img);
        String resetMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.RESET_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + resetMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, RESET_BUTTON_X, RESET_BUTTON_Y, 0, 0, VISIBLE_STATE);
        guiButtons.put(RESET_GAME_BUTTON_TYPE, s);
        
        // QUIT BUTTON
        String quitButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_IMAGE_NAME);
        sT = new SpriteType(QUIT_GAME_BUTTON_TYPE);
	img = loadImage(imgPath + quitButton);
        sT.addState(VISIBLE_STATE, img);
        String quitMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + quitMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, QUIT_BUTTON_X, QUIT_BUTTON_Y, 0, 0, VISIBLE_STATE);
        guiButtons.put(QUIT_GAME_BUTTON_TYPE, s);
        
        // LOAD TOOLBAR
        String toolbar = props.getProperty(ZombieCrushSagaPropertyType.TOOLBAR_IMAGE_NAME);
        sT = new SpriteType(TOOLBAR_TYPE);
        img = loadImage(imgPath + toolbar);
        sT.addState(VISIBLE_STATE, img);
        s = new Sprite(sT, TOOLBAR_X, TOOLBAR_Y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(TOOLBAR_TYPE, s);
        
        // AND SCROLL UP
        String scrollupButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_UP_IMAGE_NAME);
        sT = new SpriteType(SCROLL_UP_TYPE);
	img = loadImage(imgPath + scrollupButton);
        sT.addState(VISIBLE_STATE, img);
        String scrollupMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_UP_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + scrollupMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SCROLLUP_X, SCROLLUP_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SCROLL_UP_TYPE, s);
        
        // AND SCROLL DOWN
        String scrolldownButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_DOWN_IMAGE_NAME);
        sT = new SpriteType(SCROLL_DOWN_TYPE);
	img = loadImage(imgPath + scrolldownButton);
        sT.addState(VISIBLE_STATE, img);
        String scrolldownMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_DOWN_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + scrolldownMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SCROLLDOWN_X, SCROLLDOWN_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SCROLL_DOWN_TYPE, s);
        
        
        
      
    }		
    
    /**
     * Initializes the game event handlers for things like
     * game gui buttons.
     */
    @Override
    public void initGUIHandlers()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
        
        // WE'LL HAVE A CUSTOM RESPONSE FOR WHEN THE USER CLOSES THE WINDOW
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ExitHandler eh = new ExitHandler(this);
        window.addWindowListener(eh);
        
        PlayGameHandler ngh = new PlayGameHandler(this);
        guiButtons.get(PLAY_GAME_BUTTON_TYPE).setActionListener(ngh);
        
        QuitHandler qh = new QuitHandler(this);
        guiButtons.get(QUIT_GAME_BUTTON_TYPE).setActionListener(qh);
        
        ScrollDownHandler sdh = new ScrollDownHandler(this);
        guiButtons.get(SCROLL_DOWN_TYPE).setActionListener(sdh);
        
        ScrollUpHandler suh = new ScrollUpHandler(this);
        guiButtons.get(SCROLL_UP_TYPE).setActionListener(suh);
    }
    
    /**
     * Invoked when a new game is started, it resets all relevant
     * game data and gui control states. 
     */
    @Override
    public void reset()
    {
        data.reset(this);
    }
    
    /**
     * Updates the state of all gui controls according to the 
     * current game conditions.
     */
    @Override
    public void updateGUI()
    {
        // GO THROUGH THE VISIBLE BUTTONS TO TRIGGER MOUSE OVERS
        Iterator<Sprite> buttonsIt = guiButtons.values().iterator();
        while (buttonsIt.hasNext())
        {
            Sprite button = buttonsIt.next();
            
            // ARE WE ENTERING A BUTTON?
            if (button.getState().equals(VISIBLE_STATE))
            {
                if (button.containsPoint(data.getLastMouseX(), data.getLastMouseY()))
                {
                    button.setState(MOUSE_OVER_STATE);
                }
            }
            // ARE WE EXITING A BUTTON?
            else if (button.getState().equals(MOUSE_OVER_STATE))
            {
                 if (!button.containsPoint(data.getLastMouseX(), data.getLastMouseY()))
                {
                    button.setState(VISIBLE_STATE);
                }
            }
        }
    }    
}