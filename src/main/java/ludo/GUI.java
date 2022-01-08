package ludo;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.Inspector;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;

public class GUI extends GUIState {
    //Benutzerkonsole
    public Display2D display;
    public JFrame displayFrame;
    public PlayingGround sim;
    public Controller con;
    SparseGridPortrayal2D boardPortrayal = new SparseGridPortrayal2D();
    public int num_players = 4;
    public int steps_per_sim = 3;
    public int sims = 1;
    public int current_player = 0;
    public Console c;
    public PlayingGround emptyPlayingGround ;

    String strats[][];

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

    public static void main (String[] args){
        String[] first_strat;
        PlayingGround baseGround =  new PlayingGround(System.currentTimeMillis());
        GUI vid = new GUI(baseGround);
       
    }
    public GUI(PlayingGround baseGround){
        
        super(baseGround);
        c = new Console(this);
        c.setVisible(true);
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
        System.out.println("Void Start ");
        state.schedule.scheduleOnce(0,0,sim.players[0]);
    
        System.out.println("Figures on the field :" + sim.field.getAllObjects().size());
            
        
    }
    @Override
    public boolean step(){

        int next_player;
        if (current_player == 3){next_player = 0;}
        else{next_player = current_player +1;}
        boolean success = true;
        if (!sim.game_over){
            success = state.schedule.step(state);

            // Überprüfe ob Game Beendet ist.
            if ((sim.players[0].placement != 0) && (sim.players[1].placement != 0) && (sim.players[2].placement != 0) && (sim.players[3].placement != 0)) {
                //sim.game_over = true;
                //TODO Daten für Statistiken auslesen
                sim.finish();
                //serializeGameResults(sim);
                state = new PlayingGround(System.currentTimeMillis());
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
}