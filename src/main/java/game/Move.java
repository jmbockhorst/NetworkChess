package game;

import java.util.ArrayList;
import java.util.List;

public class Move {
    public Cell fromCell;
    public Cell toCell;
    public List<Cell> attackedCells;

    private String tempCell;
    private List<String> tempAttackedCells;
    private int value;

    private MoveHandlerFunction moveHandlerFunction;

    public Move(Cell fromCell, Cell toCell, int value) {
        this.fromCell = fromCell;
        this.toCell = toCell;
        this.value = value;
        this.attackedCells = new ArrayList<>();
        this.tempAttackedCells = new ArrayList<>();
    }

    public Move(Cell fromCell, Cell toCell, List<Cell> attackedCells, int value) {
        this.fromCell = fromCell;
        this.toCell = toCell;
        this.attackedCells = attackedCells;
        this.value = value;
        this.tempAttackedCells = new ArrayList<>();
    }

    public void makeMove() {
        tempCell = toCell.getToken();
        toCell.setToken(fromCell.getToken());
        fromCell.setToken("");

        tempAttackedCells.clear();
        attackedCells.forEach(cell -> {
            tempAttackedCells.add(cell.getToken());
            cell.setToken("");
        });

        if (moveHandlerFunction != null) {
            moveHandlerFunction.call(toCell);
        }
    }

    public void undoMove() {
        fromCell.setToken(toCell.getToken());
        toCell.setToken(tempCell);

        for (int i = 0; i < attackedCells.size(); i++) {
            attackedCells.get(i).setToken(tempAttackedCells.get(i));
        }
    }

    public int getValue() {
        return value;
    }

    public List<Cell> getAttackedCells() {
        return attackedCells;
    }

    public void setMoveHandlerFunction(MoveHandlerFunction moveHandlerFunction) {
        this.moveHandlerFunction = moveHandlerFunction;
    }

    @Override
    public String toString() {
        return "Move [fromCell=" + fromCell + ", toCell=" + toCell + ", value=" + value + ", attackedCells="
                + attackedCells + "]";
    }
}