package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;

public class OptionState implements IGamestate {
    
    
    
    @Override
    public boolean update() {return false;}
    
    @Override
    public void draw(Graphics g) {
    
    }
    
    @Override
    public Dimension getPreferredSize() {
        //Mach ich
        return null;
    }
    
    @Override
    public IGamestate nextState() {
        
        return this;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    
    }
}
