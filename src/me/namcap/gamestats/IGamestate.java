package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyListener;

public interface IGamestate extends KeyListener {
    
    void update();
    void draw(Graphics g);
    Dimension getPreferredSize();
    
}
