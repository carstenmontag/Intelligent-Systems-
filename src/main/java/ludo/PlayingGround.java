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
    public int test = 0;
    //Strategie eines Spielers, sollte über die Konsole verändert werden können
    //random = Alle Aktionen zufällig
    //aggressive = Es sollten nur wenige Figuren im Spiel sein, diese sollten sich schnell bewegen um andere Spiele einzuholen und rauszuwerfen
    //peaceful = Die Figuren werden gleichmäßig verteilt

    //Das gesamte Spielfeld als Reihe (Wenn das letzte Feld erreicht ist soll quasi durchgeloopt werden)
    public SparseGrid2D field;
    public int fieldWidth = 6*8+4;
    public int fieldHeight = 1;

    // Spielerparameter werden initialisert, strategies und name  optional später über UI konfigurierbar 
    public String[] strategies = {"random", "aggressive", "peaceful","test"};
    public int[] FigureSpawnLocations = {1,14,27,40};
    public int[] FigureFinishes = new int[4];
    public String[] names = {"Max", "Peter", "Hans", "Heinrich"};
    public Player[] players = new Player[4];
    public MersenneTwisterFast rng;
    //Das Spielfeld soll wie ein einfaches Array sein, die Spieler spawnen die Figuren an fixen stellen. Falls ein Spieler am Ursprungspunkt-2 ist kommt er auf ein neues kleines array dass die Ziellinie abbildet
    

    public void start(){
        super.start();
        field = new SparseGrid2D(fieldWidth, fieldHeight);
        createPlayers();
        getFinishLocations();

        //Jetzt muss die Reihenfolge festgelegt werden
    }

    
    public void createPlayers(){
        //Create the number of players specified in numPlayers
        //ToDo: Choose Strategy
        //Erstellung der Agenten und Festlegung ihrer Reihenfolge
        int[] RollsForOrder = new int[numPlayers];
        // DIe Agenten werden erstellt und rollen 1x den Würfel um den ersten Spieler zu bestimmen
        for(int i = 0; i<this.numPlayers; i++){
            players[i] = new Player(names[i], strategies[i], FigureSpawnLocations[i], FigureFinishes[i], i, rng);
            RollsForOrder[i] = players[i].firstRoll();
            System.out.println(RollsForOrder[i]);
        }
        // Der höchste Roll wird ermittelt
        int highestRollPlayerIndex = getIndexOfHighestRoll(RollsForOrder);
        players = getOrderedPlayers(highestRollPlayerIndex);
        for(int i=0;i<players.length;i++){System.out.println("Spieler "+ players[i].name + " Originaler Index" + players[i].playerIndex + " Neuer Index " + i);}

        //Schedule schedule = new Schedule();
        //schedule.scheduleOnce(players[0]);
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
            if(currentIndex == numPlayers-1) nextIndex = 0;
            else nextIndex = currentIndex++;
            ordered[toFill] = players[currentIndex];
            toFill++;
        }
        return ordered;
    }
    public int getIndexOfHighestRoll(int[] rolls){
        if ( rolls == null || rolls.length == 0 ) return -1; // null or empty
        int largest = 0;
        for (int i = 1; i < rolls.length; i++ ){
            if (rolls[i]>rolls[largest]) largest = i;
        }
        return largest; // position of the first largest found
}
    private void getFinishLocations(){
        for(int i = 0; i<this.FigureSpawnLocations.length; i++){
            FigureFinishes[i] = FigureSpawnLocations[i]-1;    
            
        }
    }

    public static void main(String[] args){
        long seed = System.currentTimeMillis();
        PlayingGround test = new PlayingGround(seed);
        System.out.println("Seed " + seed);
        test.start();
    }
}
