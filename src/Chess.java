
import java.awt.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javafx.application.Application;
import javafx.event.EventTarget;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jon
 */
public class Chess extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    String player = "w";
    String opponent = "b";
    
    private Cell[][] cell = new Cell[8][8];
    boolean[][] moves = new boolean[8][8];
    Cell movingCell;
    boolean moving = false;
    
    private Label status = new Label("Your turn");

    @Override
    public void start(Stage primaryStage) {
        GridPane pane = new GridPane();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                pane.add(cell[i][j] = new Cell(i, j), j, i);
                if(j % 2 == 0 ^ i % 2 == 0){
                    cell[i][j].setStyle("-fx-background-color: #e29b3d");
                } else {
                    cell[i][j].setStyle("-fx-background-color: #f7ca8f");
                }
            }
        }
        
        setUpBoard();
        
        status.setFont(Font.font("Times New Roman", 24));
        
        Button resetButton = new Button("Play Again");
        //resetButton.setStyle("-fx-visibility: none");
        resetButton.setOnMouseClicked(e -> resetGame());
        
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(status);
        //borderPane.setTop(resetButton);
        borderPane.setAlignment(status, Pos.CENTER);
       // borderPane.setAlignment(resetButton, Pos.CENTER);
        
        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public boolean checkWin(char token){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(token == 'b'){
                    if(cell[i][j].getToken() == "wk"){
                        return false;
                    }
                } else if(token == 'w'){
                    if(cell[i][j].getToken() == "bk"){
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    String cpuChar = "b";
    String humanChar = "w";
    int positionCount = 0;
    
    private int alphaBeta(Cell[][] cells, int alpha, int beta, int depth, boolean isMax) {
        positionCount++;
        
        if(depth == 0){
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    System.out.printf("%3s", cells[i][j].getToken());
//                }
//                System.out.println("");
//            }
//            
//            System.out.println("BOARD SCORE: " + -evaluateBoard(cells));
//            System.out.println("--------------------------------------");
//            System.out.println("");
            
            return -evaluateBoard(cells);
        }
        
        if(isMax){
            ArrayList<Move> maxMoveList = getMoves(cells, cpuChar);

            int bestMove = -9999;

            for(int i = 0; i < maxMoveList.size(); i++){
                Move move = maxMoveList.get(i);
                move.makeTempMove();

                bestMove = Math.max(bestMove, alphaBeta(cells, alpha, beta, depth - 1, false));
                
                move.undoMove();

                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        } else {
           ArrayList<Move> minMoveList = getMoves(cells, humanChar);

           int bestMove = 9999;

           for(int i = 0; i < minMoveList.size(); i++){
               Move move = minMoveList.get(i);
               move.makeTempMove();

               bestMove = Math.min(bestMove, alphaBeta(cells, alpha, beta, depth - 1, true));

               move.undoMove();

               beta = Math.min(beta, bestMove);
               if (beta <= alpha) {
                   break;
               }
           }

           return bestMove;
        }
    }
    
    boolean gameOver = false;
    
    public void cpuPlay(){
        int depth = 6;
        int alpha = -10000;
        int beta = 10000;
        
        positionCount = 0;
        
        ArrayList<Move> maxMoveList = getMoves(cell, cpuChar);

        int bestMove = -9999;
        int bestInt = 0;
        
        for(int i = 0; i < maxMoveList.size(); i++){
            Move move = maxMoveList.get(i);
            move.makeTempMove();
                
            bestMove = Math.max(bestMove, alphaBeta(cell, alpha, beta, depth - 1, false));
            
            move.undoMove();

            if(bestMove > alpha){
                alpha = bestMove;
                bestInt = i;
            }
            
            if (beta <= alpha) {
                break;
            }
        }
        
        if(maxMoveList.get(bestInt).toCell.getToken().endsWith("k")){
            gameOver = true;
        }
        
        maxMoveList.get(bestInt).makeMove();

        System.out.println(positionCount);
    }
    
    public ArrayList<Move> getMoves(Cell[][] cells, String player){
        ArrayList<Move> moveList = new ArrayList<Move>();
        
        String opponent = (player == "w") ? "b" : "w";
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(cells[i][j].getToken().startsWith(player)){
                    cells[i][j].findMoves(player, opponent);
                    
                    for(int a = 0; a < 8; a++){
                        for(int b = 0; b < 8; b++){
                            if(moves[a][b]){
                                moveList.add(new Move(cells[i][j], cells[a][b]));
                            }
                        }
                    }
                    
                    clearMoves();
                }
            }
        }
        
        //Collections.sort(moveList, new SortMove());
        moveList.sort(Comparator.comparing(Move::getValue).reversed());
        
        return moveList;
    }
    
    public int evaluateBoard(Cell[][] cells){
        int total = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                total += getPieceValue(cells[i][j]);
            }
        }
            
        return total;
    }
    
    public int getPieceValue(Cell cell){
        int absValue = 0;
        if(cell.getToken().endsWith("p"))
            absValue = 10;
        else if(cell.getToken().endsWith("r"))
            absValue = 50;
        else if(cell.getToken().endsWith("n"))
            absValue = 30;
        else if(cell.getToken().endsWith("b"))
            absValue = 30;
        else if(cell.getToken().endsWith("q"))
            absValue = 90;
        else if(cell.getToken().endsWith("k"))
            absValue = 900;
        
        return (cell.getToken().startsWith(humanChar)) ? absValue : -absValue;
    }

    private void resetGame() {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                cell[i][j].setToken("");
                setUpBoard();
                player = "w";
            }
        }
    }

    private void setUpBoard() {
        cell[0][0].setToken("br");
        cell[0][1].setToken("bn");
        cell[0][2].setToken("bb");
        cell[0][3].setToken("bq");
        cell[0][4].setToken("bk");
        cell[0][5].setToken("bb");
        cell[0][6].setToken("bn");
        cell[0][7].setToken("br");
        
        cell[7][0].setToken("wr");
        cell[7][1].setToken("wn");
        cell[7][2].setToken("wb");
        cell[7][3].setToken("wq");
        cell[7][4].setToken("wk");
        cell[7][5].setToken("wb");
        cell[7][6].setToken("wn");
        cell[7][7].setToken("wr");
        
        for(int i = 0; i < 8; i++){
            cell[1][i].setToken("bp");
            cell[6][i].setToken("wp");
        }
    }
    
    public void clearMoves(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                moves[i][j] = false;
                
                if(j % 2 == 0 ^ i % 2 == 0){
                    cell[i][j].setStyle("-fx-background-color: #e29b3d");
                } else {
                    cell[i][j].setStyle("-fx-background-color: #f7ca8f");
                }
            }
        }
    }
    
    public class SortMove implements Comparator<Move> {
        @Override
        public int compare(Move o1, Move o2) {
            return (o1.value > o2.value) ? o1.value : o2.value;
        }
    }
    
    public class Move {
        public Cell fromCell;
        public Cell toCell;
        
        private String tempCell;
        public int value = 0;
        
        public Move(Cell fromCell, Cell toCell){
            this.fromCell = fromCell;
            this.toCell = toCell;
            
            value = getPieceValue(toCell);
        }
        
        public void makeMove(){
            tempCell = toCell.getToken();
            toCell.setToken(fromCell.getToken());
            fromCell.setToken("");
        }
        
        public void undoMove(){
            fromCell.setTokenText(toCell.getToken());
            toCell.setTokenText(tempCell);
        }

        private void makeTempMove() {
            tempCell = toCell.getToken();
            toCell.setTokenText(fromCell.getToken());
            fromCell.setTokenText("");
        }
        
        public int getValue(){
            return value;
        }
        
    }
    
    public class Cell extends Pane{
        private String token = "";
        int i;
        int j;

        public Cell(int i, int j){
            setStyle("-fx-border-color: black");
            this.setPrefSize(2000, 2000);
            
            this.setOnMouseClicked(e -> handleMouseClick());
            this.setOnMouseMoved(e -> mouseReleased());
            
            this.i = i;
            this.j = j;
        }
        
        public int getI(){
            return i;
        }
        
        public int getJ(){
            return j;
        }

        public String getToken(){
            return token;
        }
        
        public void setToken (String c){
            token = c;
            
            this.getChildren().clear();
//            Label text = new Label(token);
//            this.getChildren().add(text);

            if(token != ""){
                this.getChildren().clear();
                
                ImageView image = new ImageView(token + ".png");
                image.setFitWidth(75);
                image.setFitHeight(70);

                this.getChildren().add(image);
            } else {
                this.getChildren().clear();
            }
        }
        
        public void setTokenText(String c){
            token = c;
        }

        private void handleMouseClick(){
            if(player == "w"){
                if(moving){
                    if(moves[i][j]){
                        boolean gameOver = false;
                        
                        if(cell[i][j].getToken().startsWith(opponent)){
                            if(cell[i][j].getToken().endsWith("k")){
                                if(player == "w"){
                                    status.setText("GAME OVER! White wins");
                                } else if(player == "b"){
                                    status.setText("GAME OVER! Black wins");
                                }

                                player = "";
                                opponent = "";
                                gameOver = true;
                            }
                        }

                        
                            //Move Player
                            cell[i][j].setToken("");
                            cell[i][j].setToken(movingCell.getToken());
                            movingCell.setToken("");
                            movingCell = null;
                            moving = false;
                            clearMoves();
                            
                        if(!gameOver){
                            //Switch turn
                            player = (player == "w") ? "b" : "w";
                            opponent = (opponent == "w") ? "b" : "w";

                            status.setText("CPU is thinking...");

//                            if(player == "w"){
//                                status.setText("White's turn");
//                            } else if (player == "b"){
//                                status.setText("Black's turn");
//                            }
                        }
                    } else if(cell[i][j].getToken().contains(player)){
                        clearMoves();
                        findMoves(player, opponent);
                    }
                } else {
                    findMoves(player, opponent);
                    moving = true;
                }
            }
        }
        
        
        private void mouseReleased() {
            if(player == "b"){
                cpuPlay();
                
                if(gameOver){
                    status.setText("GAME OVER! Black wins");
                    player = "";
                } else {

                player = (player == "w") ? "b" : "w";
                opponent = (opponent == "w") ? "b" : "w";
                
                status.setText("Your turn");
                }
            }
        }
        
        public void findMoves(String player, String opponent){
            //Pawn moves
            if(token.contentEquals(player + "p")){
                if(player == "w"){
                    if(i == 6){
                        if(!cell[i - 2][j].getToken().startsWith(player) && !cell[i - 2][j].getToken().startsWith(opponent)){
                            moves[i - 2][j] = true;
                        }
                    }
                } else if (player == "b"){
                    if(i == 1){
                        if(!cell[i + 2][j].getToken().startsWith(player) && !cell[i + 2][j].getToken().startsWith(opponent)){
                            moves[i + 2][j] = true;
                        }
                    }
                }
                
                int m = 0;
                
                if(player == "w"){
                    m = 1;
                } else if (player == "b"){
                    m = -1;
                }
                
                if(i - 1 >= 0 && i + 1 < 8){
                    if(!cell[i - 1 * m][j].getToken().startsWith(player) && !cell[i - 1 * m][j].getToken().startsWith(opponent)){
                        moves[i - 1 * m][j] = true;
                    }
                }
                
                if(i - 1 >= 0 && i + 1 < 8 && j - 1 >= 0){
                    if(cell[i - 1 * m][j - 1].getToken().startsWith(opponent)){
                        moves[i - 1 * m][j - 1] = true;
                    }
                }
                
                if(i - 1 >= 0 && i + 1 < 8 && j + 1 < 8){
                    if(cell[i - 1 * m][j + 1].getToken().startsWith(opponent)){
                        moves[i - 1 * m][j + 1] = true;
                    }
                }
            }
            
            //Knight moves
            if(token.contentEquals(player + "n")){
                //Vertical moves
                if(i - 2 >= 0 && j - 1 >= 0){
                    if(!cell[i - 2][j - 1].getToken().startsWith(player)){
                        moves[i - 2][j - 1] = true;
                    }
                }
                
                if(i - 2 >= 0 && j + 1 < 8){
                    if(!cell[i - 2][j + 1].getToken().startsWith(player)){
                        moves[i - 2][j + 1] = true;
                    }
                }
                
                if(i + 2 < 8 && j - 1 >= 0){
                    if(!cell[i + 2][j - 1].getToken().startsWith(player)){
                        moves[i + 2][j - 1] = true;
                    }
                }
                
                if(i + 2 < 8 && j + 1 < 8){
                    if(!cell[i + 2][j + 1].getToken().startsWith(player)){
                        moves[i + 2][j + 1] = true;
                    }
                }
                
                //Horizontal moves
                if(i - 1 >= 0 && j - 2 >= 0){
                    if(!cell[i - 1][j - 2].getToken().startsWith(player)){
                        moves[i - 1][j - 2] = true;
                    }
                }
                
                if(i - 1 >= 0 && j + 2 < 8){
                    if(!cell[i - 1][j + 2].getToken().startsWith(player)){
                        moves[i - 1][j + 2] = true;
                    }
                }
                
                if(i + 1 < 8 && j - 2 >= 0){
                    if(!cell[i + 1][j - 2].getToken().startsWith(player)){
                        moves[i + 1][j - 2] = true;
                    }
                }
                
                if(i + 1 < 8 && j + 2 < 8){
                    if(!cell[i + 1][j + 2].getToken().startsWith(player)){
                        moves[i + 1][j + 2] = true;
                    }
                }
            }
            
            //Rook and Queen moves
            if(token.contentEquals(player + "r") || token.contentEquals(player + "q")){
                //Vertical moves
                for(int a = i + 1; a < 8; a++){
                    if(!cell[a][j].getToken().startsWith(player)){
                        moves[a][j] = true;
                        
                        if(cell[a][j].getToken().startsWith(opponent)){
                            break;
                        }
                    } else {
                        break;
                    }
                }
                
                for(int a = i - 1; a >= 0; a--){
                    if(!cell[a][j].getToken().startsWith(player)){
                        moves[a][j] = true;
                        
                        if(cell[a][j].getToken().startsWith(opponent)){
                            break;
                        }
                    } else {
                        break;
                    }
                }
                
                //Horizontal moves
                for(int a = j + 1; a < 8; a++){
                    if(!cell[i][a].getToken().startsWith(player)){
                        moves[i][a] = true;
                        
                        if(cell[i][a].getToken().startsWith(opponent)){
                            break;
                        }
                    } else {
                        break;
                    }
                }
                
                for(int a = j - 1; a >= 0; a--){
                    if(!cell[i][a].getToken().startsWith(player)){
                        moves[i][a] = true;
                        
                        if(cell[i][a].getToken().startsWith(opponent)){
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            
            //Biship and Queen moves
            if(token.contentEquals(player + "b") || token.contentEquals(player + "q")){
                int b;
                
                //Up slope moves
                b = j + 1;
                for(int a = i + 1; a < 8; a++){
                    if(b < 8){
                        if(!cell[a][b].getToken().startsWith(player)){
                            moves[a][b] = true;
                            
                            if(cell[a][b].getToken().startsWith(opponent)){
                                break;
                            }
                        } else {
                            break;
                        }
                        
                        b++;
                    }
                }
                
                b = j - 1;
                for(int a = i - 1; a >= 0; a--){
                    if(b >= 0){
                        if(!cell[a][b].getToken().startsWith(player)){
                            moves[a][b] = true;
                            
                            if(cell[a][b].getToken().startsWith(opponent)){
                                break;
                            }
                        } else {
                            break;
                        }
                        
                        b--;
                    }
                }
                
                //Down slope moves
                b = j - 1;
                for(int a = i + 1; a < 8; a++){
                    if(b >= 0){
                        if(!cell[a][b].getToken().startsWith(player)){
                            moves[a][b] = true;
                            
                            if(cell[a][b].getToken().startsWith(opponent)){
                                break;
                            }
                        } else {
                            break;
                        }
                        
                        b--;
                    }
                }
                
                b = j + 1;
                for(int a = i - 1; a >= 0; a--){
                    if(b < 8){
                        if(!cell[a][b].getToken().startsWith(player)){
                            moves[a][b] = true;
                            
                            if(cell[a][b].getToken().startsWith(opponent)){
                                break;
                            }
                        } else {
                            break;
                        }
                        
                        b++;
                    }
                }
            }
            
            if(token.contentEquals(player + "k")){
                if(i - 1 >= 0){
                    if(!cell[i - 1][j].getToken().startsWith(player)){
                        moves[i - 1][j] = true;
                    }
                }
                
                if(i - 1 >= 0 && j - 1 >= 0){
                    if(!cell[i - 1][j - 1].getToken().startsWith(player)){
                        moves[i - 1][j - 1] = true;
                    }
                }
                
                if(i + 1 < 8){
                    if(!cell[i + 1][j].getToken().startsWith(player)){
                        moves[i + 1][j] = true;
                    }
                }
                
                if(i + 1 < 8 && j + 1 < 8){
                    if(!cell[i + 1][j + 1].getToken().startsWith(player)){
                        moves[i + 1][j + 1] = true;
                    }
                }
                
                if(i + 1 < 8 && j - 1 >= 0){
                    if(!cell[i + 1][j - 1].getToken().startsWith(player)){
                        moves[i + 1][j - 1] = true;
                    }
                }
                
                if(i - 1 >= 0 && j + 1 < 8){
                    if(!cell[i - 1][j + 1].getToken().startsWith(player)){
                        moves[i - 1][j + 1] = true;
                    }
                }
                
                if(j + 1 < 8){
                    if(!cell[i][j + 1].getToken().startsWith(player)){
                        moves[i][j + 1] = true;
                    }
                }
                
                if(j - 1 >= 0){
                    if(!cell[i][j - 1].getToken().startsWith(player)){
                        moves[i][j - 1] = true;
                    }
                }
            }
            
            //Fill in moves with gray area
            if(player == player){
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        if(moves[i][j]){
                            cell[i][j].setStyle("-fx-background-color: #999999");
                        }
                    }
                }

                movingCell = this;
            }
        }
    }
}
