package mahjong_solitaire.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JPanel;
import mahjong_solitaire.MahjongSolitaire;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import mahjong_solitaire.data.MahjongSolitaireDataModel;
import static mahjong_solitaire.MahjongSolitaireConstants.*;
import mahjong_solitaire.data.MahjongSolitaireRecord;
import mahjong_solitaire.file.MahjongSolitaireFileManager;
import properties_manager.PropertiesManager;

/**
 * This class performs all of the rendering for the Mahjong game application.
 * 
 * @author Richard McKenna
 */
public class MahjongSolitairePanel extends JPanel
{
    // THIS IS ACTUALLY OUR Mahjong Solitaire APP, WE NEED THIS
    // BECAUSE IT HAS THE GUI STUFF THAT WE NEED TO RENDER
    private MiniGame game;
    
    // AND HERE IS ALL THE GAME DATA THAT WE NEED TO RENDER
    private MahjongSolitaireDataModel data;
    
    // WE'LL USE THIS TO FORMAT SOME TEXT FOR DISPLAY PURPOSES
    private NumberFormat numberFormatter;
 
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING UNSELECTED TILES
    private BufferedImage blankTileImage;
    
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING SELECTED TILES
    private BufferedImage blankTileSelectedImage;
    
    /**
     * This constructor stores the game and data references,
     * which we'll need for rendering.
     * 
     * @param initGame the Mahjong Solitaire game that is using
     * this panel for rendering.
     * 
     * @param initData the Mahjong Solitaire game data.
     */
    public MahjongSolitairePanel(MiniGame initGame, MahjongSolitaireDataModel initData)
    {
        game = initGame;
        data = initData;
        numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(3);
        numberFormatter.setMaximumFractionDigits(3);
    }
    
    // MUTATOR METHODS
        // -setBlankTileImage
        // -setBlankTileSelectedImage
    
    /**
     * This mutator method sets the base image to use for rendering tiles.
     * 
     * @param initBlankTileImage The image to use as the base for rendering tiles.
     */
    public void setBlankTileImage(BufferedImage initBlankTileImage)
    {
        blankTileImage = initBlankTileImage;
    }
    
    /**
     * This mutator method sets the base image to use for rendering selected tiles.
     * 
     * @param initBlankTileSelectedImage The image to use as the base for rendering
     * selected tiles.
     */
    public void setBlankTileSelectedImage(BufferedImage initBlankTileSelectedImage)
    {
        blankTileSelectedImage = initBlankTileSelectedImage;
    }

    /**
     * This is where rendering starts. This method is called each frame, and the
     * entire game application is rendered here with the help of a number of
     * helper methods.
     * 
     * @param g The Graphics context for this panel.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        try
        {
            // MAKE SURE WE HAVE EXCLUSIVE ACCESS TO THE GAME DATA
            game.beginUsingData();
        
            // CLEAR THE PANEL
            super.paintComponent(g);
        
            // RENDER THE BACKGROUND, WHICHEVER SCREEN WE'RE ON
            renderBackground(g);

            // AND THE BUTTONS AND DECOR
            renderGUIControls(g);
                
            // AND THE TILES
            renderTiles(g);

            // AND THE DIALOGS, IF THERE ARE ANY
            renderDialogs(g);

            // AND THE TIME AND TILES STATS
            renderStats(g);
                
            // RENDERING THE GRID WHERE ALL THE TILES GO CAN BE HELPFUL
            // DURING DEBUGGIN TO BETTER UNDERSTAND HOW THEY RE LAID OUT
            renderGrid(g);
        
            // AND FINALLY, TEXT FOR DEBUGGING
            renderDebuggingText(g);
        }
        finally
        {
            // RELEASE THE LOCK
            game.endUsingData();    
        }
    }
    
    // RENDERING HELPER METHODS
        // - renderBackground
        // - renderGUIControls
        // - renderTiles
        // - renderDialogs
        // - renderGrid
        // - renderDebuggingText
    
    /**
     * Renders the background image, which is different depending on the screen. 
     * 
     * @param g the Graphics context of this panel.
     */
    public void renderBackground(Graphics g)
    {
        // THERE IS ONLY ONE CURRENTLY SET
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        renderSprite(g, bg);
    }

    /**
     * Renders all the GUI decor and buttons.
     * 
     * @param g this panel's rendering context.
     */
    public void renderGUIControls(Graphics g)
    {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> decorSprites = game.getGUIDecor().values();
        for (Sprite s : decorSprites)
        {
            renderSprite(g, s);
        }
        
        // AND NOW RENDER THE BUTTONS
        Collection<Sprite> buttonSprites = game.getGUIButtons().values();
        for (Sprite s : buttonSprites)
        {
            renderSprite(g, s);
        }
    }

    
    /**
     * This method renders the on-screen stats that change as
     * the game progresses. This means things like the game time
     * and the number of tiles remaining.
     * 
     * @param g the Graphics context for this panel
     */
    public void renderStats(Graphics g)
    {
        // RENDER THE GAME TIME
        if (((MahjongSolitaireMiniGame)game).isCurrentScreenState(GAME_SCREEN_STATE) 
                && data.inProgress() || data.isPaused())
        {
            // RENDER THE TIME
            String time = data.gameTimeToText();
            int x = TIME_X + TIME_OFFSET;
            int y = TIME_Y + TIME_TEXT_OFFSET;
            g.setFont(TEXT_DISPLAY_FONT);
            g.setColor(Color.BLACK);
            g.drawString(time, x, y);
            
        }
        if (((MahjongSolitaireMiniGame)game).isCurrentScreenState(GAME_SCREEN_STATE) || data.isPaused())
        {
            int tilesLeft = NUM_TILES - data.getStackTiles().size();
            String tileCount = "" + tilesLeft;
            int x = TILE_COUNT_X + TILE_COUNT_OFFSET;
            int y = TILE_COUNT_Y + TILE_COUNT_TEXT_OFFSET;
            g.setFont(TEXT_DISPLAY_FONT);
            g.setColor(Color.BLACK);
            g.drawString(tileCount, x, y);
        }
        
        if(game.getGUIDialogs().get(STATS_DIALOG_TYPE).getState().equals(VISIBLE_STATE))
            {
                PropertiesManager props = PropertiesManager.getPropertiesManager();
                String imgPath = props.getProperty(MahjongSolitaire.MahjongSolitairePropertyType.IMG_PATH);
                String level = data.getCurrentLevel().substring(17, data.getCurrentLevel().length() - 4);
                String statsDialog = props.getProperty(MahjongSolitaire.MahjongSolitairePropertyType.STATS_DIALOG_IMAGE_NAME);
                BufferedImage img = game.loadImageWithColorKey(imgPath + statsDialog, COLOR_KEY);
                int x = (data.getGameWidth()/2) - (img.getWidth(null)/2) + 15;
                int y = (data.getGameHeight()/2) - (img.getHeight(null)/2) + 200;
                g.setFont(STATS_FONT);
                g.drawString(level, x, y);
                
                level = data.getCurrentLevel();
                MahjongSolitaireRecord record = ((MahjongSolitaireMiniGame)game).getPlayerRecord();
                int numGames = record.getGamesPlayed(level);
                String gamesPlayed = "Games: " + numGames;
                g.drawString(gamesPlayed, x, y+50);
             
                int wins = record.getWins(level);
                String gamesWon = "Wins: " + wins;
                g.drawString(gamesWon, x, y+70);
            
                int losses = record.getLosses(level);
                String gamesLost = "Losses: " + losses;
                g.drawString(gamesLost, x, y+90);
       
                double percentage = record.calculateWinPercentage(level);
                String winPercentage = "Win%: " + percentage;
                g.drawString(winPercentage, x, y+110);
                
                long fastest = record.getFastestTime(level);
                String fastestWin = "Fastest Win: ";
                if(fastest != Long.MAX_VALUE)
                    fastestWin += data.timeToText(fastest);
                else
                    fastestWin += "N/A";
                g.drawString(fastestWin, x, y+130);
                
            }
       
        
    }
        
    /**
     * Renders all the game tiles, doing so carefully such
     * that they are rendered in the proper order.
     * 
     * @param g the Graphics context of this panel.
     */
    public void renderTiles(Graphics g)
    {
        // DRAW THE TOP TILES ON THE STACK
        if (!data.won())
        {
            // WE DRAW ONLY THE TOP 4 (OR 2 IF THERE ARE ONLY 2). THE REASON
            // WE DRAW 4 IS THAT WHILE WE MOVE MATCHES TO THE STACK WE WANT
            // TO SEE THE STACK
            ArrayList<MahjongSolitaireTile> stackTiles = data.getStackTiles();
            if (stackTiles.size() > 3)
            {
                renderTile(g, stackTiles.get(stackTiles.size()-3));
                renderTile(g, stackTiles.get(stackTiles.size()-4));
            }
            if (stackTiles.size() > 1)
            {
                renderTile(g, stackTiles.get(stackTiles.size()-1));
                renderTile(g, stackTiles.get(stackTiles.size()-2));
            }
        }
        
        // THEN DRAW THE GRID TILES BOTTOM TO TOP USING
        // THE TILE'S Z TO STAGGER THEM AND GIVE THE ILLUSION
        // OF DEPTH
        ArrayList<MahjongSolitaireTile>[][] tileGrid = data.getTileGrid();
        boolean noneOnLevel = false;
        int zIndex = 0;
        while (!noneOnLevel)
        {
            int levelCounter = 0;
            for (int i = 0; i < data.getGridColumns(); i++)
            {
                for (int j = 0; j < data.getGridRows(); j++)
                {
                    if (tileGrid[i][j].size() > zIndex)
                    {
                        MahjongSolitaireTile tile = tileGrid[i][j].get(zIndex);
                        renderTile(g, tile);
                        levelCounter++;
                    }
                }
            }
            if (levelCounter == 0)
                noneOnLevel = true;
            zIndex++;
        }
        
        // THEN DRAW ALL THE MOVING TILES
        Iterator<MahjongSolitaireTile> movingTiles = data.getMovingTiles();
        while (movingTiles.hasNext())
        {
            MahjongSolitaireTile tile = movingTiles.next();
            renderTile(g, tile);
        }
    }

    /**
     * Helper method for rendering the tiles that are currently moving.
     * 
     * @param g Rendering context for this panel.
     * 
     * @param tileToRender Tile to render to this panel.
     */
    public void renderTile(Graphics g, MahjongSolitaireTile tileToRender)
    {
        // ONLY RENDER VISIBLE TILES
        if (!tileToRender.getState().equals(INVISIBLE_STATE))
        {
            // FIRST DRAW THE BLANK TILE IMAGE
            if (tileToRender.getState().equals(SELECTED_STATE))
                g.drawImage(blankTileSelectedImage, (int)tileToRender.getX(), (int)tileToRender.getY(), null);
            else if (tileToRender.getState().equals(VISIBLE_STATE))
                g.drawImage(blankTileImage, (int)tileToRender.getX(), (int)tileToRender.getY(), null);
            
            // THEN THE TILE IMAGE
            SpriteType bgST = tileToRender.getSpriteType();
            Image img = bgST.getStateImage(tileToRender.getState());
            g.drawImage(img, (int)tileToRender.getX()+TILE_IMAGE_OFFSET, (int)tileToRender.getY()+TILE_IMAGE_OFFSET, bgST.getWidth(), bgST.getHeight(), null); 
            
            // IF THE TILE IS SELECTED, HIGHLIGHT IT
            if (tileToRender.getState().equals(SELECTED_STATE))
            {
                g.setColor(SELECTED_TILE_COLOR);
                g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
            }
            else if (tileToRender.getState().equals(INCORRECTLY_SELECTED_STATE))
            {
                g.setColor(INCORRECTLY_SELECTED_TILE_COLOR);
                g.fillRoundRect((int)tileToRender.getX(), (int)tileToRender.getY(), bgST.getWidth(), bgST.getHeight(),5,5);
            }
        }        
    }
    
    /**
     * Renders the game dialog boxes.
     * 
     * @param g This panel's graphics context.
     */
    public void renderDialogs(Graphics g)
    {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> dialogSprites = game.getGUIDialogs().values();
        for (Sprite s : dialogSprites)
        {
            // RENDER THE DIALOG, NOTE IT WILL ONLY DO IT IF IT'S VISIBLE
            renderSprite(g, s);
        }
       
    }
    
    /**
     * Renders the s Sprite into the Graphics context g. Note
     * that each Sprite knows its own x,y coordinate location.
     * 
     * @param g the Graphics context of this panel
     * 
     * @param s the Sprite to be rendered
     */
    public void renderSprite(Graphics g, Sprite s)
    {
        // ONLY RENDER THE VISIBLE ONES
        if (!s.getState().equals(INVISIBLE_STATE))
        {
            SpriteType bgST = s.getSpriteType();
            Image img = bgST.getStateImage(s.getState());
            g.drawImage(img, (int)s.getX(), (int)s.getY(), bgST.getWidth(), bgST.getHeight(), null); 
        }
    }

    /**
     * This method renders grid lines in the game tile grid to help
     * during debugging.
     * 
     * @param g Graphics context for this panel.
     */
    public void renderGrid(Graphics g)
    {
        // ONLY RENDER THE GRID IF WE'RE DEBUGGING
        if (data.isDebugTextRenderingActive())
        {
            for (int i = 0; i < data.getGridColumns(); i++)
            {
                for (int j = 0; j < data.getGridRows(); j++)
                {
                    int x = data.calculateTileXInGrid(i, 0);
                    int y = data.calculateTileYInGrid(j, 0);
                    g.drawRect(x, y, TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT);
                }
            }
        }
    }
    
    /**
     * Renders the debugging text to the panel. Note
     * that the rendering will only actually be done
     * if data has activated debug text rendering.
     * 
     * @param g the Graphics context for this panel
     */
    public void renderDebuggingText(Graphics g)
    {
        // IF IT'S ACTIVATED
        if (data.isDebugTextRenderingActive())
        {
            // ENABLE PROPER RENDER SETTINGS
            g.setFont(DEBUG_TEXT_FONT);
            g.setColor(DEBUG_TEXT_COLOR);
            
            // GO THROUGH ALL THE DEBUG TEXT
            Iterator<String> it = data.getDebugText().iterator();
            int x = data.getDebugTextX();
            int y = data.getDebugTextY();
            while (it.hasNext())
            {
                // RENDER THE TEXT
                String text = it.next();
                g.drawString(text, x, y);
                y += 20;
            }   
        } 
    }
}