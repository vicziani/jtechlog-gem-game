package gemgame;

public class Ejto extends Thread {

    private GemGamePanel panel;
    
    private boolean leallitott;

    public Ejto(GemGamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (!leallitott) {
            panel.oszlopAd();
        
        try {
            Thread.sleep(panel.getIdo());
        } catch (InterruptedException E) {
        }
        }
    }
    
    public void leallit() {
        leallitott = true;
    }
}
