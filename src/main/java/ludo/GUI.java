package ludo;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import java.awt.*;
import javax.swing.*;
import sim.portrayal.simple.OvalPortrayal2D;

public class GUI extends GUIState {
    //Benutzerkonsole

    public GUI(){
        super(new PlayingGround(System.currentTimeMillis()));
    }

    public GUI(SimState state){
        super(state);   
    }

    public static String getName(){
        return "Ludo";
    }

    public static void main (String[] args){
        GUI vid = new GUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }
}
