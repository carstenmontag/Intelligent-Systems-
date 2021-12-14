package ludo;
import ec.util.MersenneTwisterFast;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class GamePiece{
    //Spielfigur als Objekt notwendig?
    public int ownerIndex;
    public int positionx;
    public Int2D position_two_d;   
    public Int2D[] finish_line; 
    public int start;
    public int finish;
    public int PieceIndex;
    public boolean hasfinished = false;
    public boolean done = false;
    public boolean blocks = false;

    public Int2D spawn ;
    public SparseGrid2D field;
    public int[] corridor = new int[6];

    public GamePiece(int ownerIndex, int start, int finish, int PieceIndex, Int2D spawn, Int2D[] finish_line, SparseGrid2D field){
        this.ownerIndex = ownerIndex;
        this.start = start;
        this.finish = finish;
        this.PieceIndex = PieceIndex;
        this.spawn = spawn;
        this.finish_line = finish_line;
        this.field = field;
        calc_finish_corridor();
        }
    public void calc_finish_corridor(){
        corridor[0] = finish;
        int tmpfinish = finish;
        if (tmpfinish == 0){
            tmpfinish = 52;
        }
        for(int i = 1; i<corridor.length; i++){
            corridor[i] = tmpfinish-i;
            System.out.println(corridor[i]);
        }
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
    public void set_to_field_loc(int loc){
        Int2D target2d = PlayingGround.locations[loc];
        field.setObjectLocation(this,target2d);
        this.positionx = loc;
        this.position_two_d = target2d;
    }
    public void set_to_finish_loc(int index_finish_array, int pos_on_1d_board ){
        Int2D target2d = finish_line[index_finish_array];
        System.out.println("Setting piece to x " + target2d.getX() + " y " + target2d.getY());  
        field.setObjectLocation(this, target2d);
        this.positionx = pos_on_1d_board;
        this.position_two_d  = target2d; 
        this.hasfinished = true;
    }
    public void set_to_random_loc(){
        int rand = new MersenneTwisterFast().nextInt(52);
        field.setObjectLocation(this, PlayingGround.locations[rand]);
        this.positionx = rand;
        this.position_two_d = PlayingGround.locations[rand];

    }
    

}
