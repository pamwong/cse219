package zombiecrushsaga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import zombiecrushsaga.data.ZombieCrushSagaDataModel;
import zombiecrushsaga.file.ZombieCrushSagaFileManager;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;
import static zombiecrushsaga.ZombieCrushSagaConstants.*;

/**
 * This event handler responds to when the user selects
 * a Mahjong level to play on the splash screen.
 * 
 * @author Richard McKenna
 */
public class PlayGameHandler implements ActionListener
{
    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;
    
    /**
     * This constructor just stores the game and the level to
     * load for later.
     *     
     * @param initGame The game to update.
     * 
     * @param initLevelFile The level to load when the user requests it. 
     */
    public PlayGameHandler(ZombieCrushSagaMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on a button for selecting a level
     * which is how the user starts a game. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // WE ONLY LET THIS HAPPEN IF THE SPLASH SCREEN IS VISIBLE
        if (game.isCurrentScreenState(SPLASH_SCREEN_STATE))
        {
            // GET THE GAME'S DATA MODEL, WHICH IS ALREADY LOCKED FOR US
            ZombieCrushSagaDataModel data = (ZombieCrushSagaDataModel)game.getDataModel();

            // GO TO THE GAME
            game.switchToSagaScreen();
        }
    }
}