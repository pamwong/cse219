package mahjong_solitaire.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mahjong_solitaire.data.MahjongSolitaireDataModel;
import mahjong_solitaire.ui.MahjongSolitaireMiniGame;

/**
 * This event handler responds to when the user requests to
 * start a new game. 
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class UndoHandler implements ActionListener
{
    // HERE'S THE D
    private MahjongSolitaireMiniGame game;
    
    /**
     * This constructor just stores the game for later.
     * 
     * @param initGame the game to update
     */
    public UndoHandler(MahjongSolitaireMiniGame initGame)
    {
        game = initGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on the button for starting a new game,
     * which can be done when the application starts up, during
     * a game, or after a game has been played. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        MahjongSolitaireDataModel data = (MahjongSolitaireDataModel)game.getDataModel();
        data.undoLastMove();
    }
}