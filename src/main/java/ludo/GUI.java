package ludo;

import sim.display.*;
import sim.portrayal.Inspector;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;

import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;

public class GUI extends GUIState {
    //Benutzerkonsole
    public Display2D display;
    public JFrame displayFrame;
    public PlayingGround sim;
    SparseGridPortrayal2D boardPortrayal = new SparseGridPortrayal2D();
    public int current_player = 0;
    public Console c;

    // Strategien
    //First = Bewege priorisiert die Figur, die am nächsten am Ziel ist.
    //Last = Bewege priorisiert die Figur, die am weitesten weg com Ziel ist.
    //Prefer_Block = Bewege priorisiert die Figur, die einen Block verursachen kann
    //Prefer_Beat = Bewege priorisiert die Figur, die einen anderen Spieler schlagen kann
    public static String[] strategies = {"First","Last", "Prefer_Block","Prefer_Beat"};
    public static String[][] strat_combinations;

    public boolean simulation_over = false;
    public int num_of_games = 1;

    public static int games_per_comb = 5; // Gibt an wie viele Spiele per Kombination gespielt werden
    public int game_in_comb = 0; // Gibt an wie viele Spiele in der aktuellen Kombination gespielt wurden
    public int current_comb = 1; // Gibt aktuelle Kombination an
    public int counter_game_moves = 0; // Gibt an wie viele Moves das aktuelle Game hat.

    Image redImage = new ImageIcon("src/main/resources/RedPiece.png").getImage();
    Image blueImage = new ImageIcon("src/main/resources/BluePiece.png").getImage();
    Image yellowImage = new ImageIcon("src/main/resources/YellowPiece.png").getImage();
    Image greenImage = new ImageIcon("src/main/resources/GreenPiece.png").getImage();
    public Image[] icon_images = {greenImage, redImage, blueImage, yellowImage};

    Image redImageBlock = new ImageIcon("src/main/resources/RedPieceBlock.png").getImage();
    Image blueImageBlock = new ImageIcon("src/main/resources/BluePieceBlock.png").getImage();
    Image yellowImageBlock = new ImageIcon("src/main/resources/YellowPieceBlock.png").getImage();
    Image greenImageBlock = new ImageIcon("src/main/resources/GreenPieceBlock.png").getImage();
    public Image[] roadblock_images = {greenImageBlock, redImageBlock, blueImageBlock, yellowImageBlock};
    
    public ArrayList<Move> moves_this_game = new ArrayList<Move>();
    public CSVHandler so;

    public static void main (String[] args){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        CSVHandler so = new CSVHandler(timeStamp);
        String[] headine = {"Winrate", "Average Placement", "Average Turns per Game", "Turns to Finish",
                "Average Blocks Created", "Average Kicks", "Average got Kicked"};
        so.writeRowToCSV(headine);

        int[][] int_combinations = so.readRowsFromCSV("src/main/resources/strategy_combinations.csv");
        strat_combinations = new String[int_combinations.length][int_combinations[0].length];

        for(int i=0; i<=int_combinations.length-1; i++) {
            strat_combinations[i] = indices_to_strats(int_combinations[i]);
        }

        PlayingGround baseGround =  new PlayingGround(System.currentTimeMillis(), strat_combinations[0]);
        GUI vid = new GUI(baseGround,so);
    }

    public GUI(PlayingGround baseGround, CSVHandler so){
        super(baseGround);
        c = new Console(this);
        c.setVisible(true);
        this.so = so;
        System.out.println("GUI Construct");
    }

    public static String getName(){
        return "Ludo";
    }

    public Object getSimulationInspectedObject() {return state; }

    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    public void start() {
        super.start();
        setupPortrayals();
        sim = (PlayingGround) state;
        
        // Queue the Agents in a repeating schedule
        System.out.println("Void Start");
        state.schedule.scheduleOnce(0,0,sim.players[0]);
    
        System.out.println("Figures on the field :" + sim.field.getAllObjects().size());

        // Berechne den aktuellen Stand innerhalb der Kombinationen
        game_in_comb++;
        if (game_in_comb >= games_per_comb) {
            current_comb++;
            so.add_comb();
        }
    }

    @Override
    public boolean step(){
        int next_player;
        if (current_player == 3){next_player = 0;}
        else{next_player = current_player +1;}
        boolean success = true;

        // Prüfe ob am Ende der letzen Kombination angekommen
        if (current_comb > strat_combinations.length) {
            simulation_over = true;
        }

        if (!simulation_over){
            success = state.schedule.step(state);

            if (sim.move_this_turn != null){
                moves_this_game.add(sim.move_this_turn);
            }
            counter_game_moves++;

            // Überprüfe ob Game Beendet ist.
            if ((sim.players[0].placement != 0) && (sim.players[1].placement != 0) && (sim.players[2].placement != 0) && (sim.players[3].placement != 0)) {
                sim.finish();
                so.add_run(moves_this_game, game_in_comb, sim.placements.indexOf("Observed"), counter_game_moves);
                counter_game_moves = 0;
                if(game_in_comb == games_per_comb){game_in_comb = 0;}
                moves_this_game.clear();
                
                num_of_games++;
                state = new PlayingGround(System.currentTimeMillis(), strat_combinations[current_comb-1]);
                sim = (PlayingGround)state;
                start();
            }

            // iterate through all GamePieces of the current Player and check if any have to be repainted
            if (sim.redraw_images){
                setupPortrayals();
                sim.redraw_images = false;
            }
            // sim.players[current_player];
            c.refresh();
            int roll = sim.current_roll;
            if (roll == 6 && sim.six_counter<3){
                next_player = current_player; 
                System.out.println("Six Counter :" + sim.six_counter );
            }
            else {
                sim.six_counter = 0;
                System.out.println("Six Counter resetted" );
            
            }
            state.schedule.scheduleOnce(state.schedule.getTime()+1,0,sim.players[next_player]);
            current_player = next_player;
        }

        System.out.println(""+ state.schedule.getSteps());
        System.out.println(""+ state.schedule.getTime());
        // Queue player again
        return success;
    }

    public void setupPortrayals() {
        PlayingGround board = (PlayingGround) state;
        boardPortrayal.setField(board.field);
        // regular Images for GamePieces
        for (int i=0; i <= board.players.length-1; i++) {
            for (int j=0; j<= board.players[i].AtStartPieces.length-1; j++) {
                GamePiece rendering_object = board.players[i].AtStartPieces[j];  
                FacetedPortrayal2D portrayal;
                if (!rendering_object.blocks){portrayal = new FacetedPortrayal2D(new SimplePortrayal2D[]{new ImagePortrayal2D(icon_images[i])});}
                else {portrayal = new FacetedPortrayal2D(new SimplePortrayal2D[]{new ImagePortrayal2D(roadblock_images[i])});}
                boardPortrayal.setPortrayalForObject(board.players[i].AtStartPieces[j], portrayal);
            }
        }
        display.reset();
        setBackground();
        display.repaint();
    }

    public void init(Controller c) {
        super.init(c);
        display = new Display2D(720,720,this);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Ludo Game");
        displayFrame.setResizable(false);
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(boardPortrayal, "FieldBoard" );
        display.attach( new GameOverlay(this), "GameOverlay");
    }

    public void quit() {
        super.quit();
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }

    public void setBackground() {
        //https://commons.wikimedia.org/wiki/File:Ludo_board.svg
        Image i = new ImageIcon("src/main/resources/board.png").getImage();
        BufferedImage b = display.getGraphicsConfiguration().createCompatibleImage(i.getWidth(null), i.getHeight(null));
        Graphics g = b.getGraphics();
        g.drawImage(i,0,0,i.getWidth(null),i.getHeight(null),null);
        g.dispose();
        display.setBackdrop(new TexturePaint(b, new Rectangle(0,0,i.getWidth(null),i.getHeight(null))));
    }

    public static String[] indices_to_strats(int[] indices) {
        return new String[]{strategies[indices[0]], strategies[indices[1]], strategies[indices[2]], strategies[indices[3]]};
    }

}