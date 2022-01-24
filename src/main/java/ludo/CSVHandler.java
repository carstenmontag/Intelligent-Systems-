package ludo;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @className CSVHandler
 * @description Diese Klasse ist für alle Input/Output Kommunikationen mit CSV Dateien zuständig. 
 *              Mit ihr können die zu simulierenden Strategiekombinationen eingleseen und die Resultate ausgegeben werden.
 *              Das CSV Format wird genutzt da es uns die Weiterverarbeitung der Statistiken in Python erlichtert.
 */
public class CSVHandler {
    // Array List in welcher alle Statistiken für ein Spiel gespeichet werden.
    ArrayList<GameSave> games_this_comb = new ArrayList<>();
    // Dateipfad zur result CSV
    public String filename;
    public CSVHandler(String timeStamp) {
        // Datei wird mit einem timestamp versehen um Duplikate zu vermeiden.
        this.filename = "results/"+timeStamp+".csv";
    }

    /** 
     * @description Schreibt eine Reihe in die CSV Datei, Die entsprechenden Spaltennamen werden 1x in dem GUI zu Beginn des Programms mit 
     *              dieser Funktion geschrieben.
     *              Sie wird nach jeder Strategiekombination ausgeführt um die Zwischenergebnisse im Falles eines Crashes zu sichern.
     * @param row   Statistiken
     */
    public void writeRowToCSV(String[] row){
        try{
            // Stream zur CSV File wird erstellt.
            FileWriter fw = new FileWriter(filename,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String row_string = null;
            // jedes Element wird abgetrennt mit Komma in einem String verkettet.
            for (String current_column : row){
                if (row_string == null){row_string = row[0];}
                else{row_string+=(";"+current_column);}
            }
            // String schreiben.
            pw.println(row_string);
            pw.flush();
            //Stream schließen.
            pw.close();
        }
        catch(Exception e){
            System.err.println("File writing failed:");
            e.printStackTrace();
        }
    }

    /** 
     * @description Liest die zu simulierenden Strategiekombinationen ein.
     * @param filepath Die CSV Datei in welcher die Startegiekombinationen liegen.
     * @return int[][] als int codierte Strategiekombinationen
     */
    public int[][] readRowsFromCSV(String filepath) {
        String[] data;
        int[][] result = new int[28][4];

        try {
            // InputStream wird geöffnet
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);
            // line by line einlesen der strategien, dabei füllen des Arrays result
            String row;
            int counter = 0;
            while((row = br.readLine()) != null) {
                int[] res = new int[4];
                data = row.split(";");

                for(int i=0; i<=data.length-1; i++) {
                    res[i] = Integer.parseInt(data[i]);
                }
                result[counter] = res;
                counter++;
            }
            // Inputstream wird geschlossen.
            br.close();
        } catch (Exception e){
            System.err.println("File reading failed");
            e.printStackTrace();
        }

        return result;
    }

    /** 
     * @description Die Statistiken für eine Kombination werdfen aus den zwischengespeichrten GameSave Objekten errechnet und
     *              mithilfe der writeRowToCSV/() gesichert.
     * @param strat Aktuelle Strateguekjombination
     */
    public void add_comb(String[] strat){
        String[] calcs = new String[11];
        // stats to calculate
        double games = games_this_comb.size();
        double win_rate;
        double turns_to_finish;
        double average_placement;
        double average_turns_per_game;
        double average_blocks_created;
        double average_kicks;
        double average_got_kicked;
        int game_most_kicks = 0;
        int game_most_blocks = 0;
        int game_most_got_kicked = 0;
        // helper variables
        int turns_observed_total = 0;
        int sum_placement = 0;
        int games_won=0;
        int game_durations_sum = 0;
        int blocks_total = 0;
        int got_kicked_total = 0;
        int kicked_total = 0;
        Iterator<GameSave> it = games_this_comb.iterator();
        // iteration
        while(it.hasNext()){
            // Statistiken berechnen
            GameSave currentGame = it.next();
            sum_placement += currentGame.placement;
            game_durations_sum += currentGame.game_duration;
            turns_observed_total += currentGame.number_of_moves;
            blocks_total += currentGame.number_of_blocks;
            got_kicked_total += currentGame.number_has_been_kicked;
            kicked_total += currentGame.number_of_kicks;
            if (currentGame.placement == 1){games_won++;}
            if (game_most_blocks<currentGame.number_of_blocks){game_most_blocks = currentGame.number_of_blocks;}
            if (game_most_kicks<currentGame.number_of_kicks){game_most_kicks = currentGame.number_of_kicks;}
            if (game_most_got_kicked<currentGame.number_has_been_kicked){game_most_got_kicked = currentGame.number_has_been_kicked;}
        }
        // Aktuelle Strategiestrings verketten.
        String current_strat = "";
        for (int i=0; i<=strat.length-1; i++) {
            if(i== strat.length-1) {
                current_strat+=strat[i];
            } else {
                current_strat+=strat[i]+", ";
            }
        }
        // Stats in Array für die Speicheung vorbereiten.
        calcs[0] = current_strat;

        win_rate = games_won/games;
        calcs[1] = Double.toString(win_rate);

        average_placement = sum_placement/games;
        calcs[2] = Double.toString(average_placement);

        average_turns_per_game = game_durations_sum/games;
        calcs[3] = Double.toString(average_turns_per_game);

        turns_to_finish = turns_observed_total/games;
        calcs[4] = Double.toString(turns_to_finish);

        average_blocks_created = blocks_total/games;
        calcs[5] = Double.toString(average_blocks_created);

        average_kicks = kicked_total/games;
        calcs[6] = Double.toString(average_kicks);

        average_got_kicked = got_kicked_total/games;
        calcs[7] = Double.toString(average_got_kicked);

        calcs[8] = Double.toString(game_most_kicks);

        calcs[9] = Double.toString(game_most_blocks);

        calcs[10] = Double.toString(game_most_got_kicked);
        // speichern
        writeRowToCSV(calcs);
        // Die gespeucherten Games der alten Komination werden gelöscht.
        games_this_comb.clear();
    }

    /** 
     * @description Anaylsiert ein Spiel und speichert Statistiken zu diesem in einem GameSave Objekt zwischen
     * @param current_game
     * @param current_index
     * @param placement
     * @param counter_game_moves
     */
    public void add_run(ArrayList<Move> current_game, int current_index, int placement, int counter_game_moves){
        // wichtige Statistiken
        int number_of_blocks = 0;
        int number_of_kicks = 0;
        int number_of_moves = 0;
        int number_has_been_kicked = 0;
        //Iteation durch alle Moves und Berechnung der Statistiken
        Iterator<Move> it = current_game.iterator();
        while(it.hasNext()){
            Move current_move = it.next();
            if (current_move.playerName.equals("Observed")){
                if (current_move.canBeat){number_of_kicks++;}
                if (current_move.canBlock){number_of_blocks++;}
                number_of_moves++;
            }
            else{
                if (current_move.canBeat && current_move.playerBeaten.equals("Observed")){number_has_been_kicked++;}
            }
        }
        // sichern im GameSave Objekt
        games_this_comb.add(new GameSave(number_of_blocks,number_of_kicks,number_of_moves,number_has_been_kicked,placement+1,counter_game_moves));
    }
}
