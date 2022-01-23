package ludo;

import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import java.util.ArrayList;

public class PlayingGround extends SimState {
    //Spielerzahl, sollte über die Konsole verändert werden können
    public long seed;
    public int numPlayers = 4;
    public int test_steps = 2;
    public boolean redraw_images = false;
    public SparseGrid2D field;
    public int current_roll ;
    public int six_counter = 0;
    public int fieldWidth = 15;
    public int fieldHeight = 16;
    public ArrayList<String> placements = new ArrayList<>();
    public Move move_this_turn;
    // Spielerparameter werden initialisert, name optional später über UI konfigurierbar
    public String[] strategies;

    // Start wenn eine 6 gewürfelt wurde, sowohl als 2d, als auch 1d Punkt
    public int[] FigureStarts = {2,15,28,41};
    public int[] FigureFinishes = {0,13,26,39};
    // home Feld, hier starten alle Figuren beim initialisieren
    public Int2D[][] two_d_spawns = {
        //Startpositionen der einzelnen Figuren auf dem 2D Feld 
        //green
        {new Int2D(2,2),new Int2D(3,2),new Int2D(3,3),new Int2D(2,3),},
        //red
        {new Int2D(11,2),new Int2D(12,2),new Int2D(12,3),new Int2D(11,3)},
        //blue
        {new Int2D(11,11),new Int2D(12,11),new Int2D(12,12),new Int2D(11,12)},
        //yellow
        {new Int2D(2,11),new Int2D(3,11),new Int2D(3,12),new Int2D(2,12)}
    };

    // Felder die die finish line darstellen werden definiert. 
    public Int2D[][] two_d_finish_line = {
        //green
        {new Int2D(1,7),new Int2D(2,7),new Int2D(3,7),new Int2D(4,7), new Int2D(5,7), new Int2D(6,7)},
        //red
        {new Int2D(7,1),new Int2D(7,2),new Int2D(7,3),new Int2D(7,4), new Int2D(7,5), new Int2D(7,6)},
        //blue
        {new Int2D(13,7),new Int2D(12,7),new Int2D(11,7),new Int2D(10,7), new Int2D(9,7), new Int2D(8,7)},
         //yellow 
        {new Int2D(7,13),new Int2D(7,12),new Int2D(7,11),new Int2D(7,10), new Int2D(7,9), new Int2D(7,8)}
    };
    // Alle felder auf denen sich die Figuren bewegen werden definiert.
    public static Int2D[] locations = {
        new Int2D(0,7), new Int2D(0,6), new Int2D(1,6), new Int2D(2,6), new Int2D(3,6), new Int2D(4,6), new Int2D(5,6),
        new Int2D(6,5), new Int2D(6,4), new Int2D(6,3), new Int2D(6,2), new Int2D(6,1), new Int2D(6,0), new Int2D(7,0),
        new Int2D(8,0), new Int2D(8,1), new Int2D(8,2), new Int2D(8,3), new Int2D(8,4), new Int2D(8,5), new Int2D(9,6),
        new Int2D(10,6), new Int2D(11,6), new Int2D(12,6), new Int2D(13,6), new Int2D(14,6), new Int2D(14,7), new Int2D(14,8),
        new Int2D(13,8), new Int2D(12,8), new Int2D(11,8), new Int2D(10,8), new Int2D(9,8), new Int2D(8,9), new Int2D(8,10),
        new Int2D(8,11), new Int2D(8,12), new Int2D(8,13), new Int2D(8,14), new Int2D(7,14), new Int2D(6,14), new Int2D(6,13),
        new Int2D(6,12), new Int2D(6,11), new Int2D(6,10), new Int2D(6,9), new Int2D(5,8), new Int2D(4,8), new Int2D(3,8),
        new Int2D(2,8), new Int2D(1,8), new Int2D(0,8)
    };
    // Spielernamen, die Statistiken werden aus Sicht des observed players betrachtet.
    public String[] names = {"Observed", "Peter", "Hans", "Heinrich"};
    // Player[] das alle Agentenobjekte, welche das steppable Interface implementiert sammelt.
    public Player[] players = new Player[4];
    public MersenneTwisterFast rng;

    // Methoden für Model-Tab
    public String getPlayer2() { return names[1]; }
    public void setPlayer2(String val) { if (!val.equals("")) names[1] = val; }

    public String getPlayer3() { return names[2]; }
    public void setPlayer3(String val) { if (!val.equals("")) names[2] = val; }

    public String getPlayer4() { return names[3]; }
    public void setPlayer4(String val) { if (!val.equals("")) names[3] = val; }

    /** 
     * @param seed      Der seed für den MersenneTwisterFast der Simulationsumgebung, wird auch an die Agenten übergeben.
     *                  Ermittelt immer durch System.currentTimeMillis().
     * @param strats    Die zu simulierende Strategiekombination
     */
    public PlayingGround(long seed, String[] strats) {
		super(seed);
        // rng wird mit seed erstellt
        this.seed = seed;	
        this.rng = new MersenneTwisterFast(this.seed);
        // 2D Spielfeld wird erstellt
        field = new SparseGrid2D(fieldWidth, fieldHeight);
        this.strategies = strats;
        createPlayers();
	}

    public void createPlayers() {
        int[] RollsForOrder = new int[numPlayers];
        // DIe Agenten werden erstellt und rollen 1x den Würfel um den ersten Spieler zu bestimmen, dieser ist grün.
        // Alle anderen setzen sich der originalen Namensreihenfolge entsprechend ans Spielbrett 
        for(int i = 0; i<this.numPlayers; i++){
            players[i] = new Player(names[i], strategies[i], rng, field);
            RollsForOrder[i] = players[i].throwDice();
        }
        // Der höchste Roll wird ermittelt
        int highestRollPlayerIndex = getIndexOfHighestRoll(RollsForOrder);
        // Spieler werden entsprechend des ersten Wurfes hingesetzt, hierbei ist nur der erste höchste Wurf relevant. 
        players = getOrderedPlayers(highestRollPlayerIndex);
        // alle Variablen die sich aus der Sitzreihenfolge für den Spieler ergeben werden gesetzt.
        setOrderDependantVariables();
    }

    
    /** 
     * @param IndexHighestRoll Index des Spielers mit dem höchsten Wurf im Array.
     * @return Player[]
     */
    public Player[] getOrderedPlayers(int IndexHighestRoll) {
        // Höchster Roll --> erster Spieler, alle anderen werden der Sitzreihenfolge nach geordnet 
        // z.B. 1. Index 3 --> 2. Index 0, 3. Index 1, 4. Index 2 
        Player[] ordered = new Player[numPlayers];
        Player HighestRollPlayer = players[IndexHighestRoll];
        ordered[0] = HighestRollPlayer;
        int currentIndex = IndexHighestRoll;
        int toFill = 1;
        while(toFill<numPlayers){
            if(currentIndex == numPlayers-1) currentIndex = 0;
            else currentIndex++;
            ordered[toFill] = players[currentIndex];
            toFill++;
        }
        return ordered;
    }
    
    /** 
     * @param executed_move Variablen setter. Wird nahc jedem ausgeführten Zug gesetzt um alle Moves für die Auswertung in der GUI Klasse
     *                      speichern zu können. 
     */
    public void setExecutedMove(Move executed_move){
        move_this_turn = executed_move;
    }
    
    /** 
     *@description Setzt alle Variablen für die Spieler, welche abhängig von der Sitzreihenfolge sind.
     */
    public void setOrderDependantVariables() {
        for(int i = 0; i<this.numPlayers; i++){
            players[i].setOrderDependantVariables(FigureStarts[i],FigureFinishes[i],two_d_spawns[i], two_d_finish_line[i],i );
        }
    }

    
    /** 
     * @param rolls Die 4 ersten Rolls jedes Spiels. Wichtig für die Sitzreihenfolge.
     * @return int Index des Spielers mit dem höchsten Wurf im Array players
     */
    public int getIndexOfHighestRoll(int[] rolls) {
        if ( rolls == null || rolls.length == 0 ) return -1; // null or empty
        int largest = 0;
        for (int i = 1; i < rolls.length; i++ ){
            if (rolls[i]>rolls[largest]) largest = i;
        }
        return largest; // position of the first largest found
    }

    
    /** 
     * @description setzt den Spielernamen auf den ersten freien Index im placements Array
     * @param player_name Spieler der eine Platzierung erhalten soll
     * @return int Die Anzahl der bereits fertigen Spieler.
     */
    public int determinePlacement(String player_name) {
        placements.add(player_name);
        return placements.size();
    }
}
