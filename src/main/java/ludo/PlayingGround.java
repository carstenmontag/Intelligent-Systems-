package ludo;

import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class PlayingGround extends SimState {
    //Spielerzahl, sollte über die Konsole verändert werden können
    public long seed;
    public int numPlayers = 4;
    public int test_steps = 2;
    public int numGames = 1;
    //Strategie eines Spielers, sollte über die Konsole verändert werden können
    //random = Alle Aktionen zufällig
    //aggressive = Es sollten nur wenige Figuren im Spiel sein, diese sollten sich schnell bewegen um andere Spiele einzuholen und rauszuwerfen
    //peaceful = Die Figuren werden gleichmäßig verteilt

    //Das gesamte Spielfeld als Reihe (Wenn das letzte Feld erreicht ist soll quasi durchgeloopt werden)
    public boolean game_over = false;
    public SparseGrid2D field;
    public int fieldWidth = 15;
    public int fieldHeight = 15;

    // Spielerparameter werden initialisert, strategies und name  optional später über UI konfigurierbar 
    public String[] strategies = {"random","random", "random","random"};
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

    public String[] names = {"Max", "Peter", "Hans", "Heinrich"};
    public int[] strats = {0, 1, 2, 3};
    public Player[] players = new Player[4];
    public MersenneTwisterFast rng;

    // Methoden für Model-Tab
    public String getPlayer1() { return names[0]; }
    public void setPlayer1(String val) { if (!val.equals("")) names[0] = val; }

    public String getPlayer2() { return names[1]; }
    public void setPlayer2(String val) { if (!val.equals("")) names[1] = val; }

    public String getPlayer3() { return names[2]; }
    public void setPlayer3(String val) { if (!val.equals("")) names[2] = val; }

    public String getPlayer4() { return names[3]; }
    public void setPlayer4(String val) { if (!val.equals("")) names[3] = val; }

    public int getStrategiePlayer1() { return strats[0]; }
    public void setStrategiePlayer1(int val) { if (val <= 3 && val >= 0) strats[0] = val; }
    public Object domStrategiePlayer1() { return strategies; }

    public int getStrategiePlayer2() { return strats[1]; }
    public void setStrategiePlayer2(int val) { if (val <= 3 && val >= 0) strats[1] = val; }
    public Object domStrategiePlayer2() { return strategies; }

    public int getStrategiePlayer3() { return strats[2]; }
    public void setStrategiePlayer3(int val) { if (val <= 3 && val >= 0) strats[2] = val; }
    public Object domStrategiePlayer3() { return strategies; }

    public int getStrategiePlayer4() { return strats[3]; }
    public void setStrategiePlayer4(int val) { if (val <= 3 && val >= 0) strats[3] = val; }
    public Object domStrategiePlayer4() { return strategies; }

    //Das Spielfeld soll wie ein einfaches Array sein, die Spieler spawnen die Figuren an fixen stellen. Falls ein Spieler am Ursprungspunkt-2 ist kommt er auf ein neues kleines array dass die Ziellinie abbildet
    //Spielfeld / Environment / Simulation
    public PlayingGround(long seed) {
		super(seed);
        this.seed = seed;	
        this.rng = new MersenneTwisterFast(this.seed);
        field = new SparseGrid2D(fieldWidth, fieldHeight);
        createPlayers();
	}

    public void start(){
        super.start();
        System.out.println("Starting Sim");
        //startSimulation();
    }

    public void startSimulation() {
        // Queue the Agents in a repeating schedule
        for(int i=0; i<numPlayers; i++){
            schedule.scheduleRepeating(players[i],i, 1.0);
        }
        // Apply the schedule until the game is over
        for(int i=0; i<test_steps; i++) {
        //schedule.step(this);
        //System.out.println("Step" + i);
        }
        System.out.println("Figures on the field :" + field.getAllObjects().size());
    }

    public void createPlayers() {
        //Create the number of players specified in numPlayers
        //ToDo: Choose Strategy
        //Erstellung der Agenten und Festlegung ihrer Reihenfolge
        int[] RollsForOrder = new int[numPlayers];
        // DIe Agenten werden erstellt und rollen 1x den Würfel um den ersten Spieler zu bestimmen
        for(int i = 0; i<this.numPlayers; i++){
            players[i] = new Player(names[i], strategies[i], rng, field);
            RollsForOrder[i] = players[i].throwDice();
            System.out.println(RollsForOrder[i]);
        }
        // Der höchste Roll wird ermittelt
        int highestRollPlayerIndex = getIndexOfHighestRoll(RollsForOrder);
        players = getOrderedPlayers(highestRollPlayerIndex);
        for(int i=0;i<players.length;i++){System.out.println("Spieler "+ players[i].name + " Originaler Index" + players[i].playerIndex + " Neuer Index " + i);}
        setOrderDependantVariables();
    }

    public Player[] getOrderedPlayers(int IndexHighestRoll) {
        // Höchster Roll --> erster Spieler, alle anderen werden der Sitzreihenfolge nach geordnet 
        // z.B. 1. Index 3 --> 2. Index 0, 3. Index 1, 4. Index 2 
        Player[] ordered = new Player[numPlayers];
        Player HighestRollPlayer = players[IndexHighestRoll];
        ordered[0] = HighestRollPlayer;
        System.out.println(HighestRollPlayer.name);
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

    public void setOrderDependantVariables() {
        for(int i = 0; i<this.numPlayers; i++){
            players[i].setOrderDependantVariables(FigureStarts[i],FigureFinishes[i],two_d_spawns[i], two_d_finish_line[i],i );
        }
    }

    public int getIndexOfHighestRoll(int[] rolls) {
        if ( rolls == null || rolls.length == 0 ) return -1; // null or empty
        int largest = 0;
        for (int i = 1; i < rolls.length; i++ ){
            if (rolls[i]>rolls[largest]) largest = i;
        }
        return largest; // position of the first largest found
    }

    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        PlayingGround test = new PlayingGround(seed);
        System.out.println("Seed " + seed);
        test.start();
    }
}
