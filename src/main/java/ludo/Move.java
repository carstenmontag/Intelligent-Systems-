package ludo;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import sim.util.Bag;

/**
 * @description In dieser Klasse befindet sich ein Großteil der Spielelogik.
 *              Wenn ein Agent einen Zug beginnt werden 4 Move Objekte, für jeden Spielstein eines, erstellt 
 *              und abhängig von der geworfenen Zahl bewertet.
 *              Hierfür wird erst das Ziel des entsprechenden Zielsteins berechnet und 
 *              anschließend abhängig von dem aktuellen Stauts der Simulationsumgebung, ob dieser Move möglich ist. 
 *              Das Board wird innerhalb dieser Klasse von einem 2d Feld auf ein 1d Feld mit 52 Positionen 0-51 vereinfacht. 
 *              Spielsteine am Start erhalten die Position -1, Steine im Ziel zählen diesen int weiter hoch. 
 *              Dabei werden nicht überwindbare Blockaden und Stacks etc. berücksichtigt. Ist ein Move nicht mögklich, so kann er
 *              in der step Funktion des Agenten mithilfe des boolean possible aussortiert werden.
 *              Außerdem werden dem Move die boolean Attribute canBlock und canBeat zugewiesen, diese sind relevant für die Taktik-Comparatoren .
 *              Nachdem eine Entscheidung mithilfe des entsprechenden Takitik Comparatoren getroffen wurde, kann das Move Objekt mit execute() ausgeführt werden.
 *              Daraufhin wird die Figur verschoben und eventuelle Konsequenzen wie Shclagen, blocken etc treten in Kraft.
 *              Da sich in den ausgeführten Move Objekten alle wichtigen Informationen zum Spielverlauf befinden, wird eine Liste aller ausgeführten Moves später
 *              dazu genutzt das Spiel auszuwerten.
 * @className   Move
 */
public class Move {
    // Der Spielstein für welchen die Move Auswertung stattfinden soll
    public GamePiece piece;
    // Eine Kopie des Boards
    public SparseGrid2D field_copy; 
    public int roll;
    // aktuelle Position im 1d Raum
    public int originx;
    // Ziel des Moves im 1d Raum
    public int targetx;
    // ist der Move nach den Spielregeln mgl ? 
    public boolean possible;
    // wenn finish auf 0,1,2....,5 gesetzt ist kann die Figur mit diesem Move ins Ziel
    public int finish = 9999;
    public Bag ObjectsAtTarget;
    // Marker für execute() Funktion und Spielerstrategie
    public boolean canBeat;
    public boolean canBlock;
    // Marker wird gesetzt wenn ein Spielstein einen Block erstellt oder löst
    public boolean redraw_images;
    public String playerName;
    // für Auswertung
    public String playerBeaten = "";
/**
 * @description Routine für jeden Move, hier wird bestimmt ob der Zug mgl ist
 * @param piece
 * @param roll
 * @param playerName
 * @param field_copy
 */
public Move(GamePiece piece, int roll, String playerName, SparseGrid2D field_copy){
    this.piece = piece;
    this.field_copy = field_copy;
    this.roll = roll;
    this.playerName = playerName;
    originx = piece.positionx;
    // Erst wird das Ziel des Zugs berechnet
    targetx = determineTarget();
    // Danach ob dieser mgl ist
    possible = movePossible();
}

/** 
 * @description Berechnet die Position des Spielsteins nach ausführen der execute() Funktion.
 *              Hierbei müssen einige Spezialfälle berücksichtigt werden. 
 * @return int Die 1d Position auf dem Feld
 */
public int determineTarget(){
    // Keine 6 geworfen, noch am Start --> direkt aussortieren
    if (originx == -1 && roll != 6){return -1;}
    // kann ins Ziel 
    if(inFinishCorridor()&&canFinish()){
        int target = originx +roll;
        int finishIndex = 9999;
        // grün --> 0 Übergang 
        if (piece.finish == 0) {
            finishIndex = target-1;
            if (originx == 0){
                finishIndex = 51+roll;
            }
            finish = finishIndex-52;
        }
        // gelb/blau/rot
        else {
            finishIndex = target;
            finish = target-piece.finish-1;
        }
        return finishIndex;
    }
    // 6 geworfen, Stein befindet sich am Start --> Zug auf Spawn des Spielers
    else if (originx == -1 && roll == 6){return piece.start;}
    // Bewegung in der safe Zone des Spielers
    else if (piece.hasfinished){
        finish = piece.position_home + roll;
        return originx +roll;
    // kein Sonderfall
    }
    else {return (originx +roll)%52;}
    
}


/** 
 * @description Diese Funktion überprüft ob dieser Zug möglich ist.
 *              Hierbei müssen einige Sonderfälle betrachtet werden, welche in seperate Funktionen ausgelagert wurden.
 * @return boolean
 */
public boolean movePossible(){
    System.out.println("Piece " + piece.PieceIndex  + " Roll " + roll +"-->" + targetx);
    // das letzte mgl Feld für den Stein/ Ziel wird berechnet
    int last_possible_finish = piece.finish +6;
    if (piece.finish == 0) {last_possible_finish = 51+6;}
    if (targetx == -1 ){return false;}
    // wenn der Spielstein im finishCorrdidor des Spielers, also auf den letzten 6 Feldern vor der Zielline,
    // liegt und der Stein mit dem Zug ins Spiel kommen kann, werden sowohl Feld als auch Zielline auf Blockaden überprüft.
    // true wenn kein Block
    else if (inFinishCorridor()&&canFinish()){
        return !scanHomeColumnBlock()&&!scanForBlock(); }
    // Wenn der Spielstein sich bereits auf der Ziellinie befindet muss geprüft werden ob der Zug "overshooted"
    // oder ein Block vorhanden ist
    else if (piece.hasfinished) {
        return targetx <= last_possible_finish && !scanHomeColumnBlock();
    }
    // befindet sich der Stein nicht in Zielnähe, wird, sofern eine Blockade mit 2 Gegnersteinen vorhanden ist, der Move aussortiert
    else if (scanForBlock()) {
        System.out.println(piece.PieceIndex + " of Player " + piece.ownerIndex + " got blocked!");
        return false;
    }
    // targetx wird in scanforblock nicht geprüft, das wird hier getan
    else if(checkObjectAtTarget()) {
        // Wenn sich mehr als ein Objekt auf dem Zielfeld befindet, wird abgebrochen
        if(ObjectsAtTarget.numObjs>1) return false;  
        // Ein Stein ist auf dem Zielfeld, Marker für execute()/ Taktik werden gesetzt
        // enemy --> beat
        // friendly --> block
        if(checkTargetFriendly((GamePiece) ObjectsAtTarget.get(0))) {
            canBlock = true;
        }
        else {
            canBeat = true;
            GamePiece toBeat= (GamePiece)ObjectsAtTarget.get(0);
            playerBeaten = toBeat.playerName;
        }
        // Attritbut zugewiesen -> true 
        return true;
    }
    // default 
    return true;
}


/** 
 * @description Diese Funktion scannt die eigene Ziellinie auf freundliche Blockaden. 
 *              Ein einzelner Spielstein , welcher zwischen origin und
 *              target des aktuellen Zuges auf der Ziellinie liegt, gilt hierbei bereits als solcher.
 * @return boolean true bei Blockade(friendly)
 */
public boolean scanHomeColumnBlock() {
    Int2D[] finish_line = piece.finish_line;
    // mit indizes für finish_line
    int[] toScan; 
    // zu scannende Felder werden in einem Array gesammelt. 
    // Berechnung unterschiedlich, je nachdem ob Stein bereits im Ziel
    if (!piece.hasfinished){
        toScan = new int[finish+1]; 
        for(int i = 0; i<= toScan.length-1; i++){
            toScan[i] = i;
        }
    }
    else{
        toScan = new int[finish-piece.position_home];
        for(int i = 0; i<= toScan.length-1; i++){
            toScan[i] = piece.position_home +1+i;
        }
    }
    // die ermittelten Felder werden nach Steinen gescannt, befindet sich ein Block im Weg wird true zurück gegeben, 
    // andernfalls wird weiter gelooped. Wurde kein Block gefunden wird false (-> kein Block) zurück gegeben.
    for(int i = 0; i<=toScan.length-1;i++){
        int scanning = toScan[i];
        Bag friendly_piece = (Bag)field_copy.getObjectsAtLocation(finish_line[scanning]);
        // no figure at target bei friendly_piece = null
        if (friendly_piece != null) {
            if (scanning == 5) {return false;}
            else {return true;}
    
        }
    }
    return false;
}


/** 
 * @description Die Felder zwischen origin und target werden gescannt. Dafür wird zuerst eine Liste mit den 
 *              zu scannenden Feldern erstellt, da hier einige Sonderfälle beachtet werden mussten. Diese List wird im Anschluss abgearbeitet.
 * @return boolean Block true, false kein Block
 */
// true = feindlich Blockade
public boolean scanForBlock() {
    if (originx == -1) {return false;}
    int[] toScan;
    // Länge des Arrays feststellen
    // wenn die Figur ins Ziel kann müssen statt normalen Feldern Zielfelder gescannt werden.
    // dies findet nicht hier statt, jedoch müssen weniger Felder gescannt werden.
    if (canFinish()) {
        // Unterscheidung grün/Rest
        if (piece.finish != 0) {
            toScan = new int[piece.finish-originx];
            for(int i = 0; i<toScan.length-1;i++){
                toScan[i] = (originx+i+1)%52;
            }
        }
        else{
            if (originx == 0) {return false;}
            else {toScan = new int[(52-originx)+1];}
            //if (originx == 0) {toScan = new int[targetx-52];} /////// Code Snippet prüfen, scannt das ganze Feld wenn originx == 0
           
            for(int i = 0; i<toScan.length-1;i++){
                int next = (originx+i+1)%52;
                toScan[i] = next;
            }
        }
    }
    // normaler Fall --> canFinish false  
    else{
        // Unterscheidung grün/rest
        if (originx>targetx){
            toScan = new int[(targetx+52)-originx];
            for(int i = 0; i<toScan.length-1;i++){
                toScan[i] = (originx+i+1)%52 ;

            }
        }
        else {
            toScan = new int[targetx-originx];
            for(int i = 0; i<toScan.length-1;i++){
                toScan[i] = originx+i+1;
            }
        }
    }       
    // loop through array
    for (int i=0; i<toScan.length-1; i++) {
        int scanning = toScan[i];
        int numPieces = field_copy.numObjectsAtLocation(PlayingGround.locations[scanning].getX(), PlayingGround.locations[scanning].getY());
        if (numPieces ==2) {
            Bag Pieces = (Bag) field_copy.getObjectsAtLocation(PlayingGround.locations[scanning]);

            GamePiece firstPiece = (GamePiece) Pieces.get(0);
            if (!checkTargetFriendly(firstPiece)) {    
                System.out.println("Blocked at Position" + scanning);
                return true;
            }
            // darf nicht aufs letzte feld da sich ein 3er Stack ergeben würde ! 
            if (i == toScan.length-1){
                return true;
            }
        }
    }
    return false;
}


/** 
 * @return boolean Ist ein Spielstein auf dem Zielfeld
 */
public boolean checkObjectAtTarget() {
    ObjectsAtTarget = (Bag)field_copy.getObjectsAtLocation(PlayingGround.locations[targetx]);
    // no figure at target
    if (ObjectsAtTarget == null) {return false;}
    // figure at target
    return true;

}


/** 
 * @description Es wird überprüft ob sich der Stein im festgelegten Korridor befindet.
 * @return boolean  
 */
public boolean inFinishCorridor(){
    int[] corridor = piece.corridor;
    for(int i = 0;i<=corridor.length-1;i++){
        if (piece.positionx == corridor[i]){return true;}
    }
    return false;

}


/** 
 * @param atTarget
 * @return boolean Wenn der Stein auf dem Zielfeld den gleichen ownerIndex hat ist es ein freundlicher Stein
 */
public boolean checkTargetFriendly(GamePiece atTarget){
    if (piece.ownerIndex == atTarget.ownerIndex) {return true;}
    return false;
}


/** 
 * @return boolean
 */
public boolean canFinish(){
    if (!inFinishCorridor()) {return false;}
    else {
        int target = originx + roll;
        if (piece.finish == 0){ target = target%52; }
        if (target > piece.finish&& target <= piece.finish + 6){return true;}
        return false;
    }
}
/**
 * @description Der berechnete Zug wird ausgeführt. Dabei müssen verschiedene Sonderfälle berücksichtig werden.
 */
public void executeMove() {
    // ist im Finish Corridor und kann finishen
    if (finish!=9999) {
        moveToFinishCorridor();
    }
    // ist finished und bewegt sich auf dem finish feld
    else if (piece.hasfinished){
        moveOnFinishCorridor();
    }
    // ist im Startfeld und bewegt sich aufs Board
    else if (targetx == piece.start){
        insertToField();
    }
    // standard move
    else {
        moveOnField();
    }

    //Überprüfe ob GamePiece done ist
    if(piece.position_two_d == piece.finish_line[5]) {
        piece.done = true;
    }
}

/**
 * @description Stein wird geschlagen, der geschlagene Stein wird an den Start zurück gesetzt.
 */
public void beat(){
    GamePiece target_piece = (GamePiece)ObjectsAtTarget.get(0);
    System.out.println("GamePiece " + piece.PieceIndex + " from Player " + piece.ownerIndex + " beat " + target_piece.PieceIndex + " of Player " + target_piece.ownerIndex);
    target_piece.set_to_spawn();
}
/**
 * @description Ein Block wird gelöst, beide piece.blocks werdern auf false gesetzt, die Steine werden neu gezeichnet
 */
public void resolve_block(){
    piece.blocks = false; 
    GamePiece ObjectAtOrigin = (GamePiece)field_copy.getObjectsAtLocation(PlayingGround.locations[originx]).get(0);
    ObjectAtOrigin.blocks = false;
    redraw_images = true;
    System.out.println("block resolved. Player: " + piece.ownerIndex + ". Piece: " + piece.PieceIndex + ". Other Piece: " + ObjectAtOrigin.PieceIndex);
}
/**
 * @description Ein Block wird erstellt, beide piece.blocks werdern auf true gesetzt, die Steine werden neu gezeichnet
 */
public void block(){
    piece.blocks = true;
    GamePiece blocks_with = (GamePiece)ObjectsAtTarget.get(0);
    blocks_with.blocks = true;
    redraw_images = true;
}
// normaler Zug auf dem Feld ohne Sonderfälle
public void moveOnField(){
    piece.set_to_field_loc(targetx);
    if (piece.blocks){resolve_block();}
    if (canBlock){block();}
    if (canBeat){beat();}
}
// Start -> Spawnfeld
public void insertToField(){
    piece.set_to_start();
    if (canBeat){beat();}
    if (canBlock){block();}
}
// Feld -> Ziellinie
public void moveToFinishCorridor(){
    piece.set_to_finish_loc(finish, targetx);
    if (piece.blocks){resolve_block();}
}
// Bewegung auf der Ziellinie
public void moveOnFinishCorridor(){
    if (piece.finish!= 0){
        piece.set_to_finish_loc(targetx-piece.finish-1, targetx);
    }
    else {
        piece.set_to_finish_loc(targetx-52, targetx);
    }
}
}
