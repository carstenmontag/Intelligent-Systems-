package ludo;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class GamePiece{
    //Spielfigur als Objekt notwendig?
    public int ownerIndex;
    public int positionx;    
    public int start;
    public int finish;
    public int PieceIndex;
    public boolean isset = false;
    public boolean hasfinished = false;
    public Int2D spawn ;
    public SparseGrid2D field;

    public GamePiece(int ownerIndex, int start, int finish, int PieceIndex, Int2D spawn, SparseGrid2D field){
        this.ownerIndex = ownerIndex;
        this.start = start;
        this.finish = finish;
        this.PieceIndex = PieceIndex;
        this.positionx = -1;
        this.spawn = spawn;
        this.field = field;
       
    }
    public void set_to_spawn(){
        field.setObjectLocation(this, spawn);
    }

}
