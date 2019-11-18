package me.namcap.main;

import java.awt.*;
import java.io.*;

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
            bitcoinsize = 10,
            releaseDelay = 100,
            releaseTime = 100,
            boredom = 180;
    
    public static Fonts font = Fonts.COURIER;
    
    public static Difficulty difficulty = Difficulty.HARD;
    
    public static float VELOCITY = 1 / 10f;
    
    public static final Color
            coin    = new Color(255, 255, 0),
            bitcoin = new Color(207, 255, 0),
            door    = new Color(249, 39, 114);
    
    public static Color
            wall    = new Color(27, 87, 27);
    
    static {
        file = new File("./config.dat");
        load();
    }
    
    private static File file;
    
    public static void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("maxups:" + maxups + "\n");
            bw.write("maxfps:" + maxfps + "\n");
            bw.write("blocksize:" + blocksize + "\n");
            bw.write("font:" + font.ordinal() + "\n");
            bw.write("difficulty:" + difficulty.ordinal() + "\n");
            bw.write("velocity:" + VELOCITY + "\n");
            bw.write("boredom:" + boredom + "\n");
            bw.write("color:" + wall.getRed() + "," + wall.getGreen() + "," + wall.getBlue());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void load() {
        if(!file.exists()) {
            save();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                switch(parts[0]) {
                    case "maxups": {
                        maxups = Integer.parseInt(parts[1]);
                        break;
                    }
                    case "maxfps": {
                        maxfps = Integer.parseInt(parts[1]);
                        break;
                    }
                    case "blocksize": {
                        blocksize = Integer.parseInt(parts[1]);
                        break;
                    }
                    case "font": {
                        int value = Integer.parseInt(parts[1]);
                        font = Fonts.values()[value];
                        break;
                    }
                    case "difficulty": {
                        int value = Integer.parseInt(parts[1]);
                        difficulty = Difficulty.values()[value];
                        break;
                    }
                    case "velocity": {
                        VELOCITY = Float.parseFloat(parts[1]);
                        break;
                    }
                    case "boredom": {
                        boredom = Integer.parseInt(parts[1]);
                        break;
                    }
                    case "color": {
                        String[] splitted = parts[1].split(",");
                        wall = new Color(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
                        break;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
