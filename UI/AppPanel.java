package UI;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * The AppPanel, stores the four panels that show all of the contents
 * of the application.
 * 
 * Top Left -> Sprite
 * Top Right -> Chatlog
 */
public class AppPanel extends JPanel {

    // === *** Subpanel Attributes *** === //

    /**
     * The JPanel that contains the sprite.
     */
    private SpritePanel spritePanel;

    /**
     * The chat panel. This records what has been said by the user and what
     * has been said by the bot.
     */
    private ChatPanel chatPanel;
    
    // === *** Constructors *** === //
    
    /**
     * The default constructor, creates the entire application ready to go from here.
     * This requires the sprite files for imaging.
     */
    public AppPanel() {
        super(new GridLayout(1, 2));

        spritePanel = new SpritePanel();
        chatPanel = new ChatPanel();

        this.add(spritePanel);
        this.add(chatPanel);
    }

}
