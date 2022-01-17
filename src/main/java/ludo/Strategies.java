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
            if ((m1.piece.blocks == m2.piece.blocks)&&(m1.canBlock == m2.canBlock)) {return 0;}
            else if ((m1.piece.blocks && m1.canBlock && !m2.piece.blocks && !m2.canBlock)||(!m1.piece.blocks && !m1.canBlock && m2.piece.blocks && m2.canBlock)){return 0;}  
            else if (!m1.piece.blocks && m1.canBlock ||
                    (m1.piece.blocks && m1.canBlock && m2.piece.blocks && !m2.canBlock)||
                    (!m1.piece.blocks && !m1.canBlock && m2.piece.blocks && !m2.canBlock))  {return 1;}
            else {return -1;}
        }
    }

    public class First_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            int distance1 = calc_distance(m1.originx, m1.piece.finish);
            int distance2 = calc_distance(m2.originx, m2.piece.finish);
            return Integer.compare(distance2, distance1);
        }
    }

    public class Last_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            int distance1 = calc_distance(m1.originx, m1.piece.finish);
            int distance2 = calc_distance(m2.originx, m2.piece.finish);
            return Integer.compare(distance1, distance2);
        }
    }

    public static int calc_distance(int origin, int finish){
        // Stein steht vor HomeColumn
        if (finish == origin) {
            return 0;
        }
        //Ziel liegt hinter Stein
        if (finish<origin){
            // grün
            if(finish == 0){return 52-origin;}
            // rot, gelb, blau
            else {return 52-(origin-finish);}
            
        }
        //Ziel liegt vor Stein
        else{
            //grün
            if (finish == 0)  {return 52-origin;}
            // rot, gelb, blau
            else {
                return finish-origin;
            }
        }
    }

    public static void main (String[] args){
        int fig = 0; // finish grün
        int fir = 13; // finish rot
        int fib = 26; // finish blau
        int fiy = 39; // finish gelb

        //Normale Fälle
        int or1 = 6; //Befindet sich vor Ziel. Testsubjekt: Rot, Erw. Ergebnis: 7

        int or2 = 37; // Befindet sich hinter Ziel. Testsubjekt: Blau, Erw. Ergebnis: 41

        int or3 = 39; // Befindet sich auf dem Ziel. Testsubjekt: Gelb, Erw. Ergebnis: 0

        int z1 = calc_distance(or1, fir);
        int z2 = calc_distance(or2, fib);
        int z3 = calc_distance(or3, fiy);

        System.out.println("Erg. z1: " + z1);
        System.out.println("Erg. z2: " + z2);
        System.out.println("Erg. z3: " + z3);

        //Sonderfälle Grüner Spieler
        int or4 = 50; //Befindet sich vor Ziel. Erw. Ergebnis: 2

        int or5 = 4; //Befindet sich hinter Ziel. Erw. Ergebnis: 48

        int or6 = 0; //Befindet sich auf dem Ziel. Erw. Ergebnis: 0

        int z4 = calc_distance(or4, fig);
        int z5 = calc_distance(or5, fig);
        int z6 = calc_distance(or6, fig);

        System.out.println("Erg. z4: " + z4);
        System.out.println("Erg. z5: " + z5);
        System.out.println("Erg. z6: " + z6);

    }

}
