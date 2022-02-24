package ludo;

/**
 * @className GameSave
 * @description Speichert in der add_run Funktion des CSVHandlers generierte Statistiken zu einem Spiel.
 *              Diese Objekte werden später für für die gesamte Strategiekombination ausgewertet und in eine CSV Datei geschrieben. 
 * 
 */
public class GameSave {
    int number_of_blocks;
    int number_of_kicks;
    int number_of_moves;
    int number_has_been_kicked;
    int placement;
    int game_duration;

    public GameSave(int number_of_blocks,int number_of_kicks,int number_of_moves,int number_has_been_kicked,int placement,int game_duration){
        this.number_of_blocks = number_of_blocks;
        this.number_of_kicks = number_of_kicks;
        this.number_of_moves = number_of_moves;
        this.number_has_been_kicked = number_has_been_kicked;
        this.placement = placement;
        this.game_duration = game_duration;
    }
}
