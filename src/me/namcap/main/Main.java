package me.namcap.main;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
    
        JFrame frame = new JFrame("NamCap");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final Panel p = new Panel();
        frame.add(p);
        frame.addKeyListener(p);
        frame.pack();
    
        Thread drawLoop = new Thread(() -> {
        
            long current;
            long last = System.currentTimeMillis();
            float delta;
            float timeperframe = 1000.0f/Constants.maxfps;
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
            float timeperframe = 1000.0f/Constants.maxups;
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
}
