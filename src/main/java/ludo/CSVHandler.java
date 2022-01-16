package ludo;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class CSVHandler {
    GameSave[] games_this_comb;

    public CSVHandler(int games_per_comb){
        this.games_this_comb = new GameSave[games_per_comb];
    }
    public void writeRowToCSV(String[] row, String filepath){
        try{
            FileWriter fw = new FileWriter(filepath,true);
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
    public void add_comb(){
        
    }
    public void add_run(ArrayList<Move> current_game, int current_index, int placement ){
        int number_of_blocks = 0;
        int number_of_kicks = 0;
        int number_of_moves = 0;
        int number_has_been_kicked = 0;
        int game_duration = current_game.size();
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
        games_this_comb[current_index-1]= new GameSave(number_of_blocks,number_of_kicks,number_of_moves,number_has_been_kicked,placement,game_duration);
    }

}
