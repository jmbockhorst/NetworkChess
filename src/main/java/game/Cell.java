package game;

public class Cell implements Comparable<Cell> {
    private String token;
    private int i;
    private int j;

    public Cell() {

    }

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
        this.token = "";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public String toString() {
        return "Cell{" + "token='" + token + '\'' + ", i=" + i + ", j=" + j + '}';
    }

    @Override
    public int compareTo(Cell o) {
        int first = getI() - o.getI();

        if (first == 0) {
            return getJ() - o.getJ();
        }

        return first;
    }
}
