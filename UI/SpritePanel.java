package UI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class SpritePanel extends JPanel {

  // === *** Attributes *** === //

    /**
     * JPanel that contains the sprite animations.
     */
    private JPanel spritePanel;

    // === *** Constructors *** //

    /**
     * Creates the Chat Panel. This panel will contain a schat box that can be added
     * to.
     */
    public SpritePanel() {
        super(new BorderLayout());

        this.add(spritePanel());
    }

        /**
     * Creates the chat panel. This panel will be manipulated to
     * show everything that was said and responded.
     * @return the chat panel
     */
    private JPanel spritePanel() {
      JPanel spritePanel = new JPanel();
      spritePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.spritePanel = spritePanel;
      return spritePanel;
  }
  
}
