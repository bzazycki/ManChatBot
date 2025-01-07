package UI;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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

    /**
     * The input text area. This is where text before it is sent to the chatbot will be
     * staged.
     */
    private JTextArea inputArea;

    // === *** Constructors *** //

    /**
     * Creates the Chat Panel. This panel will contain a schat box that can be added
     * to.
     */
    public ChatPanel() {
        super(new BorderLayout());

        this.add(inputPanel(), BorderLayout.SOUTH);
        this.add(chatPanel());
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

    /**
     * Creates the input panel. This is so that the user can type into the requried field
     * before submitting, or the text can be added to the textfield via voice recog.
     * @return the input panel
     */
    private JPanel inputPanel() {

        JPanel inputPanel = UIBuilder.panel();

        // Input Text area
        JTextArea inputTextArea = new JTextArea();
        this.inputArea = inputTextArea;
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);

        // Setting it in a scroll pane
        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(scrollPane);

        // The submit button to send the text to the api
        JButton submitButton = new JButton(">");
        submitButton.addActionListener(e -> makeAPICallUpdate());
        inputPanel.add(submitButton, BorderLayout.EAST);

        return inputPanel;
    }

    /**
     * Creates the chat panel. This panel will be manipulated to
     * show everything that was said and responded.
     * @return the chat panel
     */
    private JPanel chatPanel() {
        JPanel chatPanel = new JPanel();
        this.chatPanel = chatPanel;
        return chatPanel;
    }


    /**
     * Makes the API call to the backend server, then clears the input text box and adds the said
     * text (both input and output) to the output text box
     */
    private void makeAPICallUpdate() {

        String output = "";

        String input = this.inputArea.getText().toString().trim();

        this.inputArea.setText("");
        
        // TODO String output = API.sendToAPI(input);

        addUserText(input);

        addChatText(output);
    }

}
