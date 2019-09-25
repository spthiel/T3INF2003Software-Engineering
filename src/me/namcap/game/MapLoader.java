package me.namcap.game;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MapLoader {
    
    private static ArrayList<Map> maps     = new ArrayList<>();
    private static Random         rng      = new Random();
    private static boolean        isLoaded = false;
    
    public static void loadMaps() throws IOException {
        
        maps.clear();
        int         idx    = 0;
        InputStream stream = MapLoader.class.getResourceAsStream("/Maps/" + idx + ".png");
        while(stream != null) {
            maps.add(new Map(ImageIO.read(stream)));
            idx++;
            stream = MapLoader.class.getResourceAsStream("/Maps/" + idx + ".png");
        }
    }
    
    public static Map getRandomMap() {
        
        if (!isLoaded) {
            try {
                loadMaps();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (maps.size() == 0) {
            throw new RuntimeException("No map was found");
        }
        return maps.get(rng.nextInt(maps.size()));
    }
}
