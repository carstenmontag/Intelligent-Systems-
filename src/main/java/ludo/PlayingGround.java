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
        //Erstellung der Agenten
        for(int i = 0; i<this.numPlayers; i++){
            players[i] = new Player(names[i], strategies[i], FigureSpawnLocations[i], rng);
        }
        Schedule schedule = new Schedule();
        schedule.scheduleOnce(players[0]);
    }
    
    private void getFinishLocations(){
        for(int i = 0; i<this.FigureSpawnLocations.length; i++){
            this.FigureFinishes[i] = this.FigureSpawnLocations[i]-1;        
        }
    }

    public static void main(String[] args){
        long seed = System.currentTimeMillis();
        PlayingGround test = new PlayingGround(seed);
        System.out.println("Seed " + seed);
        test.start();
    }
}
