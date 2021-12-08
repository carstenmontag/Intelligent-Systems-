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
    public SparseGrid2D finish_field;

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
    // finish logic 
    else if (originx +roll >= piece.finish){
        return originx +roll;
        // return field_copy.getWidth() + piece.finish
    }
    else {
        int target = originx +roll;
        System.out.println("target before Correction" + targetx);
        // overshoots field 52 with index 51
        if (target>=field_copy.getWidth()){
            target = target - field_copy.getWidth();
        }
    return target;
    }
}
public boolean movePossible(){
    System.out.println(originx + " Roll " + roll +"-->" + targetx);
     // case figure at start + target start
    if (targetx == -1 ){return false;}
    
    Bag ObjectsAtTarget = (Bag)field_copy.getObjectsAtLocation(targetx, 0);
    // no figure at target
    if (ObjectsAtTarget == null) {return true;}
    // figure at target
    GamePiece PieceAtTarget = (GamePiece) ObjectsAtTarget.get(0);
    
    if (checkTargetFriendly(PieceAtTarget) == true){return false;}
    else{return true;}
    //if (overshoots() && canFinish()){ return true;}
    //else{return false;}
}


public boolean checkTargetFriendly(GamePiece atTarget){
    if (piece.ownerIndex == atTarget.ownerIndex) {return true;}
    return false;
}
public boolean canFinish(){
    return false;
}
public boolean canBeat(){
    return false;
}
public boolean overshoots(){ // Formel falsch
    if (targetx>piece.finish) {return true;} 
    else {return false;}
}

public void execute() throws ArithmeticException{

    System.out.println("Execute logic : ");
    if (targetx == -1) {throw new ArithmeticException("Targetx is " + targetx + " this is not a valid move.");}
    boolean can_beat = canBeat();
    // insert into field move // no beat
    System.out.println(""+ targetx + piece.start + can_beat);
    if (targetx == piece.start && !can_beat){
        System.out.println("Figure "+ piece.PieceIndex+ " has been moved.");
        System.out.println("old pos " + piece.positionx);
        insertToField();
        System.out.println("new pos " +piece.positionx);

    }  
    // insert and beat 
    //else if(targetx == piece.start && can_beat){
    
    else {
        piece.set_to_random_loc();
    }
// regular move

// regular beating move

// finish Piece and set to finish field

// move on finish field

}
public void insertToField(){
    piece.set_to_start();
    piece.isset = true;
    

}
//beatMove

//insertToFieldMove

//exitToFinishMove

//regularMove

}
