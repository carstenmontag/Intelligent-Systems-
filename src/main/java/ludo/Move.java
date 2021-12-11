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
            finishIndex = target-piece.finish-1;
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
        return originx +roll;
    }
    
    // finish logic 
   
    else {
        int target = originx +roll;
        // overshoots field 52 with index 51
        if (target>=PlayingGround.locations.length){
            target = target - PlayingGround.locations.length;
        }
     // finish logic 
    
   
        return target;
    }
    
}
public boolean movePossible(){
    System.out.println(originx + " Roll " + roll +"-->" + targetx);
    int last_possible_finish = piece.finish +6;
    if (piece.finish == 0) {last_possible_finish = 51+6;}
    // case figure at start + target start
    if (targetx == -1 ){return false;}
    else if (inFinishCorridor()){
        if (canFinish()){ return true;}
        else {
            int target = originx + roll ;
            if (target> last_possible_finish) {return false;}     
        }
    }
    else if (piece.hasfinished) {
        if (targetx>last_possible_finish){
            return false;

        }
        return true;
    }
    
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
        if (piece.finish == 0){ target = target-52; }
        if (target > piece.finish&& target <= piece.finish + 4){return true;}
        return false;
    }
}
public boolean canBeat(){
    return false;
}
public boolean overshoots(){ // Formel falsch
    if (targetx>piece.finish) {return true;} 
    else {return false;}
}

public void execute() throws ArithmeticException{

    if (finish!=9999) {
        piece.set_to_finish_loc(finish, targetx);
        return;  
    }
    else if (piece.hasfinished){
        if (piece.finish!= 0){
            piece.set_to_finish_loc(targetx-piece.finish-1, targetx);
            return;
        }
        else {
            piece.set_to_finish_loc(targetx-52, targetx);
        }
    }
    System.out.println("Execute logic : ");
    if (targetx == -1) {throw new ArithmeticException("Targetx is " + targetx + " this is not a valid move.");}
    boolean can_beat = canBeat();
    // insert into field move // no beat
    if (targetx == piece.start && !can_beat){

        insertToField();
    } 
    // insert and beat 
    else if(targetx == piece.start && can_beat){

    }
    else if (!piece.hasfinished) {
        piece.set_to_field_loc(targetx);
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
