
import java.applet.Applet;
import java.awt.Image;

public class GemGameApplet extends Applet {
    private GemGamePanel panel = new GemGamePanel();

    @Override
    public void init() {
        Image hatter = getImage(getClass().getResource("GemBack.gif"));
        Image vesztettel = getImage(getClass().getResource("YouLoose.gif"));
        panel.setHatter(hatter);
        panel.setVesztettel(vesztettel);
        
        add(panel);
    }

    @Override
    public String getAppletInfo() {
        return "Gem Game version 1.0-SNAPSHOT - logikai játék\nViczián István\nhttp://jtechlog.blogspot.com";
    }
    
}
