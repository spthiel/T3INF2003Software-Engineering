package me.namcap.gamestats;

import java.awt.*;

public interface IGamestate {
    
    void update();
    void draw(Graphics g);
    Dimension getPreferredSize();
    
}
