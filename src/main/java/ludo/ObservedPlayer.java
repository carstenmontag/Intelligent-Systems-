package ludo;
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import ec.util.*;


public class ObservedPlayer extends Player{
    public ObservedPlayer(String name, String strategy, MersenneTwisterFast rng, SparseGrid2D tempBoard){
        super(name, strategy, rng,tempBoard);

    } 
    public void createStatsObject(){

    }

}
    

