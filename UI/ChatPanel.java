package UI;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * The ChatPanel Class. This chat panel class stores only text, and contains two methods revolving
 * around adding text that indicates that the user said it, and adding text that indicates that the
 * chat bot said it.
 */
public class ChatPanel extends JPanel {
    
    // === *** Attributes *** === //

    /**
     * JPanel that contains the chat information. Allows manipulation
     * by adding text to the panel.
     */
    private JPanel chatPanel;

    // === *** Constructors *** //

    /**
     * Creates the Chat Panel. This panel will contain a schat box that can be added
     * to.
     */
    public ChatPanel() {
        super(new BorderLayout());

        
    }

    /**
     * Adds text to the chat panel. Makes it identifiable that the user said it.
     * @param input the text to be added to the user side
     */
    public void addUserText(String input) {
        // TODO Add text in a way that shows it was the user who said something.
    }

    /**
     * Adds text to the chat panel. Makes it identifiable that the chatbot said it.
     * @param input the text to be added to the bot side.
     */
    public void addChatText(String input) {
        // TODO Add text in a way that shows it was the chat who said something.
    }

}
