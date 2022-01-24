package ludo;

import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

/**
 * @className GamePiece
 * @description Diese Klasse stellt einen Spielstein auf dem Feld dar. 
 *              Sie enthält einige Variablen die für Berechnungen in der Klasse Move wichtig sind.
 *              Weiterhin werden alle Bewegungen eines Spielsteins in dieser Klasse implementiert.
 *              In diesen findet die Übersetzung Position 0-52 bzw finish 1-6 statt in eine 2d 
 *              Position zur Darstellung auf dem Spielfeld in der GUI statt und die Variablen zur 
 *              Berechnung der nächsten Moves werden gesetzt.
 *              
 */
public class GamePiece{
    // Variablen für Berechnungen in der Klasse Move
    public int ownerIndex;
    public int position_home = -1;
    public int positionx;
    public Int2D position_two_d;   
    public Int2D[] finish_line; 
    public int start;
    public int finish;
    // Daten für Zuordnung zum Spieler
    public int PieceIndex;
    public String playerName;
    // Marker für Spiellogik
    public boolean hasfinished = false;
    public boolean done = false;
    public boolean blocks = false;
    // Die Startposition auf dem Feld
    public Int2D spawn ;
    // eine Referenz auf das 2d Spielfeld
    public SparseGrid2D field;
    // die 6 Felder vor dem Finishfeld, wird in calc_finish_corridor berechnet
    public int[] corridor = new int[6];

    public GamePiece(int ownerIndex, int start, int finish, int PieceIndex, Int2D spawn, Int2D[] finish_line, String playerName, SparseGrid2D field){
        this.ownerIndex = ownerIndex;
        this.playerName = playerName;
        this.start = start;
        this.finish = finish;
        this.PieceIndex = PieceIndex;
        this.spawn = spawn;
        this.finish_line = finish_line;
        this.field = field;
        calc_finish_corridor();
    }

    /**
     * @description Berechnet den Finish Korridorm, dieser inkludiert 6 Felder vor dem Ziel des Spielsteins.
     *              Utility Funktion. Wenn ein Spielstein sich in diesem Korridor befindet muss geprüft werden ob er ins Ziel kann. 
     */
    public void calc_finish_corridor(){
        corridor[0] = finish;
        int tmpfinish = finish;
        if (tmpfinish == 0){
            tmpfinish = 52;
        }
        for(int i = 1; i<corridor.length; i++){
            corridor[i] = tmpfinish-i;
        }
    }

    /**
     * @description Setzt den Stein auf den im Konstruktor festgelegten Spawn.
     */
    public void set_to_spawn(){
        field.setObjectLocation(this, spawn);
        this.positionx = -1; 
        this.position_two_d = spawn;

    }

    /**
     * @description Wird beim würfeln einer 6 dazu genutzt den Stein auf das erste Feld zu setzen.
     */
    public void set_to_start(){
        field.setObjectLocation(this, PlayingGround.locations[start]);
        this.positionx = start;
        this.position_two_d = PlayingGround.locations[this.positionx];
    }
    
    /** 
     * @description Der Stein wird auf eine 1d Position auf dem Feld gesetzt. 
     *              Die 2d Darstellung wird mithilfe des locations Arrays erreicht, welche zu 
     *              jeder 1d Position 0-52 korrespondierend, ermittelbar mit loc als Index, eine 2d Koordinate enthält.
     * @param loc Die 1d Position auf welche der Stein gesetzt wird.
     */
    public void set_to_field_loc(int loc){
        Int2D target2d = PlayingGround.locations[loc];
        field.setObjectLocation(this,target2d);
        this.positionx = loc;
        this.position_two_d = target2d;
    }
    
    /** 
     * @description Hierzu wird das instanzspezifische Int2d[] finish_line genutzt. Die entsprechende 2d
     *              Position wird mit index_finish_array ermittelt
     * @param index_finish_array Die Position auf der Zielline. 0-5
     * @param pos_on_1d_board    53,54,55 ... zB für grün, für Logik in move
     */
    public void set_to_finish_loc(int index_finish_array, int pos_on_1d_board ){
        Int2D target2d = finish_line[index_finish_array];
        field.setObjectLocation(this, target2d);
        this.positionx = pos_on_1d_board;
        this.position_two_d  = target2d; 
        this.hasfinished = true;
        this.position_home = index_finish_array;
    }
}
