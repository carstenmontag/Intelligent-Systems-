package ludo;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;

import ec.util.*;



/**
 * @className Player
 * @description Das Objekt Player imlementiert das Interface Steppable und ist somit ein Agent bzw Akteur in der Simulationsumgebung
 */
public class Player implements Steppable {
    public String name;
    public String strategy;
    public Int2D[] two_d_spawns;
    public Int2D[] two_d_finish_line;
    public int start;
    public int finish;
    public int playerIndex;
    public MersenneTwisterFast randomGenerator;
    public GamePiece[] AtStartPieces = new GamePiece[4];
    public SparseGrid2D tempBoard;
    public int placement;

    /**
     *
     * @param name      Spielername, wichtig wenn "Observed"
     * @param strategy  Strategie des Spielers, implementiert durch einen switch case, 
     *                  welcher je nach Taktik mithilfe von custom Comparators einen Zug aussucht
     * @param rng       MersenneTwisterFast
     * @param tempBoard Referenz auf ddas Spielfeld 
     * @return Player 
     
     */
    public Player(String name, String strategy, MersenneTwisterFast rng, SparseGrid2D tempBoard){
        super();
        this.name = name;
        this.strategy = strategy;
        this.randomGenerator = rng;
        this.tempBoard = tempBoard;
        this.placement = 0; //1=1Platz, 2=2Platz ...
    }
    /** 
     * @description Die step Funktion des Agenten. In ihr würfelt jeder Agent, auf Basis dieses Wurfes werden 4 Move Objekte erstellt.
     *              Diese werden vom Player anhand der Strategie mithilfe eines Comparators sortiert. 
     *              Der beste Move wird ausgewählt.
     * @param state Der aktuelle Status der Simulationsumgebung
     */
    public void step(SimState state){
        System.out.println("Turn Player " + playerIndex);
        // cast für Methoden und Variablenzugriff
        PlayingGround gameboard = (PlayingGround)state;
        // Das aktuelle 2D Spielfeld 
        tempBoard = gameboard.field;
        // würfeln 
        int eyesThisMove = throwDice();
        gameboard.current_roll = eyesThisMove;
        // Steppable Part der 6er Regel Logik
        if (eyesThisMove == 6) gameboard.six_counter++;
        if (gameboard.six_counter == 3) {
            System.out.println("Dritte 6 gewürfelt. Der nächste Spieler ist dran.");
            return;
        } 
        // Mögliche Moves werden ermittelt.
        Move[] PossibleMoves = getPossibleMoves(eyesThisMove);
        if (PossibleMoves.length>0) {
            Move move;
            // Wenn nur ein Move verfügbar ist wird dieser ausgewählt. Andernfalls werden die Züge anhand der Strategie verglichen.
            if (PossibleMoves.length==1){move = PossibleMoves[0];}
            else{move = determineMove(PossibleMoves);}
            System.out.println("Move " + move.originx + " --> " + move.targetx + " Player : "+ playerIndex+ " Piece :" + move.piece.PieceIndex);
            // Zug wird ausgeführt
            move.executeMove();
            // Ausgeführter Move wird zum speichern an die Simulationsumgebung übergeben
            gameboard.setExecutedMove(move);
            // Wenn der Move einen Block setzt oder entfernt wird der redraw flag auf true gesetzt
            gameboard.redraw_images = move.redraw_images;
            //Überprüfe ob alle Pieces des Spielers done sind, wenn ja dann wird die Platzierung gespeichert
            if (AtStartPieces[0].done && AtStartPieces[1].done && AtStartPieces[2].done && AtStartPieces[3].done) {
                placement = gameboard.determinePlacement(name);
            }
        }
        else{
            gameboard.setExecutedMove(null);
        }
    }

    
    /** 
     * @description         Diese Funktion erstellt auf Basis des Wurfes 4 Moveobjekte, diese werden in ihrem Konstruktor
     *                      auf Spielrestriktionen geprüft und bei Verletzung aussortiert,
     * @param eyesThisMove  Zahl zwischen 1 und 6, generiert vom MersenneTwisterFast
     * @return Move[]       Das Array mit allen möglichen Moves.
     */
    public Move[] getPossibleMoves(int eyesThisMove){
        int possible_counter = 0;
        Move[] moves = new Move[4];
        for(int i = 0; i<AtStartPieces.length; i++){
            Move move = new Move(AtStartPieces[i], eyesThisMove, AtStartPieces, name, tempBoard);
            // Konstruktor überprüft auf Spielregeln, Blocks etc.
            if (move.possible){possible_counter++;} // Größe für das Array mgl Moves wird bei Iteration ermittelt.
            moves[i] = move;
        }
        // Mögliche Moves werden zurück gegeben
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

    
    /** 
     * @description Ein Comparator basierend auf dem String strategy wird genutzt um die Moves zu sortieren. Der beste wird ausgewählt
     * @param moves Das Array mit allen möglichen Spielzügen diesen Zug.
     * @return Move Der preferierte Move nach der zugewiesenen Strategie.
     */
    public Move determineMove(Move[] moves){
        
        ArrayList<Move> movesArrayList = new ArrayList<Move>(Arrays.asList(moves));
        Strategies comparators = new Strategies(); 
        switch (strategy){
        case "Prefer_Beat":  
            Strategies.Beat_Comparator beat_comp = comparators.new Beat_Comparator(); 
            Collections.sort(movesArrayList, beat_comp);
            return movesArrayList.get(0);
        case "Prefer_Block" : 
            Strategies.Block_Comparator block_comp = comparators.new Block_Comparator();
            Collections.sort(movesArrayList,block_comp);
            return movesArrayList.get(0);
        case "First":
            Strategies.First_Comparator first_comp = comparators.new First_Comparator();
            Collections.sort(movesArrayList,first_comp);
            return movesArrayList.get(0);
        case "Last":
            Strategies.Last_Comparator last_comp = comparators.new Last_Comparator();
            Collections.sort(movesArrayList,last_comp);
            return movesArrayList.get(0);
        default :
            int rndint = randomGenerator.nextInt(moves.length);
            return moves[rndint];
        }
    }

    
    /** 
     * @param start         Startpunkt auf dem Spielfeld 1D
     * @param finish        letzter Platz vor dem Ziel
     * @param spawns        Startfelder der Spielsteine 2D
     * @param finish_line   die 2D Felder, welche die Zielline für den Spieler darstellen.
     * @param playerIndex   index im Array players der aktuellen PlayingGround Instanz
     */
    public void setOrderDependantVariables(int start, int finish, Int2D[] spawns, Int2D[] finish_line, int playerIndex){
        this.start = start;
        this.finish = finish;
        this.playerIndex = playerIndex;
        this.two_d_spawns = spawns;
        this.two_d_finish_line = finish_line; 
        createFigures();    
    }
    /**
    * @description Spielfiguren werden erstellt und auf das entsprechende Startfeld aus dem spawns Array gesetzt
    */
     public void createFigures(){
        for(int i = 0;i<=AtStartPieces.length-1;i++){
            AtStartPieces[i] = new GamePiece(playerIndex,start,finish,i, two_d_spawns[i],two_d_finish_line,name,tempBoard);
            AtStartPieces[i].set_to_spawn();
        }
    }

    
    /** 
     * @return int
     */
    public int throwDice(){
        //Gibt eine Zahl zwischen 1 und 6 für den Würfelwurf aus, besser als Javas Random 
        return randomGenerator.nextInt(6)+1;
    }
}
