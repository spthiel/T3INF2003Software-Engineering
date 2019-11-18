package me.namcap.colorpicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import me.namcap.util.Direction;

public abstract class ColorArea extends JPanel implements MouseMotionListener {
    
    private int scale;
    private int width, height;
    
    public ColorArea(int scale, int width, int height) {
        this.scale = scale;
        this.width = width;
        this.height = height;
        setSize(width*scale, height*scale);
        setVisible(true);
        addMouseMotionListener(this);
    }
    
    @Override
    public int getHeight() {
        
        return height*scale;
    }
    
    @Override
    public int getWidth() {
        
        return width*scale;
    }
    
    abstract void processClick(int x, int y);
    abstract void movePoint(int x, int y);
    
    private int speed = 1;
    private static final int maxspeed = 10;
    
    public void move(Direction d) {
        movePoint(d.getDx() * speed, d.getDy() * speed);
        speed++;
        if(speed > maxspeed) {
            speed = maxspeed;
        }
    }
    
    public void resetSpeed() {
        speed = 1;
    }
    
    protected void fillRect(Graphics g, int x, int y, int width, int height, int red, int green, int blue) {
        fillRect(g, x, y, width, height, new Color(red, green, blue));
    }
    
    protected void fillRect(Graphics g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        fillRect(g, x, y, width, height);
    }
    
    protected void fillRect(Graphics g, int x, int y, int width, int height) {
        g.fillRect(x*scale, y*scale, width*scale, height*scale);
    }
    
    protected void drawRect(Graphics g, int x, int y, int width, int height, int red, int green, int blue) {
        drawRect(g, x, y, width, height, new Color(red, green, blue));
    }
    
    protected void drawRect(Graphics g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        drawRect(g, x, y, width, height);
    }
    
    protected void drawRect(Graphics g, int x, int y, int width, int height) {
        g.drawRect(x*scale, y*scale, width*scale, height*scale);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        
        int x = e.getX()/scale, y = e.getY()/scale;
        if(x > width) {
            x = width;
        }
        if(x < 0) {
            x = 0;
        }
        if(y > height) {
            y = height;
        }
        if(y < 0) {
            y = 0;
        }
        processClick(x, y);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
    
    }
}
