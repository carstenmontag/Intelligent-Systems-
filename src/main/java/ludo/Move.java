package ludo;

import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import sim.util.Bag;

public class Move {
    public GamePiece piece;
    public GamePiece[] start_field;
    public SparseGrid2D field_copy; 
    public int roll;
    public int originx;
    public int targetx;
    public boolean possible;
    public boolean doesBeat;
    public int finish = 9999;
    public Bag ObjectsAtTarget;
    public boolean canBeat;
    public boolean canBlock;
    public boolean redraw_images;

public Move(GamePiece piece, int roll, GamePiece[] start_field, SparseGrid2D field_copy){
    this.piece = piece;
    this.field_copy = field_copy;
    this.start_field = start_field;
    this.roll = roll;
    originx = piece.positionx;
    targetx = determineTarget();
    possible = movePossible();
}

public int determineTarget(){
    if (originx == -1 && roll != 6){return -1;}
    if(inFinishCorridor()&&canFinish()){
        int target = originx +roll;
        int finishIndex = 9999;
        if (piece.finish == 0) {
            finishIndex = target-1;
            if (originx == 0){
                finishIndex = 51+roll;
            }
            finish = finishIndex-52;
        }
        else {
            finishIndex = target;
            finish = target-piece.finish-1;
        }
        System.out.println("Ab ins Ziel : finish : " + finish + " finishindex : " + finishIndex);
        return finishIndex;
    }
    else if (originx == -1 && roll == 6){return piece.start;}
    else if (piece.hasfinished){
        finish = piece.position_home + roll;
        return originx +roll;
    }
    else {return (originx +roll)%52;}
    
}

public boolean movePossible(){
    System.out.println(originx + " Roll " + roll +"-->" + targetx);
    int last_possible_finish = piece.finish +6;
    if (piece.finish == 0) {last_possible_finish = 51+6;}
    // case figure at start + target start
    if (targetx == -1 ){return false;}
    else if (inFinishCorridor()){
        if (canFinish()){
            return !scanHomeColumnBlock()&&!scanForBlock(); ////// unsicher ? war schon geändert als ich heute angefangen hab, keiner Erinnerung mehr dran
        }
        ////// NULLPOINTER HIER !!!!!! 
        int target = originx + roll ;
        return (target <= last_possible_finish)&&!scanForBlock() ; // eventueller Fix, einfach scannen 
        ///////////////
    }
    else if (piece.hasfinished) {
        return targetx <= last_possible_finish && !scanHomeColumnBlock();
    }
    else if (scanForBlock()) {
        System.out.println(piece.PieceIndex + " of Player " + piece.ownerIndex + " got blocked!");
        return false;
    }
    else if(checkObjectAtTarget()) {
        if(ObjectsAtTarget.numObjs>1) return false; ///// AUSSCHLAGGEBENDER BUGFIX !!! 
        if(checkTargetFriendly((GamePiece) ObjectsAtTarget.get(0))) {
            canBlock = true;
        }
        else {
            canBeat = true;
        }
        return true;
    }
    return true;
}

//true bei Blockade(friendly) 
public boolean scanHomeColumnBlock() {
    Int2D[] finish_line = piece.finish_line;
    // mit indizes für finish_line
    int[] toScan; 
    if (!piece.hasfinished){
        System.out.println("Arraylength calc " +finish+ "+"+1+"="+(finish+1) + "for Player " + piece.ownerIndex + ". Piece: " + piece.PieceIndex + ". Ori:" + originx);
        toScan = new int[finish+1]; 
        for(int i = 0; i<= toScan.length-1; i++){
            toScan[i] = i;
        }
    }
    else{
        toScan = new int[finish-piece.position_home];
        System.out.println("Arraylength calc "+ finish+"-"+piece.position_home+"="+(finish-piece.position_home));
        for(int i = 0; i<= toScan.length-1; i++){
            toScan[i] = piece.position_home +1+i;
        }
    }
    System.out.println("hasfinished "+ piece.hasfinished);
    
    for(int i = 0; i<=toScan.length-1;i++){
        int scanning = toScan[i];
        System.out.println(scanning);
        Bag friendly_piece = (Bag)field_copy.getObjectsAtLocation(finish_line[scanning]);
        // no figure at target
        if (friendly_piece != null) {
            if (scanning == 5) {return false;}
            else {return true;}
            //if (piece_at_scan.position_home<=piece.position_home && piece_at_scan.position_home>= finish){return true;}       
        }
    }
    return false;
}

// true = feindlich Blockade
public boolean scanForBlock() {
    if (originx == -1) {return false;}
    int[] toScan;
    // Länge des Arrays feststellen
    
    ///////// ÄNDERUNGEN HIER BITTE ÜBERPRÜFEN, BIN UNSICHER !!!! 
    ///////// Änderung siehe Screenshot in Discord, origin wird nicht mehr gescannt, stattdessen ein Feld weiter hinten , deshalb +1 
    ///////// BSP : originx = 24, targetx = 30 
    ///////// vorher scan 24,25,26,27,28
    ///////// jetzt scan  25,26,27,28,29 + !!!! check des letzten Blockes in der MovePossible Methode 
    if (canFinish()) {
        if (piece.finish != 0) {
            toScan = new int[piece.finish-originx];
            for(int i = 0; i<toScan.length-1;i++){
                toScan[i] = (originx+i+1)%52;
            }
        }
        else{ 
            toScan = new int[(52-originx)+1];
            for(int i = 0; i<toScan.length-1;i++){
                int next = (originx+i+1)%52;
                toScan[i] = next;
            }
        }
    }
    else{
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
    System.out.print("Scanning");
    for (int i=0; i<toScan.length-1; i++) {
        int scanning = toScan[i];
        //System.out.println("Scanning Position " + scanning);
        int numPieces = field_copy.numObjectsAtLocation(PlayingGround.locations[scanning].getX(), PlayingGround.locations[scanning].getY());
        System.out.println("Pieces at position " + scanning +" :" + numPieces);
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

public boolean checkObjectAtTarget() {
    ObjectsAtTarget = (Bag)field_copy.getObjectsAtLocation(PlayingGround.locations[targetx]);
    // no figure at target
    if (ObjectsAtTarget == null) {return false;}
    // figure at target
    return true;

}

public boolean inFinishCorridor(){
    int[] corridor = piece.corridor;
    for(int i = 0;i<=corridor.length-1;i++){
        if (piece.positionx == corridor[i]){return true;}
    }
    return false;

}

public boolean checkTargetFriendly(GamePiece atTarget){
    if (piece.ownerIndex == atTarget.ownerIndex) {return true;}
    return false;
}

public boolean canFinish(){
    if (!inFinishCorridor()) {return false;}
    else {
        int target = originx + roll;
        if (piece.finish == 0){ target = target%52; }
        if (target > piece.finish&& target <= piece.finish + 6){return true;}
        return false;
    }
}

public void executeMove() {
    System.out.println("Execute logic : ");
    // ist im Finish Corridor und can finishen
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
    // standart move
    else {
        moveOnField();
    }

    //Überprüfe ob GamePiece done ist
    if(piece.position_two_d == piece.finish_line[5]) {
        piece.done = true;
    }
}

public void beat(){
    GamePiece target_piece = (GamePiece)ObjectsAtTarget.get(0);
    System.out.println("GamePiece " + piece.PieceIndex + " from Player " + piece.ownerIndex + " beat " + target_piece.PieceIndex + " of Player " + target_piece.ownerIndex);
    target_piece.set_to_spawn();
}

public void resolve_block(){
    System.out.println("Ori: " + originx);
    piece.blocks = false; 
    GamePiece ObjectAtOrigin = (GamePiece)field_copy.getObjectsAtLocation(PlayingGround.locations[originx]).get(0);
    ObjectAtOrigin.blocks = false;
    redraw_images = true;
    System.out.println("block resolved. Player: " + piece.ownerIndex + ". Piece: " + piece.PieceIndex + ". Other Piece: " + ObjectAtOrigin.PieceIndex);
}

public void block(){
    piece.blocks = true;
    GamePiece blocks_with = (GamePiece)ObjectsAtTarget.get(0);
    blocks_with.blocks = true;
    redraw_images = true;
}
public void moveOnField(){
    piece.set_to_field_loc(targetx);
    if (piece.blocks){resolve_block();}
    if (canBlock){block();}
    if (canBeat){beat();}
}

public void insertToField(){
    piece.set_to_start();
    if (canBeat){beat();}
    if (canBlock){block();}
}

public void moveToFinishCorridor(){
    piece.set_to_finish_loc(finish, targetx);
    if (piece.blocks){resolve_block();}
}

public void moveOnFinishCorridor(){
    if (piece.finish!= 0){
        piece.set_to_finish_loc(targetx-piece.finish-1, targetx);
    }
    else {
        piece.set_to_finish_loc(targetx-52, targetx);
    }
}
}
