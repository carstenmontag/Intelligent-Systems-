package ludo;
import java.util.Comparator;


public class Strategies {
    public class Beat_Comparator implements Comparator<Move>{
        public int compare(Move m1, Move m2){
            boolean beatsm1 = m1.canBeat;
            boolean beatsm2 = m2.canBeat;
            if (beatsm1 == beatsm2) {return 0;}
            else if(beatsm1 == true && beatsm2 == false){}
            else {}



        }   
    }
    public class Block_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
           return 0;
        
        }
    }     
}
