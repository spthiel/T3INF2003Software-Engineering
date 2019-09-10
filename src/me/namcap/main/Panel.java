package me.namcap.main;

import javax.swing.*;
import java.awt.*;

import me.namcap.gamestats.GameState;
import me.namcap.gamestats.IGamestate;

public class Panel extends JPanel {
    
    private static IGamestate gamestate = new GameState();
    
    public Panel() {
        setBackground(Color.black);
        setVisible(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        gamestate.draw(g);
    }
    
    @Override
    public Dimension getPreferredSize() {
        
        return gamestate.getPreferredSize();
    }
}
