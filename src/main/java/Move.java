public class Move {
    private final Chess chess;
    public Cell fromCell;
    public Cell toCell;

    private String tempCell;
    public int value = 0;

    public Move(Chess chess, Cell fromCell, Cell toCell) {
        this.chess = chess;
        this.fromCell = fromCell;
        this.toCell = toCell;

        value = this.chess.getPieceValue(toCell);
    }

    public void makeMove() {
        tempCell = toCell.getToken();
        toCell.setToken(fromCell.getToken());
        fromCell.setToken("");
    }

    public void undoMove() {
        fromCell.setTokenText(toCell.getToken());
        toCell.setTokenText(tempCell);
    }

    void makeTempMove() {
        tempCell = toCell.getToken();
        toCell.setTokenText(fromCell.getToken());
        fromCell.setTokenText("");
    }

    public int getValue() {
        return value;
    }

}