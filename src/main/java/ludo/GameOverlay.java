package ludo;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.FieldPortrayal2D;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class GameOverlay extends FieldPortrayal2D {

    GUI ui;
    public static final int GUTTER = 32;
    public static final int BORDER = 8;
    Font font = new Font("SansSerif", Font.BOLD, 18);
    public GameOverlay(GUI ui) { this.ui = ui;}

    int firstTimeNumG = 1;

    
    /** 
     * @param obj
     * @param graphics
     * @param info
     */
    public void draw(Object obj, Graphics2D graphics, DrawInfo2D info) {
        PlayingGround sim = (PlayingGround) (ui.state);

        graphics.setFont(font);
        graphics.setColor(Color.black);

        Rectangle2D bounds = new TextLayout("" + ui.num_of_games, font, graphics.getFontRenderContext()).getBounds();

        if (firstTimeNumG == 1) {
            firstTimeNumG = (int)((GUTTER + bounds.getHeight()) / 2);
        }

        graphics.drawString("Num Game: " + ui.num_of_games, BORDER, firstTimeNumG);
        graphics.drawString("Game in Comb: " + ui.game_in_comb, (int)((info.clip.width - BORDER * 2) * 1 / 3 + BORDER), firstTimeNumG);
        graphics.drawString("Current Comb: " + ui.current_comb, (int)((info.clip.width - BORDER * 2) * 2 / 3 + BORDER), firstTimeNumG);

    }
}
