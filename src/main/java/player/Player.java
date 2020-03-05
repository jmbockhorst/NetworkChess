package player;

public class Player {
    private PlayerType type;
    private String character;

    public Player(PlayerType type, String character) {
        this.type = type;
        this.character = character;
    }

    public PlayerType getType() {
        return type;
    }

    public String getCharacter() {
        return character;
    }
}
