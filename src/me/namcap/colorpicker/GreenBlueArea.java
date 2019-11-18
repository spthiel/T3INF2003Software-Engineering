package me.namcap.colorpicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import me.namcap.util.Direction;
import me.namcap.util.Util;

public class GreenBlueArea extends ColorArea {
    
    private int green = 0, blue = 0, red = 0;
    private BiConsumer<Integer,Integer> callback;
    
    public GreenBlueArea(int scale, BiConsumer<Integer,Integer> callback) {
        super(scale, 255, 255);
        this.callback = callback;
    }
    
    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public void setRed(int red) {
        
        this.red = red;
    }
    
    @Override
    void processClick(int x, int y) {
    
        green = x;
        blue = y;
        callback.accept(green, blue);
    }
    
    @Override
    void movePoint(int x, int y) {
    
        green += x;
        blue += y;
        if(green < 0) {
            green = 0;
        }
        if(green > 255) {
            green = 255;
        }
        if(blue < 0) {
            blue = 0;
        }
        if(blue > 255) {
            blue = 255;
        }
        callback.accept(green, blue);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        for(int x = 0; x < 255; x++) {
            for(int y = 0; y < 255; y++) {
                fillRect(g, x, y, 1, 1, red, x, y);
            }
        }
        
        g.setColor(Color.BLACK);
        fillRect(g, green, 0, 1, 255);
        fillRect(g, 0, blue, 255, 1);
        drawRect(g, green - 3, blue - 3, 6, 6);
    
        g.setColor(Color.RED);
        fillRect(g, green, blue, 1, 1);
    }
}
