package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The UI Builder class contains numerous static methods used to create
 * prebuilt components that are to be added directly to any UI made.
 */
public class UIBuilder {

    // === *** Static Colors *** === //

    /**
     * The color black.
     */
    public final static Color BLACK = new Color(0, 0, 0);
    

    // === *** Panel Methods *** === //

    /**
     * Adds a number blank panels to the container with the specified color. These panels
     * are not assigned a layoutmanager.
     * @param container the container that is having empty panels added to.
     * @param num the number of panels added to the container.
     * @param c the color of all of these panels.
     */
    public static void addEmptyPanels(Container container, int num, Color c) {
        for (int i = 0; i < num; i++) {
            JPanel panel = new JPanel();
            panel.setBackground(c);
            container.add(panel);
        }
    }

    /**
     * Adds a number blank panels to the container with the specified color. These panels
     * are not assigned a layoutmanager.
     * @param container the container that is having empty panels added to.
     * @param num the number of panels added to the container.
     * @param c the color of all of these panels.
     */
    public static void addEmptyPanels(Container container, int num) {
        for (int i = 0; i < num; i++) {
            JPanel panel = new JPanel();
            container.add(panel);
        }
    }

    /**
     * Creates a default panel with the color being white, and the layout manager
     * being a border layout.
     * @return the panel.
     */
    public static JPanel panel() {
        return panel(new Color(255, 255, 255));
    }

    /**
     * Creates a panel with a border layout manager, and the color character
     * will set the color much easier.
     * @param color the character that represents the color
     * @return the panel colored
     */
    public static JPanel panel(char color) {
        switch (color) {

            case 'b':
            case 'B':
                return panel(new Color(0, 0, 0));

            default:
                return panel();

        }
    }

    /**
     * Creates a Jpanel that has a BorderLayout. This is the default layout, as
     * it is used the most.
     * @param c the color of the panel.
     * @return the panel created here.
     */
    public static JPanel panel(Color c) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(c);
        return panel;
    }

    /**
     * Creates a JPanel that has a grid layout with the input rows and columns.
     * @param c the color of the panel
     * @param rows the number of rows in the JPanel
     * @param columns the number of columns in the JPanel
     * @return the panel
     */
    public static JPanel panel(Color c, int rows, int columns) {
        JPanel panel = new JPanel(new GridLayout(rows, columns));
        panel.setBackground(c);
        return panel;
    }

    /**
     * Creates a grid layout with the default background being white.
     * @param rows the number of rows in the grid layout
     * @param columns the number of columns in the grid layout
     * @return the panel
     */
    public static JPanel panel(int rows, int columns) {
        JPanel panel = new JPanel(new GridLayout(rows, columns));
        panel.setBackground(new Color(255, 255, 255));
        return panel;
    }

    // === *** Buttons *** === //

    /**
     * Creates a button that by default has the buttonPath image, and when hovering
     * over the button, will change to the rollover Path.
     * @param buttonPath the path to the standard button
     * @param rolloverPath the path to the rollover button
     * @return the button
     */
    public static JButton button(String buttonPath, String rolloverPath) {
        
        ImageIcon buttonImage = new ImageIcon(buttonPath);
        ImageIcon rolloverImage = new ImageIcon(rolloverPath);

        JButton button = new JButton(buttonImage);
        button.setText("");
        button.setFocusable(false);

        button.setRolloverEnabled(true);
        button.setRolloverIcon(rolloverImage);

        return button;
    }

    /**
     * Creates a button that by default has the buttonPath image.
     * @param buttonPath the path to the image that will paint on this button
     * @return the button.
     */
    public static JButton button(String buttonPath) {

        ImageIcon buttonImage = new ImageIcon(buttonPath);

        JButton button = new JButton(buttonImage);
        button.setText("");
        button.setFocusable(false);

        return button;
    }

    // === *** Window Methods *** === //

    /**
     * Fetches the dimensions of the entire screen.
     * @return the dimenisons of the screen.
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Fetches the width of the screen in pixels.
     * @return the width of the screen in pixels.
     */
    public static int getScreenWidth() {
        return getScreenSize().width;
    }

    /**
     * Fetches the height of the screen in pixels.
     * @return the height of the screen in pixels.
     */
    public static int getScreenHeight() {
        return getScreenSize().height;
    }

    /**
     * Sets the cursor to the image path. Needs a parent component to manage this. So it
     * knows what cursor to use where.
     * @param filePath the file path to the cursor image.
     */
    public static void setCursor(Component component, String filePath) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(filePath);
        Point hotSpot = new Point(0, 0); // Define the hotspot of the cursor
        Cursor customCursor = toolkit.createCustomCursor(image, hotSpot, "Custom Cursor");
        component.setCursor(customCursor);
    }

}
