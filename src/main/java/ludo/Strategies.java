package ludo;

import java.util.Comparator;

/**
 * @className Strategies
 * @description Diese Klasse beinhaltet alle implementierten Strategien, 
 *              welche vom Steppable Objekt Player genutzt werden um einen Zug auszuwählen.
 *              Diese Strategien basieren auf dem Comparator Interface, welches es ermöglicht
 *              Objekte basierend auf ihren Instanzvariablen miteinander zu vergleichen. 
 *              Jede implementierte Comparator Klasse muss die Funktion "public int compare(Element e1,Element e2) implementieren".
 *              Diese Comparatoren können mithilfe der Utility Fuktion Collections.sort(ArrayList<Move>moves, comparator) genutzt werden.
 *              Die ArrayList wird mithilfe der implementierten compare Funktion sortiert.
 *              Dafür muss die compare Funktion wenn das Objekt e1 höher wertiger beim Vergleich ist 1 zurück gegeben werden, bei Gleichwertigkeit mit e2 0, bei
 *              höherer Wertigkeit von e2 -1.
 *              Die Elemente werden auf Basis von Statistiken aus den möglichen Moves verglichen : Distanz, Schlagen bevorzugen, Blocks maximieren
 *              Komplexere Comparatoren welche mehrere der unten implementierten verketten wären möglich, jedoch mussten wir unsere Ressourcen auf Assignment 2 und 3 verlegen.
 */
public class Strategies {
   
    /**
     * 
     * @className   Beat_Comparator
     * @description Diese Strategie bevorzugt Züge, welche Gegnerspielsteine schlagen.
     */
    public class Beat_Comparator implements Comparator<Move>{
        public int compare(Move m1, Move m2){
            boolean beatsm1 = m1.canBeat;
            boolean beatsm2 = m2.canBeat;

            if ((m1.roll == 6) && (m1.piece.positionx == -1) && (m2.piece.positionx != -1))  {
                return -1;
            } else if ((m1.roll == 6) && (m1.piece.positionx != -1) && (m2.piece.positionx == -1)) {
                return 1;
            } else {
                // die oben beschriebene Logik wird durch diese Utility Funktion ausgeführt.
                return Boolean.compare(beatsm1, beatsm2);
            }
        }   
    }

    /**
     *
     * @className Block_Comparator
     * @description Strategie : Gesamtanzahl blocks aufrecht erhalten bzw erhöhen.
     *              Die gezeigte Wahrheitstabelle wird mithilfe von 3 Logikaussagen überprüft.               
     */
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

            if ((m1.roll == 6) && (m1.piece.positionx == -1) && (m2.piece.positionx != -1))  {
                System.out.println("Returned 1");
                return -1;
            } else if ((m1.roll == 6) && (m1.piece.positionx != -1) && (m2.piece.positionx == -1)) {
                System.out.println("Returned -1");
                return 1;
            }

            if ((!m1.piece.blocks && m1.canBlock && !m2.piece.blocks && !m2.canBlock) || (!m1.piece.blocks && m1.canBlock && m2.piece.blocks && !m2.canBlock)
                    || (!m1.piece.blocks && m1.canBlock && m2.piece.blocks && m2.canBlock) || (!m1.piece.blocks && !m1.canBlock && m2.piece.blocks && !m2.canBlock)
                    || (m1.piece.blocks && m1.canBlock && m2.piece.blocks && !m2.canBlock)) {return 1;}
            else if ((!m1.piece.blocks && !m1.canBlock && !m2.piece.blocks && m2.canBlock) || (m1.piece.blocks && !m1.canBlock && !m2.piece.blocks && !m2.canBlock)
                    || (m1.piece.blocks && !m1.canBlock && !m2.piece.blocks && m2.canBlock) || (m1.piece.blocks && !m1.canBlock && m2.piece.blocks && m2.canBlock)
                    || (m1.piece.blocks && m1.canBlock && !m2.piece.blocks && m2.canBlock)) {return -1;}
            else return 0;
        }
    }

    /**
     * @className First_Comparator
     * @description Strategie : Den Stein, welcher die geringste Distanz zum Ziel hat wird bewegt.
     */
    public class First_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            int distance1 = calc_distance(m1.originx, m1.piece.finish);
            int distance2 = calc_distance(m2.originx, m2.piece.finish);
            return Integer.compare(distance1, distance2);
        }
    }

    /**
     *@className Last_Comparator
     * @description Strategie : Den Stein, welcher die geringste Distanz zum Ziel hat wird bewegt.
     */
    public class Last_Comparator implements Comparator<Move>{
        public int compare(Move m1,Move m2){
            int distance1 = calc_distance(m1.originx, m1.piece.finish);
            int distance2 = calc_distance(m2.originx, m2.piece.finish);
            return Integer.compare(distance2, distance1);
        }
    }

    /**
     * @description  Diese Funktion ist eine utility Funktion, welche die Distanz zwischen der Position
     *               eines Steins und dem finish berechnet. 
     *               Relevant für Last und First Strategie.
     * @param origin Position vor dem Ausführen des Zuges
     * @param finish Letztes Feld vor der Ziellinie
     * @return int
     */
    public static int calc_distance(int origin, int finish){
        if (origin == -1) {
            return 9999;
        }
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
            else {return finish-origin;}
        }
    }
}
