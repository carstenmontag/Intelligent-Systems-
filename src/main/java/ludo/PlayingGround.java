package ludo;

import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.SparseGrid2D;

public class PlayingGround extends SimState {
//Spielfeld / Environment / Simulation
    public PlayingGround(long seed) {
		super(seed);
        this.seed = seed;	
        this.rng = new MersenneTwisterFast(this.seed);
	}

    //Spielerzahl, sollte über die Konsole verändert werden können
    public long seed;
    public int numPlayers = 4;
    public int test_steps = 2;
    //Strategie eines Spielers, sollte über die Konsole verändert werden können
    //random = Alle Aktionen zufällig
    //aggressive = Es sollten nur wenige Figuren im Spiel sein, diese sollten sich schnell bewegen um andere Spiele einzuholen und rauszuwerfen
    //peaceful = Die Figuren werden gleichmäßig verteilt

    //Das gesamte Spielfeld als Reihe (Wenn das letzte Feld erreicht ist soll quasi durchgeloopt werden)
    public SparseGrid2D field;
    public int fieldWidth = 6*8+4;
    public int fieldHeight = 1;

    // Spielerparameter werden initialisert, strategies und name  optional später über UI konfigurierbar 
    public String[] strategies = {"random","random", "random","random"};
    
    public int[] FigureSpawnLocations = {2,15,28,41};
    public int[] FigureFinishes = {0,13,26,39};
    public String[] names = {"Max", "Peter", "Hans", "Heinrich"};
    public Player[] players = new Player[4];
    public MersenneTwisterFast rng;
    //Das Spielfeld soll wie ein einfaches Array sein, die Spieler spawnen die Figuren an fixen stellen. Falls ein Spieler am Ursprungspunkt-2 ist kommt er auf ein neues kleines array dass die Ziellinie abbildet
    

    public void start(){
        super.start();
        field = new SparseGrid2D(fieldWidth, fieldHeight);
        createPlayers();
        startSimulation();
    }

    public void startSimulation(){
        // Queue the Agents in a repeating schedule
        for(int i=0; i<numPlayers; i++){
            schedule.scheduleRepeating(players[i],i, 1.0);
        }
        // Apply the schedule until the game is over
        for(int i=0; i<test_steps; i++) schedule.step(this);
            System.out.println("Figures on the field :" + field.getAllObjects().size());
    }
    public void createPlayers(){
        //Create the number of players specified in numPlayers
        //ToDo: Choose Strategy
        //Erstellung der Agenten und Festlegung ihrer Reihenfolge
        int[] RollsForOrder = new int[numPlayers];
        // DIe Agenten werden erstellt und rollen 1x den Würfel um den ersten Spieler zu bestimmen
        for(int i = 0; i<this.numPlayers; i++){
            players[i] = new Player(names[i], strategies[i], rng);
            RollsForOrder[i] = players[i].throwDice();
            System.out.println(RollsForOrder[i]);
        }
        // Der höchste Roll wird ermittelt
        int highestRollPlayerIndex = getIndexOfHighestRoll(RollsForOrder);
        players = getOrderedPlayers(highestRollPlayerIndex);
        for(int i=0;i<players.length;i++){System.out.println("Spieler "+ players[i].name + " Originaler Index" + players[i].playerIndex + " Neuer Index " + i);}
        setOrderDependantVariables();
    }
    public Player[] getOrderedPlayers(int IndexHighestRoll){
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
    public void setOrderDependantVariables(){
        for(int i = 0; i<this.numPlayers; i++){
            players[i].setOrderDependantVariables(FigureSpawnLocations[i],FigureFinishes[i],i);
        }
    }
    public int getIndexOfHighestRoll(int[] rolls){
        if ( rolls == null || rolls.length == 0 ) return -1; // null or empty
        int largest = 0;
        for (int i = 1; i < rolls.length; i++ ){
            if (rolls[i]>rolls[largest]) largest = i;
        }
        return largest; // position of the first largest found
}
    public static void main(String[] args){
        long seed = System.currentTimeMillis();
        PlayingGround test = new PlayingGround(seed);
        System.out.println("Seed " + seed);
        test.start();
    }
}
