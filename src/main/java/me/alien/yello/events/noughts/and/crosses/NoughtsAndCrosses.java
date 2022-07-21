package me.alien.yello.events.noughts.and.crosses;

import me.alien.yello.events.Event;
import me.alien.yello.events.PrintHandler;
import me.alien.yello.util.Vector2I;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NoughtsAndCrosses implements Event {
    PrintHandler out;
    final ArrayList<Object> data = new ArrayList<>();
    ArrayList<Tile> map = new ArrayList<>();

    public NoughtsAndCrosses(PrintHandler out) {
        this.out = out;
        out.print("You will be playing a game of noughts and crosses");
        out.print("You will be playing on a 20x20 board");
        out.print("Good lock!");

    }

    public static void main(String[] args) throws IOException {
        NoughtsAndCrosses main = new NoughtsAndCrosses(System.out::println);
        Thread t = new Thread(main::run);
        t.start();
        //main.start();
        while(true){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            main.addData(in.readLine());
        }
    }

    @Override
    public boolean run() {
        while(true){
            try {
                print();

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
                if(!pos.isInSide(0,0,20,20)) continue;

                Tile tile = getTile(pos);
                if(tile == null){
                    map.add(new Tile(pos, 1));
                }else if(tile.getNum() == 0){
                    tile.place(1);
                }else{
                    // Invalid move
                }



                int win = testWin();
                if(win == 2){
                    return false;
                }else if(win == 1){
                    return true;
                }

            }catch (Exception e){

            }
        }
    }

    private int testWin() {
        int winId = 0;
        boolean finished = false;
        for (Tile checkTile : map) {
            int id1 = checkTile.getNum();
            Vector2I pos = checkTile.getPos();
            for (int xDir = -1; xDir <= 1; xDir++) {
                for (int yDir = -1; yDir <= 1; yDir++) {
                    try {
                        if (yDir == 0 && xDir == 0) continue;
                        int y2 = pos.getY() + yDir;
                        int x2 = pos.getX() + xDir;
                        Tile t = getTile(x2, y2);
                        if(t == null){
                            continue;
                        }
                        int stack = 1;
                        while (t.getNum() == id1 && stack < 5) {
                            x2 += xDir;
                            y2 += yDir;
                            t = getTile(x2, y2);
                            if(t == null){
                                break;
                            }
                            stack++;
                        }
                        if (stack == 5) {
                            finished = true;
                            winId = id1;
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {

                    }
                }
                if (finished) {
                    break;
                }
            }
            if (finished) {
                break;
            }
        }
        return winId;
    }

    private void print() {

    }

    private Tile getTile(int x, int y){
        return getTile(new Vector2I(x,y));
    }

    private Tile getTile(Vector2I pos){
        List<Tile> tileList = map.stream().filter(t -> t.getPos().equals(pos)).toList();
        if(tileList.isEmpty()) return null;
        return tileList.get(0);
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
