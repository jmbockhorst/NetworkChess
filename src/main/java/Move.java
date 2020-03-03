public class Move {
    public Cell fromCell;
    public Cell toCell;

    private String tempCell;
    private int value = 0;

    public Move(Cell fromCell, Cell toCell) {
        this.fromCell = fromCell;
        this.toCell = toCell;

        value = toCell.getValue();
    }

    public void makeMove() {
        tempCell = toCell.getToken();
        toCell.setToken(fromCell.getToken());
        fromCell.setToken("");
    }

    public void undoMove() {
        fromCell.setToken(toCell.getToken());
        toCell.setToken(tempCell);
    }

    public int getValue() {
        return value;
    }
}