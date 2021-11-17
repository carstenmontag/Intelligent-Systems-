package ludo;

import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import sim.util.Bag;

public class Move {
    public GamePiece piece;
    public GamePiece[] start_field;
    public SparseGrid2D finish_field;
    public SparseGrid2D field_copy; 
    public int roll;
    public int originx;
    public int targetx;
    public boolean possible;
    public boolean doesBeat;
    

public Move(GamePiece piece, int roll, GamePiece[] start_field, SparseGrid2D field_copy, SparseGrid2D finish_field){
    this.piece = piece;
    this.field_copy = field_copy;
    this.finish_field = finish_field;
    this.start_field = start_field;
    this.roll = roll;
    originx = piece.positionx;
    targetx = determineTarget();
    possible = movePossible();
}

public int determineTarget(){
    if (originx == -1 && roll != 6){return -1;}
    if (originx == -1 && roll == 6){return piece.start;}
    else if (originx +roll >= piece.finish){
    
    }
    else {
        targetx = originx +roll;
        // overshoots field 52 with index 51
        if (targetx>=field_copy.getWidth()){
            targetx = field_copy.getWidth() -targetx - 1 ;
        }
    }
    return targetx;
}
public boolean movePossible(){
    System.out.println(originx + " Roll " + roll +"-->" + targetx);
    if (targetx == -1){return false;}
    // case figure at start
    Bag ObjectsAtTarget = (Bag)field_copy.getObjectsAtLocation(targetx, 0);
    if (ObjectsAtTarget == null) {return true;}
    GamePiece PieceAtTarget = (GamePiece) ObjectsAtTarget.get(0);
    
    if (checkTargetFriendly(PieceAtTarget) == true){return true;}
    else{
        if (overshoots() && canFinish()){ return true;}
        else{return false;}
    }
}

public boolean checkTargetFriendly(GamePiece atTarget){
    if (piece.ownerIndex == atTarget.ownerIndex) {return true;}
    else {return false;}
}
public boolean canFinish(){
    return false;
}
public boolean overshoots(){
    if (targetx>piece.finish) {return true;} 
    else {return false;}
}

public void execute(){
// insert into field move
if (targetx == piece.start){
    insertToField();
}
// insert and beat
// beating move
// finish Piece and set to finish field
// regular move when moving on field without beating
else {}


}
public void insertToField(){
    field_copy.setObjectLocation(piece, new Int2D(targetx,0));
    piece.isset = true;
    piece.positionx = targetx;
}
//beatMove

//insertToFieldMove

//exitToFinishMove

//regularMove

}
