package me.namcap.game;

import java.awt.image.BufferedImage;

public class Map {
    
    private DataToObject[][] map;
    
    public Map(BufferedImage img) {
    
        int[][][] mapData = Util.getColorData(img);
        map = new DataToObject[mapData.length][mapData[0].length];
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                int[] colour = mapData[x][y];
                map[x][y] = DataToObject.getObject(colour[0], colour[1], colour[2], colour.length > 3 ? colour[3] : -1);
            }
        }
    }
    
    public DataToObject getBlock(int x, int y) throws ArrayIndexOutOfBoundsException {
        return map[x][y];
    }
    
    public DataToObject[][] getMap() {
        
        return map;
    }
}
