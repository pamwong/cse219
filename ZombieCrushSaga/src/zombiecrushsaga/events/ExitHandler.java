package zombiecrushsaga.events;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import zombiecrushsaga.ui.ZombieCrushSagaMiniGame;

/**
 * This class manages when the user clicks the window X to
 * kill the application.
 * 
 * @author Richard McKenna
 */
public class ExitHandler extends WindowAdapter
{
    private ZombieCrushSagaMiniGame miniGame;
    
    public ExitHandler(ZombieCrushSagaMiniGame initMiniGame)
    {
        miniGame = initMiniGame;
    }
    
    /**
     * This method is called when the user clicks the window's X. We 
     * respond by giving the player a loss if the game is still going on.
     * 
     * @param we Window event object.
     */
    @Override
    public void windowClosing(WindowEvent we)
    {
        // IF THE GAME IS STILL GOING ON, SAVE DATA
        if (miniGame.getDataModel().inProgress())
        {
           // SAVE DATA
        }
        // AND CLOSE THE ALL
        System.exit(0);
    }
}