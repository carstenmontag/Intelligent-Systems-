package ludo;

import sim.display.*;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;

public class GUI extends GUIState {
    //Benutzerkonsole
    public Display2D display;
    public JFrame displayFrame;
    // Die Simulationsumgebung
    public PlayingGround sim;
    // Die Visualisierung des Brettes in 2D
    SparseGridPortrayal2D boardPortrayal = new SparseGridPortrayal2D();
    public int current_player = 0;
    public Console c;

    // Strategien
    //First = Bewege priorisiert die Figur, die am nächsten am Ziel ist.
    //Last = Bewege priorisiert die Figur, die am weitesten weg vom Ziel ist.
    //Prefer_Block = Bewege priorisiert die Figur, die eine Blockade verursachen kann.
    //Prefer_Beat = Bewege priorisiert die Figur, die die Figur eines anderen Spielers schlagen kann.
    public static String[] strategies = {"First","Last", "Prefer_Block","Prefer_Beat"};
    public static String[][] strat_combinations;
    public boolean simulation_over = false; // flag Signalisiert ob alle Simulationen durchlaufen wurden.
    // Wie viele Spiele wurden insgesamt gepsielt
    public int num_of_games = 1;

    public static int games_per_comb = 2000; // Gibt an wie viele Spiele per Kombination gespielt werden
    public int game_in_comb = 0; // Gibt an wie viele Spiele in der aktuellen Kombination gespielt wurden
    public int current_comb = 1; // Gibt aktuelle Kombination an
    public int counter_game_moves = 0; // Gibt an wie viele Moves das aktuelle Game hat.

    // Bilder für reguläre Spielsteine werden geladen und in einem Array zusammengefasst.
    Image redImage = new ImageIcon("src/main/resources/RedPiece.png").getImage();
    Image blueImage = new ImageIcon("src/main/resources/BluePiece.png").getImage();
    Image yellowImage = new ImageIcon("src/main/resources/YellowPiece.png").getImage();
    Image greenImage = new ImageIcon("src/main/resources/GreenPiece.png").getImage();
    public Image[] icon_images = {greenImage, redImage, blueImage, yellowImage};
    // Bilder für blockende Spielsteine werden geladen und in einem Array zusammengefasst.
    Image redImageBlock = new ImageIcon("src/main/resources/RedPieceBlock.png").getImage();
    Image blueImageBlock = new ImageIcon("src/main/resources/BluePieceBlock.png").getImage();
    Image yellowImageBlock = new ImageIcon("src/main/resources/YellowPieceBlock.png").getImage();
    Image greenImageBlock = new ImageIcon("src/main/resources/GreenPieceBlock.png").getImage();
    public Image[] roadblock_images = {greenImageBlock, redImageBlock, blueImageBlock, yellowImageBlock};
    // Alle ausgefüphrten Züge eines Spiels werden in einer enstprechenden Array Liste gespeichert.
    // Es können so alle Zügen nachvollzogen werden.
    public ArrayList<Move> moves_this_game = new ArrayList<>();
    // CSV Handler Objekt. so steht für save object. 
    public CSVHandler so;

    public static void main (String[] args){
        /*
          Instanz der Klasse CreateIndex wird erstellt, um der Simulation eine Beschreibung hinzuzufügen.
         */
        CreateIndex in = new CreateIndex();
        in.copy_files();
        /*
         Hier wird eine Instanz der Klasse CSVHandler gebildet.
         In dieser werden alle Simulationsergebnisse für die spätere Auswertung gesichert.
        */
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        CSVHandler so = new CSVHandler(timeStamp);
        // Die Spaltennamen der CSV werden definiert und in die CSV Datei geschrieben        
        String[] headine = {"Strategies","Winrate", "Average Placement", "Average Turns per Game", "Turns to Finish",
                "Average Blocks Created", "Average Kicks", "Average got Kicked", "Game most Kicks",
                "Game most Blocks", "Game most got Kicked"};      
        so.writeRowToCSV(headine);
        // Die Strategiekombinationen die simuliert werden sollen werden aus einer CSV Datei gelesen 
        int[][] int_combinations = so.readRowsFromCSV("src/main/resources/strategy_combinations.csv");
        /*
          Die in der CSV als Nummern angegebenen Strategien werden in die entsprechenden Strings übersetzt
         */
        strat_combinations = new String[int_combinations.length][int_combinations[0].length];
        for(int i=0; i<=int_combinations.length-1; i++) {
            strat_combinations[i] = indices_to_strats(int_combinations[i]);
        }
        /*
          Die erste Simulationsumgebung wird erstellt und zusammen mit der CSVHandler Instanz an den GUI Konstruktor übergeben.
         */
        PlayingGround baseGround =  new PlayingGround(System.currentTimeMillis(), strat_combinations[0]);
        GUI vid = new GUI(baseGround,so);
    }

    public GUI(PlayingGround baseGround, CSVHandler so){
       /**
        * @param CSVHandler
        * @param PlayingGround Die erste Simulation.
        */
        super(baseGround);
        // Konsole wird geöffnet. 
        c = new Console(this);
        c.setVisible(true);
        this.so = so;
    }

    /** 
     * @return String Der Name der Anwendung. Fenstername wird festgelegt.
     */
    public static String getName(){
        return "Ludo";
    }
    
    /**
     * 
     * @return Das genutzte Simulationsobjekt wird an die GUI gegeben. 
     */
    public Object getSimulationInspectedObject() {return state; }

    /**
     *
     * @description Diese Methode wird bei jedem Start einer neuen Simulation ausgeführt.
     */
    @Override
    public void start() {
        super.start();
        // Die Grafiken werden an die Simulation im Hintegrund gekoppelt um sie darstellen zu können.
        setupPortrayals();
        sim = (PlayingGround) state;
        //Der erste Turn des Spiels wird gequeued
        state.schedule.scheduleOnce(0,0,sim.players[0]);
        // Aktuelles Game in der Taktikkombination
        game_in_comb++;
        // Bei Abschluss einer Taktikkombination wird das Resultat in die Ergebnis CSV geschrieben.
        // Außerdem wird Iterator für die Strategiekombinationen erhöbt geladen.
        if (game_in_comb >= games_per_comb) {
            current_comb++;
            so.add_comb(sim.strategies);
        }
    }

    /** 
     *@description      Diese Funktion wird für jeden Zeitsschritt in der Simulationsumgebung ausgeführt. 
     *                  Sie ist Hauptbestandteil der Simulationslogik. Dabei führt sie unter anderem die step 
     *                  Funktion des steppable Objektes Player aus
     *
     * @return boolean 
     */
    @Override
    public boolean step(){
        // der Spieler der als nächstes dran ist wird bestimmt.
        int next_player;
        if (current_player == 3){next_player = 0;}
        else{next_player = current_player +1;}
        boolean success = true;

        // Prüfe ob am Ende der letzen Kombination angekommen
        if (current_comb > strat_combinations.length) {
            // Alle Kombinationen wurden simuliert. Das Programm wird beendet.
            simulation_over = true;
        }
          // Game loop solange die Simulation noch nicht durch den oben stehenden Codeblock beendet wurde.
        if (!simulation_over){
            // Die im Steppable Object Player implementierte step() Funktion wird ausgeführt. In ihr findet die Zuglogik des Spielers statt.
            success = state.schedule.step(state);
            // Wenn ein Zug ausgeführt werden konnte wird dieser abgespeichert. 
            if (sim.move_this_turn != null){
                moves_this_game.add(sim.move_this_turn);
            }
            // Spielzüge werden gezählt, dies umfasst auch "null" Züge
            counter_game_moves++;

            // Überprüfe ob Game Beendet ist.
            if ((sim.players[0].placement != 0) && (sim.players[1].placement != 0) && (sim.players[2].placement != 0) && (sim.players[3].placement != 0)) {
                // Wenn ja beende die Simulation.
                sim.finish();
                // Füge das Spiel dem CSV Hanndler hinzu um es später auszuwerten
                so.add_run(moves_this_game, sim.placements.indexOf("Observed"), counter_game_moves);
                // resette alle simulationsabhängigen Variablen.
                counter_game_moves = 0;
                if(game_in_comb == games_per_comb){game_in_comb = 0;}
                moves_this_game.clear();
                // Eine neue Simulation wird gestartet.
                num_of_games++;
                state = new PlayingGround(System.currentTimeMillis(), strat_combinations[current_comb-1]);
                sim = (PlayingGround)state;
                start();
            }

            // der flag redraw images wird gesetzt wenn ein blockender Spielstein gezeichnet werden soll. 
            if (sim.redraw_images){
                // Die Spielsteinbilder werden neu gebindet
                setupPortrayals();
                // reset flag
                sim.redraw_images = false;
            }
            // repaint board
            c.refresh();
            // 6er Regel 
            int roll = sim.current_roll;
            if (roll == 6 && sim.six_counter<3){
                next_player = current_player; 
            }
            else {sim.six_counter = 0;}
            // Der nächste Zug wird der schedule hinzugefügt
            state.schedule.scheduleOnce(state.schedule.getTime()+1,0,sim.players[next_player]);
            current_player = next_player;
        }
        return success;
    }

    /**
     * 
     * @description Diese Funktion setzt die Grafiken für alle Spielsteine fest. Dabei wird sowohl zwischen den Farben als auch zwischen blockenden und nicht blockenden Steinen unterschieden.
     */
    public void setupPortrayals() {
        // Das Simulationsobjekt 
        PlayingGround board = (PlayingGround) state;
        boardPortrayal.setField(board.field);
        //Iteration durch alle Spieler i und Spielsteine j
        for (int i=0; i <= board.players.length-1; i++) {
            for (int j=0; j<= board.players[i].AtStartPieces.length-1; j++) {
                // darzustellender Spielstein wird ermittelt.
                GamePiece rendering_object = board.players[i].AtStartPieces[j];  
                FacetedPortrayal2D portrayal;
                // blockender oder nicht blockender Spielstein
                if (!rendering_object.blocks){
                    portrayal = new FacetedPortrayal2D(new SimplePortrayal2D[]{new ImagePortrayal2D(icon_images[i])});}
                else {
                    portrayal = new FacetedPortrayal2D(new SimplePortrayal2D[]{new ImagePortrayal2D(roadblock_images[i])});}
                    // Grafik wird zugewiesen
                    boardPortrayal.setPortrayalForObject(board.players[i].AtStartPieces[j], portrayal);
            }
        }
        // Brett wird neu gezeichnet.
        display.reset();

        display.repaint();
    }

    /** 
     * @description Zeichnet das Board, setzt das board portrayal objekt und bindet das GameOverlay Objekt an die GUI
     *  
     * @param c
     */
    @Override
    public void init(Controller c) {
        super.init(c);
        display = new Display2D(720,768,this);
        display.setClipping(false);
        // GameFrame wird erstellt
        displayFrame = display.createFrame();
        displayFrame.setTitle("Ludo Game");
        displayFrame.setResizable(false);
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        setBackground();
        // SparseGridPortrayal2D Objekt wird an die GUI gebindet.
        display.attach(boardPortrayal, "FieldBoard" );
        display.attach(new GameOverlay(this), "GameOverlay");
    }

    /**
     * @description Anwendung wird geschlossen
     */
    public void quit() {
        // quit() method is called to tell the GUIState to shut down
        super.quit();
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }

    /**
     * @description Spielbrett wird gezeichnet. 
     */
    public void setBackground() {
        //https://commons.wikimedia.org/wiki/File:Ludo_board.svg
        Image i = new ImageIcon("src/main/resources/board.png").getImage();
        BufferedImage b = display.getGraphicsConfiguration().createCompatibleImage(i.getWidth(null), i.getHeight(null));
        Graphics g = b.getGraphics();
        g.drawImage(i,0,0,i.getWidth(null),i.getHeight(null),null);
        g.dispose();
        display.setBackdrop(new TexturePaint(b, new Rectangle(0,0,i.getWidth(null),i.getHeight(null))));
    }

    /** 
     * @param indices 0,1,2,3 --> index in dem Array strategies
     * @return String[] Übersetzte Strategien
     */
    public static String[] indices_to_strats(int[] indices) {
        return new String[]{strategies[indices[0]], strategies[indices[1]], strategies[indices[2]], strategies[indices[3]]};
    }
}