package ludo;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import ec.util.*;

public class Player implements Steppable {
    //Agent, jeder Agent soll 4 Spielfiguren haben (sind die Spielfiguren Objekte?)
    //Step, Würfeln, Figur bewegen, falls schon eine Figur da ist Entscheidung treffen, Position der Figur aktualisieren und repeat
    
    public int diceNumber;
    public String name;
    public String strategy;
    public int startPosition;
    public int finish;
    public int playerIndex;
    public int order;
    public MersenneTwisterFast randomGenerator;
    public GamePiece[] AtStartPieces = new GamePiece[4];
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
        System.out.println("Hallo ich bin Spieler "+this.name);
    }
    public void step(SimState state){ 
        PlayingGround gameboard = (PlayingGround)state;
        SparseGrid2D tempBoard = gameboard.field;
        System.out.println("Step "+ name + gameboard.schedule.getSteps()); 

        //Mit state bekommt der Agent den aktuellen Status
        //Hier sollen die Aktionen durchgeführt werden
        throwDice();
        

        //Wenn keine Spielfigur vorhanden ist
        //Würfelwurf -> Veränderung des states, möglich hier?
    }
    
    public void setOrderDependantVariables(int start, int finish, int playerIndex){
        this.startPosition = start;
        this.finish = finish;
        this.playerIndex = playerIndex;
        System.out.println("Variables for Player "+name+" set.");
        createFigures();    
    }
    public void createFigures(){
        //Die Figuren sollen auf pro Spieler Basis existieren, ist das möglich? Vielleicht die Figuren als einfache Attribute
        //Oder die Figuren als Objekte in der Simulationsenvironment, wäre vermutlich sinnvoll
        for(int i = 0;i<=AtStartPieces.length-1;i++){
            AtStartPieces[i] = new GamePiece(playerIndex);
            
        }
    }

    public int throwDice(){
        //Gibt eine Zahl zwischen 1 und 6 für den Würfelwurf aus, besser als Javas Random
        int eyes = this.randomGenerator.nextInt(6) + 1; 
        return eyes;
    }

    public void move(GamePiece figure, int i){
        //move figure 

        //Figur soll ggf. auf die Finishline transfertiert werden und vom originalen Brett gelöscht

    }



    
}
