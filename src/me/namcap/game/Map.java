package me.namcap.game;

import java.awt.image.BufferedImage;

import me.namcap.util.Util;

public class Map {
    
    private DataToObject[][] map;
    private int width, height;
    
    public Map(BufferedImage img) {
    
        int[][][] mapData = Util.getColorData(img);
        map = new DataToObject[mapData.length][mapData[0].length];
        this.width = map.length;
        this.height = map[0].length;
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int[] colour = mapData[x][y];
                map[x][y] = DataToObject.getObject(colour[0], colour[1], colour[2], colour.length > 3 ? colour[3] : -1);
            }
        }
    }
    
    public int getHeight() {
        
        return height;
    }
    
    public int getWidth() {
        
        return width;
    }
    
    public DataToObject getBlock(int x, int y) throws ArrayIndexOutOfBoundsException {
        return map[x][y];
    }
    
    public DataToObject[][] getMap() {
        
        return map;
    }
}
