package ludo;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class CSVHandler {
    ArrayList<GameSave> games_this_comb = new ArrayList<GameSave>();
    public String filename;

    public CSVHandler(String timeStamp) {
        this.filename = "results/"+timeStamp+".csv";
    }

    public void writeRowToCSV(String[] row){
        try{
            FileWriter fw = new FileWriter(filename,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String row_string = null;
            for (String current_column : row){
                if (row_string == null){row_string = row[0];}
                else{row_string+=(";"+current_column);}
            }
            pw.println(row_string);
            pw.flush();
            pw.close();
        }
        catch(Exception e){}
    }

    public int[][] readRowsFromCSV(String filepath) {
        String[] data;

        int[][] result = new int[28][4];

        try {
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);

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
            br.close();
        } catch (Exception e){}

        return result;
    }

    public void add_comb(String[] strat){
        String[] calcs = new String[11];
        // TODO null Turns werden noch nicht gezählt
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
            GameSave currentGame = (GameSave)it.next();
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

        String current_strat = "";
        for (int i=0; i<=strat.length-1; i++) {
            if(i== strat.length-1) {
                current_strat+=strat[i];
            } else {
                current_strat+=strat[i]+", ";
            }
        }

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

        writeRowToCSV(calcs);
        games_this_comb.clear();
    }

    public void add_run(ArrayList<Move> current_game, int current_index, int placement, int counter_game_moves){
        int number_of_blocks = 0;
        int number_of_kicks = 0;
        int number_of_moves = 0;
        int number_has_been_kicked = 0;
        System.out.println("Start iteration over moves");
        Iterator<Move> it = current_game.iterator();
        while(it.hasNext()){
            Move current_move = (Move)it.next();
            if (current_move.playerName.equals("Observed")){
                if (current_move.canBeat){number_of_kicks++;}
                if (current_move.canBlock){number_of_blocks++;}
                number_of_moves++;
            }
            else{
                if (current_move.canBeat && current_move.playerBeaten.equals("Observed")){number_has_been_kicked++;}
            }
        }
        System.out.println("Current Index :" + current_index);
        games_this_comb.add(new GameSave(number_of_blocks,number_of_kicks,number_of_moves,number_has_been_kicked,placement+1,counter_game_moves));
    }
}
