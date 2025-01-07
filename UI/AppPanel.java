package UI;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * The AppPanel, stores the four panels that show all of the contents
 * of the application.
 * 
 * Top Left -> Sprite
 * Top Right -> Chatlog
 * Bottom Left -> ???
 * Bottom Right -> ???
 */
public class AppPanel extends JPanel {

    // === *** Subpanel Attributes *** === //

    /**
     * The JPanel that contains the sprite.
     */
    private JPanel spritePanel;

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

        spritePanel = spritePanel();
        chatPanel = new ChatPanel();

        this.add(spritePanel);
        this.add(chatPanel);
    }

    /**
     * Sets up the sprite panel. This includes the art for the system
     * so that it is more user friendly.
     * @return the sprite panel
     */
    private JPanel spritePanel() {
        return new JPanel();
    }

}
