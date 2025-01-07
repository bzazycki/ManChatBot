package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class SpritePanel extends JPanel {

  private Timer timer;
  private int elapsedTime = 0;
  private boolean isPlaying = true;

  // === *** Attributes *** === //

    /**
     * JPanel that contains the sprite animations.
     */
    private JPanel spritePanel;

    ImageIcon gifIcon = new ImageIcon("UI\\Sprite.gif");

    JLabel gifLabel = new JLabel(gifIcon);
    gifLabel.setVerticalAlignment(SwingConstants.CENTER);
    add(gifLabel);

    timer = new Timer(1000, new ActionListener()) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (isPlaying) {
          elapsedTime++;
          if (elapsedTime >= 30) {
              stopGif();
          }
          // Simulate the progress of the GIF (you could add more actions here)
        }
      }
    });
    
    timer.start();

    // === *** Constructors *** //

    /**
     * Creates the Sprite Panel. This panel will contain a schat box that can be added
     * to.
     */
    public SpritePanel() {
        super(new BorderLayout());

        this.add(spritePanel());
    }

        /**
     * Creates the sprite panel. This panel will be manipulated to
     * show the animations.
     * @return the sprite panel
     */
    private JPanel spritePanel() {
      JPanel spritePanel = new JPanel();
      spritePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.spritePanel = spritePanel;
      return spritePanel;
  }
  
}
