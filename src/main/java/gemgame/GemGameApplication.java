package gemgame;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class GemGameApplication extends JFrame {

    private GemGamePanel panel = new GemGamePanel();

    public GemGameApplication() {
        super("Gem Game");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        Image hatter = Toolkit.getDefaultToolkit().getImage(GemGamePanel.class.getResource("/GemBack.gif"));
        Image vesztettel = Toolkit.getDefaultToolkit().getImage(GemGamePanel.class.getResource("/YouLoose.gif"));
        panel.setHatter(hatter);
        panel.setVesztettel(vesztettel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        pack();

    }

    public static void main(String[] args) {
        GemGameApplication app = new GemGameApplication();
        app.setVisible(true);
    }
}
