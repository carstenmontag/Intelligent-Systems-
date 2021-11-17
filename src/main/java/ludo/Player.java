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
    public int start;
    public int finish;
    public int playerIndex;
    public int order;
    public MersenneTwisterFast randomGenerator;
    public GamePiece[] AtStartPieces = new GamePiece[4];
    SparseGrid2D tempBoard;
    //public int figureNumber = 1;

    //Die Zielline, SparseGrid2D damit mehrere Objekte (Spielfiguren?) darauf gespeichert werden können.
    //Die Ziellinie ist eine eigene Entität für den jeweiligen Spieler, die Verwendung in der Simulation ist unnötig
    
    public int finishLineWidth = 4;
    public int finishLineHeight = 1;
    public SparseGrid2D finishLine ;

    public Player(String name, String strategy, MersenneTwisterFast rng){
        super();
        this.name = name;
        this.strategy = strategy;
        this.randomGenerator = rng;
        this.finishLine = new SparseGrid2D(finishLineWidth,finishLineHeight);
    }
    public void step(SimState state){ 
        PlayingGround gameboard = (PlayingGround)state;
        tempBoard = gameboard.field;
        //Mit state bekommt der Agent den aktuellen Status
        //Hier sollen die Aktionen durchgeführt werden
        int eyesThisMove = throwDice();
        Move[] PossibleMoves = getPossibleMoves(eyesThisMove);
        if (PossibleMoves.length>0) {
            Move move = determineMove(PossibleMoves);
            move.execute();
        }
        else {return;}
        System.out.println("Possible moves : " + PossibleMoves.length);
        // System.out.println("Player "+ playerIndex);
        // System.out.println("Roll: " +eyesThisMove);
        // for(int i = 0; i<locs.length; i++){
        //     System.out.println("Location "+ i+ " "+locs[i]);
        // }
        // System.out.println("pieces set " + PiecesSet());
        tempBoard = null;
        //Wenn keine Spielfigur vorhanden ist
        //Würfelwurf -> Veränderung des states, möglich hier?
    }
    // mögliche Züge werden in der Form int[gamepiece index, new_position] dargestellt
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
        return moves[0];
    }
    public void setOrderDependantVariables(int start, int finish, int playerIndex){
        this.start = start;
        this.finish = finish;
        this.playerIndex = playerIndex;
        System.out.println("Variables for Player "+name+" set.");
        createFigures();    
    }
    public void createFigures(){
        //Die Figuren sollen auf pro Spieler Basis existieren, ist das möglich? Vielleicht die Figuren als einfache Attribute
        //Oder die Figuren als Objekte in der Simulationsenvironment, wäre vermutlich sinnvoll
        for(int i = 0;i<=AtStartPieces.length-1;i++){
            AtStartPieces[i] = new GamePiece(playerIndex,start,finish,i);
        }
    }
    public int throwDice(){
        //Gibt eine Zahl zwischen 1 und 6 für den Würfelwurf aus, besser als Javas Random
        int eyes = randomGenerator.nextInt(6) + 1; 
        return eyes;
    }

}
