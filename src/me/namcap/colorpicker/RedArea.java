package me.namcap.colorpicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

import me.namcap.util.Direction;
import me.namcap.util.Util;

public class RedArea extends ColorArea {

    private int red = 0, green = 0, blue = 0;
    private Consumer<Integer> callback;
    
    public RedArea(int scale, Consumer<Integer> callback) {
        super(scale, 255, 20);
        this.callback = callback;
    }
    
    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public void setGreenBlue(int green, int blue) {
        this.green = green;
        this.blue = blue;
    }
    
    @Override
    void processClick(int x, int y) {
        red = x;
        callback.accept(red);
    }
    
    void movePoint(int x, int y) {
        red += x;
        if(red < 0) {
            red = 0;
        }
        if(red > 255) {
            red = 255;
        }
        callback.accept(red);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        for(int x = 0; x < 255; x++) {
            fillRect(g, x, 0, 1, 20, x, green, blue);
        }
    
        g.setColor(Color.black);
        fillRect(g, red, 0, 1, 20);
        fillRect(g, 0, 10, 255, 1);
        drawRect(g, red - 3, 10 - 3, 6, 6);
    
        g.setColor(Color.RED);
        fillRect(g, red, 10, 1, 1);
    }
    
}
