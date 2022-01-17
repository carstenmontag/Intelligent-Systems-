package ludo;
import java.util.Comparator;


public class Strategies {
    public class Beat_Comparator implements Comparator<Move>{
        public int compare(Move m1, Move m2){
            boolean beatsm1 = m1.canBeat;
            boolean beatsm2 = m2.canBeat;
            return Boolean.compare(beatsm1, beatsm2);
        }   
    }
    public class Block_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            // Gesamtanzahl blocks aufrecht erhalten bzw erhöhen.
            /*
            block1, canBlock1, block2, canBlock2 Wahrheitstabelle 
            abgedeckt : 
            0000 --> 0
            0101 --> 0
            1010 --> 0
            1111 --> 0
            1100 --> 0
            0011 --> 0
            0100 --> 1 
            0110 --> 1 
            0111 --> 1 
            0010 --> 1 
            1110 --> 1
            0001 --> -1 
            1000 --> -1 
            1001 --> -1 
            1011 --> -1 
            1101 --> -1 
            */
            boolean move1_prio_no_block_create_block = !m1.piece.blocks && m1.canBlock;
            if ((m1.piece.blocks == m2.piece.blocks)&&(m1.canBlock == m2.canBlock)) {return 0;}
            else if ((m1.piece.blocks && m1.canBlock && !m2.piece.blocks && !m2.canBlock)||(!m1.piece.blocks && !m1.canBlock && m2.piece.blocks && m2.canBlock)){return 0;}  
            else if (move1_prio_no_block_create_block || 
                    (m1.piece.blocks && m1.canBlock && m2.piece.blocks && !m2.canBlock)||
                    (!m1.piece.blocks && !m1.canBlock && m2.piece.blocks && !m2.canBlock) )  {return 1;}
            else {return -1;}
            
        }
    }     
    public class First_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            int distance1 = calc_distance(m1);
            int distance2 = calc_distance(m2);
            return Integer.compare(distance2, distance1);
        }
    }
    public class Last_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            int distance1 = calc_distance(m1);
            int distance2 = calc_distance(m2);
            return Integer.compare(distance1, distance2);
        }
    }
    public int calc_distance(Move m){
        int origin = m.originx;
        int finish = m.piece.finish;
        
        if (finish<origin){
            if(finish == 0){return 52-origin;}
            else {return 52-(finish-origin);}
            
        }
        //grün 
        else{
            if (finish == 0)  {finish = 52;}
            return finish-origin;}
        
    }
}
