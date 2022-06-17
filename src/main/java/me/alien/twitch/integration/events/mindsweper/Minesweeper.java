package me.alien.twitch.integration.events.mindsweper;

import me.alien.twitch.integration.events.Event;
import me.alien.twitch.integration.events.PrintHandler;
import me.alien.twitch.integration.util.Vector2I;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Minesweeper implements Event {

    public static void main(String[] args) throws IOException {
        Minesweeper minesweeper = new Minesweeper(System.out::println);
        Thread t = new Thread(minesweeper::run);
        t.start();
        minesweeper.addData("r,0,0");
        while(true){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            minesweeper.addData(in.readLine());
        }
    }

    PrintHandler out;
    final ArrayList<Object> data = new ArrayList<>();
    ArrayList<Tile> board = new ArrayList<>();
    int size;
    int bombs;
    boolean spawnedBombs = false;

    public Minesweeper(PrintHandler out){
        int size = 8;//(int) (Math.random()*(30-4)+4);
        int bombs = 10;//(int) (Math.random()*(Math.pow(size, 2)-5)+3);
        int trys = 0;
        while(bombs < 0 && trys < 20){
            size = (int) (Math.random()*(16-4)+4);
            bombs = (int) (Math.random()*(Math.pow(size, 2)-5)+3);
            trys++;
        }
        if(trys > 19){
            size = 16;
            bombs = 40;
        }
        out.print("You will be playing a game of minesweeper");
        out.print("The objective is to place a marker on all the bombs if you place a marker normal time the game wont be won");
        out.print("If you revile a bomb you will lose");
        out.print("The number on a tile tell you how many bombs ar next to it. if it dosen't have a number there arent any bombs");
        out.print("Example action F, 1, 1 will place a Flag/Marker at position 1,1 the F can be a M for the same action but replace whit a R to revile the tile");
        out.print("Map size is: "+size+" by "+size);
        out.print("You have "+markedTiles()+"/"+bombs+" marked(a marked tile dosen't mean its a bomb tile)");
        this.out = out;
        this.size = size;
        this.bombs = bombs;
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                board.add(new Tile(new Vector2I(x,y)));
            }
        }
        //board.forEach(t -> t.setShowen(true));
    }

    @Override
    public boolean run() {
        while(true){
            try {
                print(false);

                if (data.isEmpty()) {
                    synchronized (data) {
                        data.wait();
                    }
                }

                Action action = null;
                for(Object obj : this.data){
                    if(obj instanceof String str){
                        if(str.equalsIgnoreCase("end")) return false;
                        this.data.remove(obj);
                        if((action = parse(str)) != null) break;
                    }else{
                        this.data.remove(obj);
                    }
                }

                if(action == null) continue;

                if(!spawnedBombs){
                    int placedBombs = 0;
                    long time = System.currentTimeMillis();
                    while(placedBombs <= bombs) {
                        /*for(Tile t : board){
                            if(placedBombs > bombs) break;
                            if(t.getPos().equals(action.getPos())) continue;
                            if(Math.random() >= 0.5 && !t.isBomb()){
                                t.setBomb(true);
                                placedBombs++;

                            }
                        }*/
                        int x = (int) (Math.random()*size);
                        int y = (int) (Math.random()*size);
                        Vector2I pos = new Vector2I(x,y);
                        Tile tile = board.stream().filter(t -> t.getPos().equals(pos)).toList().get(0);
                        if(!tile.isBomb() && !action.getPos().equals(pos)){
                            tile.setBomb(true);
                            placedBombs++;
                        }
                    }
                    out.print("Took: "+(System.currentTimeMillis()-time)+"ms to place bombs");
                    time = System.currentTimeMillis();
                    for (int x = 0; x < size; x++) {
                        for (int y = 0; y < size; y++) {
                            Vector2I pos = new Vector2I(x, y);
                            Tile tile = board.stream().filter(t -> t.getPos().equals(pos)).toList().get(0);
                            if(tile.isBomb()) continue;
                            int bombs = 0;
                            for(int x1 = -1; x1 <= 1; x1++){
                                for(int y1 = -1; y1 <= 1; y1++) {
                                    Vector2I pos1 = new Vector2I(x+x1,y+y1);
                                    if(pos.equals(pos1)) continue;
                                    List<Tile> tileList = board.stream().filter(t -> t.getPos().equals(pos1)).toList();
                                    if(tileList.isEmpty()) continue;
                                    Tile tile1 = tileList.get(0);
                                    if(tile1 == null) continue;
                                    if(tile1.isBomb()) bombs++;
                                }
                            }
                            tile.setNeberingBombs(bombs);
                        }
                    }
                    out.print("Took: "+(System.currentTimeMillis()-time)+"ms to set nebering bombs number");
                    print(true);
                    out.print("\n");
                    spawnedBombs = true;
                }

                Action finalAction = action;
                Tile tile = board.stream().filter(t -> t.getPos().equals(finalAction.getPos())).toList().get(0);
                if(action.isPlacingFlag() && tile.isMarked()) {}
                else if(action.isPlacingFlag() && !tile.isMarked() && !tile.isShowen()) tile.setMarked(true);
                else if(!action.isPlacingFlag() && tile.isBomb() && !tile.isMarked()) {
                    out.print("you lost!");
                    board.forEach(t -> {
                        if(t.isBomb()) t.setShowen(true);
                    });
                    print(false);
                    return false;
                }
                else if(!action.isPlacingFlag() && !tile.isShowen() && !tile.isMarked()){
                    if(tile.getNeberingBombs() == 0){
                        checkTile(tile.getPos().getX(),tile.getPos().getY());
                    }else{
                        tile.setShowen(true);
                    }
                }
                out.print("You have "+markedTiles()+"/"+bombs+" marked(a marked tile dosen't mean its a bomb tile)");
            }catch (Exception ignored){

            }
        }
    }

    private Tile getTile(int x, int y){
        Vector2I pos = new Vector2I(x,y);
        List<Tile> tileList = board.stream().filter(t -> t.getPos().equals(pos)).toList();
        if(tileList.isEmpty()) return null;
        return tileList.get(0);
    }

    private boolean checkTile(int x, int y){
        Tile tile = getTile(x,y);
        if(tile == null) return false;
        if(!tile.isShowen()&&!tile.isBomb()&&!tile.isMarked()){
            tile.setShowen(true);
            for(int x1 = x-1; x1 <= 1; x1++){
                for(int y1 = y-1; y1 <= 1; y1++){
                    checkTile(x1, y1);
                }
            }
        }
        return false;
    }

    private void print(boolean overWriteShow) {
        StringBuilder builder = new StringBuilder("\n");

        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                Vector2I pos = new Vector2I(x,y);
                Tile tile = board.stream().filter(t -> t.getPos().equals(pos)).toList().get(0);
                if(tile.isMarked()) builder.append("P");
                else if(tile.isBomb() && (tile.isShowen() || overWriteShow) && !tile.isExploded()) builder.append("X");
                else if(tile.isBomb() && tile.isExploded()) builder.append("#");
                else if((tile.isShowen() || overWriteShow)) {
                    int neberingBombs = tile.getNeberingBombs();
                    builder.append(neberingBombs == 0?"-":neberingBombs);
                }
                else builder.append(" ");
            }
            builder.append("\n");
        }
        out.print(builder.toString());
    }

    public Action parse(String str){
        char[] chars = str.toCharArray();
        StringBuilder xStr = new StringBuilder();
        StringBuilder yStr = new StringBuilder();
        boolean placingFlag = false;
        int commas = 0;

        for(char c : chars){
            if(c != ',' && !Character.isDigit(c) && commas == 0){
                c = Character.toUpperCase(c);
                if(c == 'F' || c == 'M') placingFlag = true;
            } else if (Character.isDigit(c)) {
                if(commas == 1){
                    xStr.append(c);
                } else {
                    yStr.append(c);
                }
            }else if (c == ','){
                commas++;
            }
        }

        return new Action(placingFlag, new Vector2I(Integer.parseInt(xStr.toString()), Integer.parseInt(yStr.toString())));
    }

    public int markedTiles(){
        if(board.isEmpty()) return 0;
        AtomicInteger marked = new AtomicInteger();
        board.forEach(tile -> {if(tile.isMarked())marked.incrementAndGet();});
        return marked.get();
    }

    @Override
    public void addData(Object data) {
        synchronized (this.data) {
            this.data.add(data);
            this.data.notifyAll();
        }
    }

    @Override
    public void removeData(Object data) {
        synchronized (this.data) {
            this.data.remove(data);
        }
    }

    @Override
    public void removeData(int location) {
        synchronized (this.data) {
            this.data.remove(location);
        }
    }

    @Override
    public Object getData(int location) {
        return data.get(location);
    }

    @Override
    public void end() {
        addData("end");
    }
}
