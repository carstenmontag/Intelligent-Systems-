package ludo;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.FieldPortrayal2D;
import java.awt.*;

/**
 * @className GameOverlay
 * @description Diese Klasse zeigt information über den Simulationstatus auf der GUI an.
 *              Das GameOverlay zeigt welcher Spieler im aktuellen Spiel welche Farbe hat,
 *              welche Strategie er verfolgt und an welchem Punkt sich die Simulation befindet.
 */
public class GameOverlay extends FieldPortrayal2D {
    GUI ui;
    Font font = new Font("SansSerif", Font.BOLD, 16);
    public GameOverlay(GUI ui) { this.ui = ui;}

    /**
     * @description Zeichnet und aktualisiert die Daten auf den Hintergrund.
     * @param obj
     * @param graphics
     * @param info
     */
    public void draw(Object obj, Graphics2D graphics, DrawInfo2D info) {
        PlayingGround sim = (PlayingGround) (ui.state);

        graphics.setFont(font);
        graphics.setColor(Color.black);

        int games_overall = GUI.games_per_comb * GUI.strat_combinations.length;

        graphics.drawString("Pl: "+sim.players[0].name, 5, 20); //Player Grün
        graphics.drawString("Pl: "+sim.players[1].name, 440, 20); //Player Rot
        graphics.drawString("Pl: "+sim.players[2].name, 5, 455); //Player Blau
        graphics.drawString("Pl: "+sim.players[3].name, 440, 455); //Player Gelb

        graphics.drawString("St: "+sim.players[0].strategy, 5, 40); //Strat Grün
        graphics.drawString("St: "+sim.players[1].strategy, 440, 40); //Strat Rot
        graphics.drawString("St: "+sim.players[2].strategy, 5, 475); //Strat Blau
        graphics.drawString("St: "+sim.players[3].strategy, 440, 475); //Strat gelb

        //Unabhänige Anzahl an Spielen
        graphics.drawString("Game Overall: "+ui.num_of_games+" / "+games_overall, 5, 750);

        //Anzahl des Spiels in der aktuellen Kombination
        graphics.drawString("Game in Comb.: "+ui.game_in_comb+" / "+GUI.games_per_comb, 280, 750);

        //Anzahl der Kombinationen
        graphics.drawString("Combination: "+ui.current_comb+" / "+ GUI.strat_combinations.length, 565,750);
    }
}
