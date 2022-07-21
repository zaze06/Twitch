package me.alien.yello.events.maze;

import java.util.Arrays;
import java.util.Random;

public class MazeGenerator {

    private char[][] grid;
    private final int WIDTH;
    private final int HEIGHT;
    Random randomGen = new Random();

    //Checks to see if any of the surrounding cells are un
    private boolean hasUnvisited (char[][] grid, int sr, int sc) {
        if (sc+2 > HEIGHT-2) {
        } else if (grid[sr][sc+2]=='#') {
            return true;
        }
        if (sc-2 < 0) {
        } else if (grid[sr][sc-2]=='#') {
            return true;
        }
        if (sr+2 > WIDTH-2) {
        } else if (grid[sr+2][sc]=='#') {
            return true;
        }
        if (sr-2 < 0) {
        } else if (grid[sr-2][sc]=='#') {
            return true;
        }
        return false;
    }

    //Visits each cell, turns it to '.'
    private void pather (char[][] grid, int sr, int sc) {
        //Sets current cell to '.' to mark as visited
        grid[sr][sc] = '.';

        //"Clipping": if it is at edge of grid, don't carve any more, just return.
        if (sr>WIDTH-2||sr<1||sc>HEIGHT-2||sc<1) {
            return;
        }

        //Gets a number between 0-3
        switch (randomGen.nextInt(4)) {
            case 0:
                if(hasUnvisited(grid,sr,sc)) {
                    if(sc+2>HEIGHT-2) {
                    }else if(grid[sr][sc+2]!='.'){
                        grid[sr][sc+1]='.';
                        pather(grid,sr,sc+2);
                    }
                    pather(grid,sr,sc);
                }
                break;
            case 1:
                if(hasUnvisited(grid,sr,sc)) {
                    if(sc-2<0) {
                    } else if(grid[sr][sc-2]!='.'){
                        grid[sr][sc-1]='.';
                        pather(grid,sr,sc-2);
                    }
                    pather(grid,sr,sc);
                }
                break;
            case 2:
                if(hasUnvisited(grid,sr,sc)) {
                    if(sr+2>WIDTH-2) {
                    }else if(grid[sr+2][sc]!='.'){
                        grid[sr+1][sc]='.';
                        pather(grid,sr+2,sc);
                    }
                    pather(grid,sr,sc);
                }
                break;

            case 3:
                if(hasUnvisited(grid,sr,sc)) {
                    if(sr-2<0) {
                    } else if(grid[sr-2][sc]!='.') {
                        grid[sr-1][sc]='.';
                        pather(grid,sr-2,sc);
                    }
                    pather(grid,sr,sc);
                }
                break;
        }
    }

//Returns a complete maze, gets the carved out paths from the pather function,
//then 'cleans it up' to return a useable maze format for the game.

    private char[][] mazer() {
        grid = new char[WIDTH][HEIGHT];
        //Initialize Grid with all walls
        for (int i=0;i<WIDTH;i++)
        {
            for (int j=0;j<HEIGHT;j++)
            {
                grid[i][j]= '#';
            }
        }
        //Starting from row and column 1 and 1, respectively.
        int sr=1,sc=1;
        //Carve Out the Grid
        pather(grid,sr,sc);

/*
    //Draw Vertical Surrounding Walls
    for (int j=0;j<HEIGHT;j++)
    {
        grid [0][j]= '#';
        grid [WIDTH-1][j]= '#';
    }
    //Draw Horizontal Surrounding Walls
    for (int j=0;j<WIDTH;j++)
    {
        grid [j][0]= '#';
        grid [j][HEIGHT-1]= '#';
    }
*/

        //JUST FOR DEBUGGING:
        /*for (int i=0;i<HEIGHT;i++)
        {
            for (int j=0;j<WIDTH;j++)
            {
                System.out.print(grid[j][i]);
            }
            System.out.println("");
        }*/
        //JUST FOR DEBUGGING ERASE IMMEDIATELY AFTER DONE WITH
        return grid;
    }

    public MazeGenerator (int wIn, int hIn) {
        WIDTH = wIn;
        HEIGHT = hIn;
        grid = mazer();

    }

    public int[][] getIntMaze(){
        int[][] out = new int[HEIGHT][WIDTH];
        for (int i=0;i<HEIGHT;i++)
        {
            for (int j=0;j<WIDTH;j++)
            {
                out[i][j] = (grid[i][j] == '#'?1:0);
            }
        }
        return out;
    }

    //After Debugging the maze:
    public static void main(String[] args) {
        MazeGenerator maze = new MazeGenerator(17,17);
        int[][] maze1 = maze.getIntMaze();
        for(int[] ints : maze1){
            System.out.println(Arrays.toString(ints)+"\n");
        }
    }
}
