package ludo;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;


public class RunSave {
    String filepath; 
    GameSave[] stats;

    public RunSave(String filepath){
        this.filepath = filepath;
    }
    public void writeColumnsToCSV(String[] columns){
        writeRowToCSV(columns);
    }
    public void writeRowToCSV(String[] row){
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
   public static void main(String[] args) {
    String[] test = {"1","2","3"};   
    RunSave so = new RunSave("test.csv");
    so.writeRowToCSV(test);
        
    }
}

