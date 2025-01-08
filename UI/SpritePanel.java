package UI;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SpritePanel extends JPanel {

  // === *** Attributes *** === //

  /**
   * Stores the active gif label. This is so the gif can be removed and readded to
   * the panel when necessary.
   */
  private JLabel activeGifLabel;

  // === *** Constructors *** //

  /**
   * Creates the Sprite Panel. This panel will contain a schat box that can be
   * added
   * to.
   */
  public SpritePanel() {
    super(new BorderLayout());

    // Sets the gif label
    ImageIcon gifIcon = new ImageIcon("UI/Sprite.gif");
    JLabel gifLabel = new JLabel(gifIcon);
    this.activeGifLabel = gifLabel;
    this.add(gifLabel);
  }

  /**
   * Sets the gif to the provided file path.
   * @param filePath the filepath to the appropriate gif
   */
  public void setGif(String filePath) {
    this.remove(this.activeGifLabel);

    ImageIcon gifIcon = new ImageIcon(filePath);
    JLabel gifLabel = new JLabel(gifIcon);
    this.activeGifLabel = gifLabel;
    this.add(gifLabel);
  }

}
