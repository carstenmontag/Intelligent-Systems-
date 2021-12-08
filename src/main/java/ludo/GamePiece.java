package ludo;
import ec.util.MersenneTwisterFast;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class GamePiece{
    //Spielfigur als Objekt notwendig?
    public int ownerIndex;
    public int positionx;
    public Int2D position_two_d;    
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
        this.spawn = spawn;
        this.field = field;
       
    }
    public void set_to_spawn(){
        field.setObjectLocation(this, spawn);
        this.positionx = -1; 
        this.position_two_d = spawn;

    }
    public void set_to_start(){
        Int2D target = PlayingGround.locations[start];
        System.out.println("Target :  x " + target.getX()+ ", y " +target.getY());
        field.setObjectLocation(this, PlayingGround.locations[start]);
        this.positionx = start;
        this.position_two_d = PlayingGround.locations[this.positionx];
    }
    public void set_to_random_loc(){
        int rand = new MersenneTwisterFast().nextInt(52);
        field.setObjectLocation(this, PlayingGround.locations[rand]);
        this.positionx = rand;
        this.position_two_d = PlayingGround.locations[rand];

    }
    

}
