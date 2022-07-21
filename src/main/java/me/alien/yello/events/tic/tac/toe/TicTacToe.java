package me.alien.yello.events.tic.tac.toe;

import me.alien.yello.events.PrintHandler;
import me.alien.yello.events.Event;
import me.alien.yello.util.Vector2I;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TicTacToe /*extends Thread*/ implements Event {
    /**
     * Testing purposes only!
     */
    public static void main(String[] args) throws IOException {
        TicTacToe main = new TicTacToe(System.out::println);
        main.print();
        Thread t = new Thread(main::run);
        t.start();
        //main.start();
        while(true){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            main.addData(in.readLine());
        }
    }

    final ArrayList<Object> data = new ArrayList<>();
    Board board = new Board();
    PrintHandler out;

    public TicTacToe(PrintHandler out) {
        out.print("You will be player a game of Tic Tac Toe(3 in a row)");
        out.print("The rules ar simple, you and the computer take turns and place 1 marker");
        out.print("First to 3 in a row wins. a tie is counted as a lost for you");
        out.print("Note: all chat messages will not be sent to public chat while you ar playing,");
        out.print("to place a marker 1,1 is center 0,0 is top left, 2,2 is bottom right, so you type row,col");
        this.out = out;
    }

    @Override
    public boolean run() {
        while(true){
            try {

                print();

                if(board.isGameOver()){
                    break;
                }

                if (data.isEmpty()) {
                    synchronized (data){
                        data.wait();
                    }
                }
                Vector2I pos = null;
                for(Object obj : data){
                    if(obj instanceof String str){
                        if(str.equalsIgnoreCase("end")) return false;
                        if((pos = parse(str)) != null){
                            data.remove(obj);
                            break;
                        }
                    }
                    data.remove(obj);
                }
                if(pos == null) continue;
                if(!board.placeMark(pos.getX(), pos.getY())){
                    out.print("Failed to place marker");
                    continue;
                }

                int[] move = MiniMax.getBestMove(board);
                int col = move[1];
                int row = move[0];
                board.placeMark(row, col);
            }catch (Exception e){
               //throw  new RuntimeException(e);
            }
        }
        out.print("\nYou "+(board.getWinningMark() == Mark.O?"won":"lost"));
        return board.getWinningMark() == Mark.O;
        //System.exit(0);
    }

    private void print() {
        StringBuilder builder = new StringBuilder("\n");

        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                builder.append(board.getMarkAt(y,x).getMark());
            }
            builder.append("\n");
        }
        out.print(builder.toString());
    }

    private Vector2I parse(String str) {
        char[] chars = str.toCharArray();
        StringBuilder xStr = new StringBuilder();
        StringBuilder yStr = new StringBuilder();
        boolean comma = false;
        for(char c : chars){
           if(Character.isDigit(c)){
                if(!comma){
                    xStr.append(c);
                }else{
                    yStr.append(c);
                }
            }else{
                if(c == '.'){
                    if(!comma){
                        xStr.append(c);
                    }else{
                        yStr.append(c);
                    }
                } else if (c == ',') {
                    if(!comma){
                        comma = true;
                    }else{
                        return null;
                    }
                } else if (c == ' ') {

                } else{
                    return null;
                }
            }
        }
        Vector2I pos = null;
        try{
            int x = (int) Math.floor(Float.parseFloat(xStr.toString()));
            int y = (int) Math.floor(Float.parseFloat(yStr.toString()));
            pos = new Vector2I(x,y);
        }catch (Exception e){
            return null;
        }
        return pos;
    }


    //@Override
    public void addData(Object data) {
        synchronized (this.data) {
            this.data.add(data);
            this.data.notifyAll();
        }
    }

    //@Override
    public void removeData(Object data) {
        synchronized (this.data) {
            this.data.remove(data);
        }
    }

    //@Override
    public void removeData(int location) {
        synchronized (this.data) {
            this.data.remove(location);
        }
    }

    //@Override
    public Object getData(int location) {
        return data.get(location);
    }

    @Override
    public void end() {
        addData("end");
    }
}
