package me.namcap.main;

import java.awt.*;

import me.namcap.assets.Fonts;

public class Config {
    
    public enum Difficulty {
        EASY, MEDIUM, HARD;
    }
    
    public static int
            maxups      = 60,
            maxfps      = 120,
            blocksize   = 32,
            coinsize    = 4,
            bitcoinsize = 8;
    
    public static Fonts font = Fonts.COURIER;
    
    public static Difficulty difficulty = Difficulty.HARD;
    
    public static float VELOCITY = 1 / 10f;
    
    public static final Color
            coin    = new Color(255, 255, 0),
            bitcoin = new Color(207, 255, 0),
            wall    = new Color(27, 87, 27),
            door    = new Color(249, 39, 114);
    
}
