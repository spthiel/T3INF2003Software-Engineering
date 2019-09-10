package me.namcap.main;

import javax.swing.*;

import me.namcap.game.Map;
import me.namcap.game.MapLoader;

public class Main {

    public static void main(String[] args) {
    
        JFrame frame = new JFrame("NamCap");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new Panel());
        frame.pack();
        
    }
}
