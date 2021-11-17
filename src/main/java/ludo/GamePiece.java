package ludo;

public class GamePiece{
    //Spielfigur als Objekt notwendig?
    public int ownerIndex;
    public int positionx;    
    public int start;
    public int finish;
    public int PieceIndex;
    public boolean isset = false;
    public boolean hasfinished = false;

    public GamePiece(int ownerIndex, int start, int finish, int PieceIndex){
        this.ownerIndex = ownerIndex;
        this.start = start;
        this.finish = finish;
        this.PieceIndex = PieceIndex;
        this.positionx = -1;
    }

}
