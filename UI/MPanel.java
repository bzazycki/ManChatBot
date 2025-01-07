package UI;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * MPanel standing for Main Panel holds everything in the application so that
 * it is abstracted away from the frame. Having a borderlayout manager will
 * contain a top panel that represents a settings and information panel, and
 * a lower center panel.
 * @author John Belak
 */
public class MPanel extends JPanel {
    
    // === *** Attributes *** === //

    /**
     * The default start panel. This will be added and removed from the
     * panel when needed.
     */
    private JPanel defaultStartPanel;

    // === *** Constructors *** === //

    /**
     * Default constructor, sets up the border layout for the application, and holds
     * all of the smaller panels that make up the application.
     */
    public MPanel() {
        super();
        this.setLayout(new BorderLayout());

        this.defaultStartPanel = defaultStartPanel();
        this.add(defaultStartPanel);

    }

    // === *** App Panels *** === //

    /**
     * Generates the default start panel. This only includes a single button
     * at the center of the screen.
     * @return the panel with the button at the center.
     */
    private JPanel defaultStartPanel() {

        /*
         *    - - -   3
         *    - - -   6
         *    - - -   9
         *    - x -   12
         *    - - -   15
         *    - - -   18
         *    - - -   21        
         */

        JPanel gridPanel = UIBuilder.panel(7, 3);

        UIBuilder.addEmptyPanels(gridPanel, 10);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> switchToAppPanel());

        gridPanel.add(startButton);

        UIBuilder.addEmptyPanels(gridPanel, 10);

        return gridPanel;
    }

    /**
     * Switches to the app panel, and removes the default start panel and then adds 
     * the app panel.
     */
    private void switchToAppPanel() {
        this.remove(this.defaultStartPanel);
        //this.add(this.appPanel);
        this.revalidate();
        this.repaint();
    }

}
