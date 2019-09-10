package me.namcap.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MapLoader {
    
    private static final File folder = new File("./res/Maps");
    
    private static ArrayList<Map> maps     = new ArrayList<>();
    private static Random         rng      = new Random();
    private static boolean        isLoaded = false;
    
    public static void loadMaps() {
        
        maps.clear();
        
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
            return;
        }
        
        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (!f.getName().endsWith("png")) {
                continue;
            }
            try {
                BufferedImage image = ImageIO.read(f);
                maps.add(new Map(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Map getRandomMap() {
        
        if (!isLoaded) {
            loadMaps();
        }
        if (maps.size() == 0) {
            throw new RuntimeException("No map was found");
        }
        return maps.get(rng.nextInt(maps.size()));
    }
}
