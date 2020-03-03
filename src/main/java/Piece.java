public class Piece {
    public static int value(String piece, String humanChar) {
        int absValue = 0;
        if (piece.endsWith("p"))
            absValue = 10;
        else if (piece.endsWith("r"))
            absValue = 50;
        else if (piece.endsWith("n"))
            absValue = 30;
        else if (piece.endsWith("b"))
            absValue = 30;
        else if (piece.endsWith("q"))
            absValue = 90;
        else if (piece.endsWith("k"))
            absValue = 900;

        return (piece.startsWith(humanChar)) ? absValue : -absValue;
    }
}
