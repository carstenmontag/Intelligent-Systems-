package ludo;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class CSVHandler {
    GameSave[] stats;

    //public void writeColumnsToCSV(String[] columns){writeRowToCSV(columns);}

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

        } catch (Exception e){}

        return result;
    }

    public static void main(String[] args) {
        String[] test = {"1","2","3"};
        CSVHandler so = new CSVHandler();
        so.writeRowToCSV(test, "test.csv");

        int[][] res = so.readRowsFromCSV("src/main/resources/strategy_combinations.csv");

        for (int[] r: res) {
            System.out.println(Arrays.toString(r));
        }

    }

}
