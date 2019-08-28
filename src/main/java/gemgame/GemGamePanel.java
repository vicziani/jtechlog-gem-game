package gemgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.JPanel;

public class GemGamePanel extends JPanel implements MouseListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 200;
    // Offscreen kép
    private Image imageBuffer;
    // Annak grafikus buffere
    private Graphics graphicsBuffer;
    // Hattér & vesztettél kép
    private Image hatter;
    private Image vesztettel;
    // Színek
    private static final Color FEHER = new Color(255, 255, 255);
    private static final Color SZURKE = new Color(200, 200, 200);
    private static final Color SOTETSZURKE = new Color(150, 150, 150);
    private static final Color PIROS = new Color(200, 0, 0);
    private static final Color SOTETPIROS = new Color(150, 0, 0);
    private static final Color ZOLD = new Color(0, 200, 0);
    private static final Color SOTETZOLD = new Color(0, 150, 0);
    private static final Color KEK = new Color(0, 0, 200);
    private static final Color SOTETKEK = new Color(0, 0, 150);
    // Táblák
    private int[][][] kicsik = new int[4][4][4];
    private int[][] nagy = new int[27][4];
    // Aktív kis tábla
    private int aktiv;
    // A nagy tábla első és utolsó telített oszlopa
    private int elso, utolso;
    // Kis tábla elhelyezkedése
    private int merre;
    // Hibakövetés
    private static final boolean DEBUG = true;
    // Szál, mely vezérli az oszlopok ejtegetését
    private Ejto ejto = null;
    // Véletlen szám generátor
    private Random generator = new Random();
    // Pontszám
    private int pont;
    // Idő, hogy mennyit vár két oszlop leejtése között
    private int ido;
    // Vesztett-e a játékos?
    private boolean vesztett;
    // Az ejtést végző thread fut-e
    private boolean fut;

    public void oszlopAd() {
        int oszlop = 0;

        // Annak kiszámolása, hogy hova ejtse a következő elemet
        if ((elso == 0) && (utolso == 0)) {
            oszlop = 13;
            elso = 13;
            utolso = 13;
        } else {
            if (13 - elso < utolso - 13) {
                oszlop = elso - 1;
                elso--;
            } else {
                oszlop = utolso + 1;
                utolso++;
            }
        }

        // Ha vesztett, leállítja a thread-et
        if (oszlop == 27) {
            if (DEBUG) {
                System.out.println("Vesztettel!");
            }
            vesztett = true;            
            ejto.leallit();
            repaint();
            return;
        }

        int szin = generator.nextInt(3) + 2;
        int szam = generator.nextInt(4) + 1;

        if (DEBUG) {
            System.out.println("Oszlop: " + oszlop + " Szin: " + szin + " Szam: " + szam + " Elso: " + elso + " Utolso: " + utolso);
        }
        int i, j = 0;
        switch (szam) {
            case 1:
                for (i = 0; i < 4; i++) {
                    nagy[oszlop][i] = 1;
                }
                nagy[oszlop][generator.nextInt(4)] = szin;
                break;
            case 2:
                for (i = 0; i < 4; i++) {
                    nagy[oszlop][i] = 1;
                }
                do {
                    i = generator.nextInt(4);
                    j = generator.nextInt(4);
                } while (i == j);
                nagy[oszlop][i] = szin;
                nagy[oszlop][j] = szin;
                break;
            case 3:
                for (i = 0; i < 4; i++) {
                    nagy[oszlop][i] = szin;
                }
                nagy[oszlop][generator.nextInt(4)] = 1;
                break;
            case 4:
                for (i = 0; i < 4; i++) {
                    nagy[oszlop][i] = szin;
                }
                break;
        }
        repaint();

    }

    public void forgat() {
        int[][] tmp = new int[4][4];
        int i, j;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                tmp[i][j] = kicsik[aktiv][i][j];
            }
        }
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                kicsik[aktiv][3 - j][i] = tmp[i][j];
            }
        }
    }

    public boolean eshet() {
        boolean eshete = true;
        int i;
        for (i = 0; i < 4; i++) {
            if ((nagy[13][i] != 1) && (kicsik[aktiv][3 - merre][i] != 1)) {
                eshete = false;
            }
        }
        return eshete;
    }

    public boolean teli() {
        int i, j;
        boolean telie = true;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                if (kicsik[aktiv][i][j] == 1) {
                    telie = false;
                }
            }
        }
        return telie;
    }

    public void ejtes() {
        int i, j;
        if (eshet()) {
            for (i = 0; i < 4; i++) {
                if (nagy[13][i] != 1) {
                    kicsik[aktiv][3 - merre][i] = nagy[13][i];
                    pont++;
                }
            }
            if (elso == utolso) {
                for (i = 0; i < 4; i++) {
                    nagy[13][i] = 0;
                }
                elso = 0;
                utolso = 0;
            } else if ((13 - elso) <= (utolso - 13)) {
                for (i = 14; i <= utolso; i++) {
                    for (j = 0; j < 4; j++) {
                        nagy[i - 1][j] = nagy[i][j];
                    }
                }
                for (i = 0; i < 4; i++) {
                    nagy[utolso][i] = 0;
                }
                utolso--;
            } else {
                for (i = 12; i >= elso; i--) {
                    for (j = 0; j < 4; j++) {
                        nagy[i + 1][j] = nagy[i][j];
                    }
                }
                for (i = 0; i < 4; i++) {
                    nagy[elso][i] = 0;
                }
                elso++;
            }
            if (teli()) {
                for (i = 0; i < 4; i++) {
                    for (j = 0; j < 4; j++) {
                        kicsik[aktiv][i][j] = 1;
                    }
                }
                if (pont % 50 == 0) {
                    ido = ido - 200;
                }
            }
        }
    }

    public GemGamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // Háttér betöltése MediaTracker-rel
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(hatter, 0);
        tracker.addImage(vesztettel, 1);
        try {
            tracker.waitForAll();
        } catch (Exception e) {
            System.err.println("Error by loading pictures: " + e);
        }
        reset();
    }

    private void reset() {
        // Táblák beállítása
        int i, j, k;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                for (k = 0; k < 4; k++) {
                    kicsik[i][j][k] = 1;
                }
            }
            for (j = 0; j < 27; j++) {
                nagy[j][i] = 0;
            }
        }
        // Aktív kis tábla beállítása
        aktiv = 0;
        // Kis tábla elhelyezkedésének beállítása
        merre = 0;
        // A nagy tábla első és utolsó telített sorának beállítása
        elso = 0;
        utolso = 0;
        // Pont nullázása
        pont = 0;
        //Idő beállítása 4 másodpercre
        ido = 4000;
        // Nem vesztett, most kezdi
        vesztett = false;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        imageBuffer = createImage(WIDTH, HEIGHT);
        graphicsBuffer = imageBuffer.getGraphics();
        ejto = new Ejto(this);
        ejto.start();
        // MouseListener hozzáadása
        addMouseListener(this);
    }

    public void nagyRajzol(Graphics g) {
        int i, j;
        for (i = 0; i < 27; i++) {
            for (j = 0; j < 4; j++) {
                if (nagy[i][j] != 0) {
                    switch (nagy[i][j]) {
                        case 1:
                            g.setColor(SZURKE);
                            break;
                        case 2:
                            g.setColor(PIROS);
                            break;
                        case 3:
                            g.setColor(ZOLD);
                            break;
                        case 4:
                            g.setColor(KEK);
                            break;
                    }
                    g.fill3DRect(84 + i * 16, 20 + j * 16, 16, 16, true);
                }
            }
        }
    }

    public void kisRajzol(Graphics g) {
        int i, j;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                switch (kicsik[aktiv][i][j]) {
                    case 1:
                        g.setColor(SZURKE);
                        break;
                    case 2:
                        g.setColor(PIROS);
                        break;
                    case 3:
                        g.setColor(ZOLD);
                        break;
                    case 4:
                        g.setColor(KEK);
                        break;
                }
                g.fill3DRect(244 + i * 16 + merre * 16, 116 + j * 16, 16, 16, true);
            }
        }
    }

    public void jelzoRajzol(Graphics g, int i) {
        int j, k;
        for (j = 0; j < 4; j++) {
            for (k = 0; k < 4; k++) {
                switch (kicsik[i][j][k]) {
                    case 1:
                        if (aktiv == i) {
                            g.setColor(SOTETSZURKE);
                        } else {
                            g.setColor(SZURKE);
                        }
                        break;
                    case 2:
                        if (aktiv == i) {
                            g.setColor(SOTETPIROS);
                        } else {
                            g.setColor(PIROS);
                        }
                        break;
                    case 3:
                        if (aktiv == i) {
                            g.setColor(SOTETZOLD);
                        } else {
                            g.setColor(ZOLD);
                        }
                        break;
                    case 4:
                        if (aktiv == i) {
                            g.setColor(SOTETKEK);
                        } else {
                            g.setColor(KEK);
                        }
                        break;
                }
                switch (i) {
                    case 0:
                        g.fill3DRect(185 + j * 8, 114 + k * 8, 8, 8, true);
                        break;
                    case 1:
                        g.fill3DRect(383 + j * 8, 114 + k * 8, 8, 8, true);
                        break;
                    case 2:
                        g.fill3DRect(185 + j * 8, 150 + k * 8, 8, 8, true);
                        break;
                    case 3:
                        g.fill3DRect(383 + j * 8, 150 + k * 8, 8, 8, true);
                        break;
                }
            }
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        // Ide kell rajzolni
        Graphics actg;
        actg = graphicsBuffer;
        // Ciklusváltozó definiálása
        int i;
        // Háttér rajzolása
        actg.drawImage(hatter, 0, 0, this);
        // Nagy tábla vagy a vesztett felirat kirajzolása 
        if (vesztett) {
            actg.drawImage(vesztettel, 84, 20, this);
        } else {
            nagyRajzol(actg);
        }
        // Kis tábla kirajzolása
        kisRajzol(actg);
        // Kis táblák jelzőinek kirajzolása
        for (i = 0; i < 4; i++) {
            jelzoRajzol(actg, i);
        }
        // Frissítés
        g.drawImage(imageBuffer, 0, 0, this);
        // Pontszám
        g.setColor(Color.WHITE);
        g.drawString("Pontszám: " + pont, 10, 190);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        if (vesztett == false) {
            int i, j;
            if ((185 < x) && (x < 217) && (114 < y) && (y < 145)) {
                if (DEBUG) {
                    System.out.println("Elso kisteglalap");
                }
                aktiv = 0;
            }
            if ((383 < x) && (x < 415) && (114 < y) && (y < 145)) {
                if (DEBUG) {
                    System.out.println("Masodik kisteglalap");
                }
                aktiv = 1;
            }
            if ((185 < x) && (x < 217) && (150 < y) && (y < 182)) {
                if (DEBUG) {
                    System.out.println("Harmadik kisteglalap");
                }
                aktiv = 2;
            }
            if ((383 < x) && (x < 415) && (150 < y) && (y < 182)) {
                if (DEBUG) {
                    System.out.println("Negyedik kisteglalap");
                }
                aktiv = 3;
            }
            if ((64 < x) && (x < 84) && (20 < y) && (y < 84)) {
                if (DEBUG) {
                    System.out.println("Nagy balra.");
                }
                if ((elso > 0) && (utolso != 13)) {
                    for (i = elso; i <= utolso; i++) {
                        for (j = 0; j < 4; j++) {
                            nagy[i - 1][j] = nagy[i][j];
                        }
                    }
                    for (j = 0; j < 4; j++) {
                        nagy[utolso][j] = 0;
                    }
                    elso--;
                    utolso--;
                }
            }
            if ((516 < x) && (x < 546) && (20 < y) && (y < 84)) {
                if (DEBUG) {
                    System.out.println("Nagy jobbra");
                }
                if ((utolso < 26) && (elso != 13)) {
                    for (i = utolso; i >= elso; i--) {
                        for (j = 0; j < 4; j++) {
                            nagy[i + 1][j] = nagy[i][j];
                        }
                    }
                    for (j = 0; j < 4; j++) {
                        nagy[elso][j] = 0;
                    }
                    elso++;
                    utolso++;
                }
            }
            if ((244 < x) && (x < 356) && (115 < y) && (y < 180)) {
                if (DEBUG) {
                    System.out.println("Forgatas");
                }
                forgat();
            }
            if ((217 < x) && (x < 244) && (116 < y) && (y < 179)) {
                if (DEBUG) {
                    System.out.println("Kicsi balra");
                }
                if (merre != 0) {
                    merre--;
                }
            }
            if ((357 < x) && (x < 383) && (116 < y) && (y < 180)) {
                if (DEBUG) {
                    System.out.println("Kicsi jobbra");
                }
                if (merre != 3) {
                    merre++;
                }
            }
            if ((292 < x) && (x < 308) && (20 < y) && (y < 84)) {
                if (DEBUG) {
                    System.out.println("Ejtes");
                }
                if ((elso != 0) && (utolso != 0)) {
                    ejtes();
                }
            }
            repaint();
        }
        else {
            // Újraindítás
            reset();
            ejto = new Ejto(this);
            ejto.start();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void setHatter(Image hatter) {
        this.hatter = hatter;
    }

    public void setVesztettel(Image vesztettel) {
        this.vesztettel = vesztettel;
    }

    public int getIdo() {
        return ido;
    }

    public boolean isFut() {
        return fut;
    }
}