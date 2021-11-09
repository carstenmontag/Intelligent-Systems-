package ludo;

public class GamePiece{
    //Spielfigur als Objekt notwendig?
    public int ownerIndex;
    public int positionx;    
    public int start;
    public int finish;
    public boolean isset = false;
    public boolean hasfinished = false;

    public GamePiece(int ownerIndex, int start, int finish){
        this.ownerIndex = ownerIndex;
        this.start = start;
        this.finish = finish;
    }

}
