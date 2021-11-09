package ludo;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import ec.util.*;

public class Player implements Steppable {
    //Agent, jeder Agent soll 4 Spielfiguren haben (sind die Spielfiguren Objekte?)
    //Step, Würfeln, Figur bewegen, falls schon eine Figur da ist Entscheidung treffen, Position der Figur aktualisieren und repeat
    
    public SparseGrid2D tempFinishLine = new SparseGrid2D(0,0);
    public int diceNumber;
    public String name;
    public String strategy;
    public int startPosition;
    public int finish;
    public int playerIndex;
    public int order;
    public MersenneTwisterFast randomGenerator;
    //public int figureNumber = 1;

    //Die Zielline, SparseGrid2D damit mehrere Objekte (Spielfiguren?) darauf gespeichert werden können.
    //Die Ziellinie ist eine eigene Entität für den jeweiligen Spieler, die Verwendung in der Simulation ist unnötig
    
    public SparseGrid2D finishLine;
    public int finishLineWidth = 4;
    public int finidhLineHeight = 1;


    public Player(String name, String strategy, int startPosition, int finish, int playerIndex, MersenneTwisterFast rng){
        super();
        this.name = name;
        this.strategy = strategy;
        this.startPosition = startPosition;
        this.finish = finish;
        this.playerIndex = playerIndex;
        this.randomGenerator = rng;

        System.out.println("Hallo ich bin Spieler "+this.name);
        createFigure(name);
    }
    public void step(SimState state){
        PlayingGround gameboard = (PlayingGround)state;
        tempFinishLine = gameboard.field;
        //Mit state bekommt der Agent den aktuellen Status
        //Hier sollen die Aktionen durchgeführt werden
        throwDice();
        

        //Wenn keine Spielfigur vorhanden ist
        //Würfelwurf -> Veränderung des states, möglich hier?
    }
    public int firstRoll(){
        // Der Würfel wird das erste mal zum feststellen der Würfelreihenfolge 
        int eyes = this.randomGenerator.nextInt(6) + 1; 
        return eyes;

    }
    public void setOrdering(int order){this.order = order;}
    public void createFigure(String name){
        //Die Figuren sollen auf pro Spieler Basis existieren, ist das möglich? Vielleicht die Figuren als einfache Attribute
        //Oder die Figuren als Objekte in der Simulationsenvironment, wäre vermutlich sinnvoll
        Spielfigur figure1 = new Spielfigur(name);
        Spielfigur figure2 = new Spielfigur(name);
        Spielfigur figure3 = new Spielfigur(name);
        Spielfigur figure4 = new Spielfigur(name);
    }

    public void throwDice(){
        //Gibt eine Zahl zwischen 1 und 6 für den Würfelwurf aus, besser als Javas Random
       MersenneTwisterFast m = new MersenneTwisterFast();
       diceNumber = m.nextInt(6);
       diceNumber += 1;

    }

    public void move(Spielfigur figure, int i){
        //move figure 

        //Figur soll ggf. auf die Finishline transfertiert werden und vom originalen Brett gelöscht

    }



    
}
