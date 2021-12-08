package ludo;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

public class GUI extends GUIState {
    //Benutzerkonsole
    public Display2D display;
    public JFrame displayFrame;
    public PlayingGround sim;
    SparseGridPortrayal2D boardPortrayal = new SparseGridPortrayal2D();
    public int num_players = 4;
    public int steps_per_sim = 3;
    public int sims = 1;
    public static void main (String[] args){
        PlayingGround sim = new PlayingGround(System.currentTimeMillis());
        GUI vid = new GUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

    public GUI(){
        super(new PlayingGround(System.currentTimeMillis()));
        System.out.println("GUI Construct");
        
    }
    public GUI(SimState state){
        super(state);
    }
    public static String getName(){
        return "Ludo";
    }

    public void start() {
        super.start();
        setupPortrayals();
        sim = (PlayingGround) state;
        
        // Queue the Agents in a repeating schedule
        System.out.println("Void Start ");
        
            
        for(int i=0; i<sim.numPlayers; i++){
            state.schedule.scheduleRepeating(sim.players[i],i, 1.0);
        }
        
        // Apply the schedule until the game is over
       
        //schedule.step(this);
        //System.out.println("Step" + i);
        
        System.out.println("Figures on the field :" + sim.field.getAllObjects().size());
            
        
    }
    @Override
    public boolean step(){
        boolean success = true;
        if (!sim.game_over){
             success = state.schedule.step(state);
             setupPortrayals();
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
