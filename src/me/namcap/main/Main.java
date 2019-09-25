package me.namcap.main;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
    
        JFrame frame = new JFrame("NamCap");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final Panel p = new Panel();
        frame.setResizable(false);
        frame.add(p);
        frame.addKeyListener(p);
        frame.pack();
        placeCentered(frame);
    
        Thread drawLoop = new Thread(() -> {
        
            long current;
            long last = System.currentTimeMillis();
            float delta;
            float timeperframe = 1000.0f/ Config.maxfps;
            float timer = 0;
        
            while(true) {
            
                current = System.currentTimeMillis();
                delta = current - last;
                last = current;
                timer += delta;
            
                if(timer >= timeperframe) {
                    p.repaint();
                    timer -= timeperframe;
                }
            }
        
        });
        drawLoop.start();
        
        Thread updateLoop = new Thread(() -> {
        
            long current;
            long last = System.currentTimeMillis();
            float delta;
            float timeperframe = 1000.0f/ Config.maxups;
            float timer = 0;
            float upstimer = 0;
            int ups = 0;
        
            while(true) {
            
                current = System.currentTimeMillis();
                delta = current - last;
                last = current;
                timer += delta;
                upstimer += delta;
            
                if(upstimer > 1000) {
                    System.out.println("Ups: " + ups);
                    ups = 0;
                    upstimer -= 1000;
                }
                if(timer >= timeperframe) {
                    p.update();
                    ups++;
                    timer -= timeperframe;
                }
            }
        
        });
        updateLoop.start();
    }
    
    private static void placeCentered(JFrame f) {
        Rectangle res = getResolution(f);
        Dimension size = f.getSize();
        int x = res.x + res.width/2-size.width/2;
        int y = res.y + res.height/2-size.height/2;
        f.setBounds(x,y,size.width,size.height);
    }
    
    
    private static Rectangle getResolution(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        //height of the task bar
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        
        int x = scnMax.left;
        int y = scnMax.top;
        int width = screenSize.width-scnMax.left-scnMax.right;
        int height = screenSize.height-y-scnMax.bottom;
        Rectangle rect = new Rectangle();
        rect.x = x;
        rect.y = y;
        rect.width = width;
        rect.height = height;
        return rect;
    }
}
