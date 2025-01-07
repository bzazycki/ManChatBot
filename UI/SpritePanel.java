package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;



public class SpritePanel extends JPanel {

  private Timer timer;
  private int elapsedTime = 0;
  private boolean isPlaying = true;

  // === *** Attributes *** === //

    /**
     * JPanel that contains the sprite animations.
     */
    
    private JPanel spritePanel;
    // === *** Constructors *** //

    /**
     * Creates the Sprite Panel. This panel will contain a schat box that can be added
     * to.
     */
    public SpritePanel() {
      super(new BorderLayout());

      ImageIcon gifIcon = new ImageIcon("UI\\Sprite.gif");

      JLabel gifLabel = new JLabel(gifIcon);
      gifLabel.setVerticalAlignment(SwingConstants.CENTER);
      add(gifLabel);

      timer = new Timer(1000, new ActionListener() {
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

      this.add(spritePanel());
    }

    // Simulate starting the GIF (starts the timer)
    private void startGif() {
        isPlaying = true;
        elapsedTime = 0;
        timer.start();
    }

    // Stop the GIF after 30 seconds
    private void stopGif() {
        isPlaying = false;
        timer.stop();
        JOptionPane.showMessageDialog(this, "GIF Stopped After 30 Seconds");
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
      
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          SpritePanel app = new SpritePanel();
          app.setVisible(true);
          app.startGif();  // Automatically start the GIF when the app launches
        }
      });
      return spritePanel;
    }
  
}
