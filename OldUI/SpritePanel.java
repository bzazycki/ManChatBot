package UI;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SpritePanel extends JPanel {

  // === *** Attributes *** === //

  /**
   * Stores the active gif label. This is so the gif can be removed and readded to
   * the panel when necessary.
   */
  private JLabel activeGifLabel;

  /**
   * The main panel, this is passed to the Sprite panel so that the end conversation
   * button has access to the other panel.
   */
  private MPanel mainPanel;

  // === *** Constructors *** //

  /**
   * Creates the Sprite Panel. This panel will contain a schat box that can be
   * added
   * to.
   */
  public SpritePanel(MPanel panel) {
    super(new BorderLayout());
    this.mainPanel = panel;

    // Sets the gif label
    ImageIcon gifIcon = new ImageIcon("UI/Images/Sprite.gif");
    JLabel gifLabel = new JLabel(gifIcon);
    this.activeGifLabel = gifLabel;
    this.add(gifLabel);

    JButton endConversationButton = new JButton("End Conversation");
    endConversationButton.addActionListener(e -> mainPanel.switchToDefaultPanel());
    this.add(endConversationButton, BorderLayout.SOUTH);
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

    this.revalidate();
    this.repaint();
  }

}
