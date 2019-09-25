package me.namcap.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import me.namcap.main.Config;

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
    
    
    public static void translate(BufferedImage... images) {
        
        Color wallColor = Config.wall;
        Color doorColor = Config.door;
        
        for(BufferedImage image : images) {
            
            Graphics g = image.getGraphics();
            int[][][] pixels = Util.getColorData(image);
            
            int width = pixels.length;
            int height = pixels[0].length;
            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    int[] colour = pixels[x][y];
                    if(colour[3] > 0) {
                        if(colour[0] > 0x7f && colour[1] > 0x7f && colour[2] > 0x7f) {
                            g.clearRect(x,y,0,0);
                            g.setColor(wallColor);
                            g.drawRect(x,y,0,0);
                        } else if(colour[0] <= 0x7f && colour[1] <= 0x7f && colour[2] <= 0x7f) {
                            g.clearRect(x,y,0,0);
                            g.setColor(doorColor);
                            g.drawRect(x,y,0,0);
                        }
                    }
                }
            }
            
        }
        
    }
    
}
