package me.namcap.game;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Util {
    
    public static int[][][] getColorData(BufferedImage image) {
    
        boolean hasAlpha = image.getAlphaRaster() != null;
        int width = hasAlpha ? 4 : 3;
        int[][][] out = new int[image.getWidth()][image.getHeight()][width];
    
    
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int i = 0;
    
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                for(int j = 1; j <= width; j++) {
                    out[x][y][width-j] = pixels[i++] & 0xff;
                }
            }
        }
        
        return out;
    }
    
}
