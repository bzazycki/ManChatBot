package UI;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Frame Class. This starts the rendering and creation of the intial frame
 * that populates the entire screen. Everything visual in the program is found
 * within this frame.
 * @author John Belak
 */
public class Frame extends JFrame {

    // === *** Attributes *** === //

    // === *** Constructors *** === //

    /**
     * Generates the frame, and sets up the default behaviors of the frame.
     */
    public Frame() {
        super();
        this.setLayout(new BorderLayout());

        this.add(companyBannerPanel(), BorderLayout.NORTH);

        this.add(new MPanel());

        // 1280 x 800

        // Screen Size
        // Toolkit tk = Toolkit.getDefaultToolkit();
        // this.setSize(tk.getScreenSize());

        this.setSize(1280, 800);

        // Default Behaviors
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * The Company Banner Panel. Creates a decorative banner that is always at the top of the
     * application, so that users have an idea to what is happening.
     * @return the JPanel.
     */
    private JPanel companyBannerPanel() {
        JPanel banner = UIBuilder.panel();

        JLabel label = new JLabel("JustLife");
        banner.add(label);

        return banner;
    }

    /**
     * Entry point for the program. Generates the frame.
     * @param args the default java arguments.
     */
    public static void main(String[] args) {
        new Frame();
    }

}