package me.namcap.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import me.namcap.gamestats.IGamestate;
import me.namcap.gamestats.MenuState;

public class Panel extends JPanel implements KeyListener {
    
    private static IGamestate gamestate = new MenuState();
    
    public Panel() {
        setBackground(Color.black);
        setVisible(true);
    }
    
    public boolean update() {
        gamestate = gamestate.nextState();
        return gamestate.update();
    }
    
    private int repaints = 0;
    private long lastTimestamp = System.currentTimeMillis();
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        gamestate.draw(g);
        repaints++;
        if(lastTimestamp + 1000 < System.currentTimeMillis()) {
            System.out.println("Fps: " + repaints);
            lastTimestamp = System.currentTimeMillis();
            repaints = 0;
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
    
        return gamestate.getPreferredSize();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        gamestate.keyTyped(e);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        gamestate.keyPressed(e);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        gamestate.keyReleased(e);
    }
}
