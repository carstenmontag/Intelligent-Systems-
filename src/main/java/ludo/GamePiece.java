package ludo;

public class GamePiece{
    //Spielfigur als Objekt notwendig?
    public int ownerIndex;
    public int position, nummer;
    public boolean set = false;

    public GamePiece(int ownerIndex){
        this.ownerIndex = ownerIndex;
    }

}
