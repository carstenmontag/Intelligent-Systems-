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
    public static void main (String[] args){
        GUI vid = new GUI();
    }
    public GUI(){
        super(new PlayingGround(System.currentTimeMillis()));
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
        
        
        // Apply the schedule until the game is over
       
        //schedule.step(this);
        //System.out.println("Step" + i);
        
        System.out.println("Figures on the field :" + sim.field.getAllObjects().size());
            
        
    }
    @Override
    public boolean step(){
        int next_player;
        if (current_player == 3) {next_player = 0;}
        else {next_player = current_player +1;}
        boolean success = true;
        if (!sim.game_over){
            state.schedule.scheduleOnce(state.schedule.getTime()+1,0,sim.players[next_player]);
            success = state.schedule.step(state);
            c.refresh();
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

        Image redImage = new ImageIcon("src/main/resources/RedPiece.png").getImage();
        Image blueImage = new ImageIcon("src/main/resources/BluePiece.png").getImage();
        Image yellowImage = new ImageIcon("src/main/resources/YellowPiece.png").getImage();
        Image greenImage = new ImageIcon("src/main/resources/GreenPiece.png").getImage();

        Image[] images = {greenImage, redImage, blueImage, yellowImage};

        for (int i=0; i <= board.players.length-1; i++) {
            for (int j=0; j<= board.players[i].AtStartPieces.length-1; j++) {
                boardPortrayal.setPortrayalForObject(board.players[i].AtStartPieces[j], new FacetedPortrayal2D(
                        new SimplePortrayal2D[] {
                                new ImagePortrayal2D(images[i]),
                        }
                ));
            }
        }

        display.reset();
        setBackground();
        display.repaint();
    }
    public void load(SimState state){
        super.load(state);
        setupPortrayals();
    }
    public void init(Controller c) {
        super.init(c);
        display = new Display2D(720,720,this);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Ludo Game");
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
