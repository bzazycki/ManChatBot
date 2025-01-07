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
    
    // === *** Constructors *** === //
    
    /**
     * The default constructor, creates the entire application ready to go from here.
     */
    public AppPanel() {
        super(new GridLayout(2, 2));

        
    }


}
