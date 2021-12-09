package ludo;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import ec.util.*;

public class Player implements Steppable {
    //Agent, jeder Agent soll 4 Spielfiguren haben (sind die Spielfiguren Objekte?)
    //Step, Würfeln, Figur bewegen, falls schon eine Figur da ist Entscheidung treffen, Position der Figur aktualisieren und repeat
    public int diceNumber;
    public String name;
    public String strategy;
    public Int2D[] two_d_spawns;
    public Int2D[] two_d_finish_line;
    public int start;
    public int finish;
    public int playerIndex;
    public int order;
    public MersenneTwisterFast randomGenerator;
    public GamePiece[] AtStartPieces = new GamePiece[4];
    public SparseGrid2D tempBoard;
    public SparseGrid2D finishLine;
    public int win;
    //public int figureNumber = 1;

    //Die Zielline, SparseGrid2D damit mehrere Objekte (Spielfiguren?) darauf gespeichert werden können.
    //Die Ziellinie ist eine eigene Entität für den jeweiligen Spieler, die Verwendung in der Simulation ist unnötig
    
    public int finishLineWidth = 4;
    public int finishLineHeight = 1;

    public Player(String name, String strategy, MersenneTwisterFast rng, SparseGrid2D tempBoard){
        super();
        this.name = name;
        this.strategy = strategy;
        this.randomGenerator = rng;
        this.tempBoard = tempBoard;
        this.win = 0;
        
    }
    public void step(SimState state){ 
        System.out.println("Turn Player " + playerIndex);
        PlayingGround gameboard = (PlayingGround)state;
        tempBoard = gameboard.field;
        //Mit state bekommt der Agent den aktuellen Status
        //Hier sollen die Aktionen durchgeführt werden
        int eyesThisMove = throwDice();
        Move[] PossibleMoves = getPossibleMoves(eyesThisMove);
        System.out.println("Possible moves : " + PossibleMoves.length);
        if (PossibleMoves.length>0) {
            Move move = determineMove(PossibleMoves);
            System.out.println("Move" + move.originx + " --> " + move.targetx );
            move.execute();
        }
        else {return;}
    }
    // mögliche Züge werden in der Form eines Objektes dargestellt
    // für jeden Move wird errechnet ob er durch die Spielrestriktionen mgl ist
    // sind mehrere Moves mgl so wird nach der angegebenen Strategie entschieden --> ein Move bekommt Attribute wie "beat" oder "keep up", hiernach kann entschieden werden
    public Move[] getPossibleMoves(int eyesThisMove){
        int possible_counter = 0;
        Move[] moves = new Move[4];
        for(int i = 0; i<AtStartPieces.length; i++){
            Move move = new Move(AtStartPieces[i], eyesThisMove, AtStartPieces, tempBoard, finishLine);
            if (move.possible){possible_counter++;}
            moves[i] = move;
        }
        Move[] possibleMoves = new Move[possible_counter];
        int nextIndex = 0;
        for(int i = 0;i<moves.length;i++){
            if (moves[i].possible){
                possibleMoves[nextIndex] = moves[i];
                nextIndex++;
            }
        }
        return possibleMoves;
    }

    public Move determineMove(Move[] moves){
        //Strategien implementieren
        // random decision
       
       /* switch (strategy){
        case "random":  System.out.println("test");
        }
        */
        int rndint = randomGenerator.nextInt(moves.length);
        return moves[rndint];
    }
    public void setOrderDependantVariables(int start, int finish, Int2D[] spawns, Int2D[] finish_line, int playerIndex){
        this.start = start;
        this.finish = finish;
        this.playerIndex = playerIndex;
        this.two_d_spawns = spawns;
        this.two_d_finish_line = finish_line; 
        System.out.println("Variables for Player "+name+" set.");
        createFigures();    
    }
    public void createFigures(){
        //Die Figuren sollen auf pro Spieler Basis existieren, ist das möglich? Vielleicht die Figuren als einfache Attribute
        //Oder die Figuren als Objekte in der Simulationsenvironment, wäre vermutlich sinnvoll
        for(int i = 0;i<=AtStartPieces.length-1;i++){
            AtStartPieces[i] = new GamePiece(playerIndex,start,finish,i, two_d_spawns[i],tempBoard);
            AtStartPieces[i].set_to_spawn();
        }
    }
    public int throwDice(){
        //Gibt eine Zahl zwischen 1 und 6 für den Würfelwurf aus, besser als Javas Random
        int eyes = randomGenerator.nextInt(6) + 1; 
        return eyes;
    }

}
