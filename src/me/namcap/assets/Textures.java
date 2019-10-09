package me.namcap.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import me.namcap.util.Util;

public enum Textures {
    
    VDOOR(true, "Door/Closed/Vertical","Door/Open/Vertical"),
    HDOOR(true, "Door/Closed/Horizontal","Door/Open/Horizontal"),
    GHOSTS(false, "Ghost/Body/Ghost1","Ghost/Body/Ghost2","Ghost/Body/Ghost3","Ghost/Body/Ghost4"),
    MORTALGHOST(false, "Ghost/Mortal/Mortal"),
    EYES(false, "Ghost/Eyes/Up","Ghost/Eyes/Right","Ghost/Eyes/Down","Ghost/Eyes/Left","Ghost/Eyes/None"),
    NAMCAP(false, "NamCap/Open/Up","NamCap/Open/Right","NamCap/Open/Down","NamCap/Open/Left","NamCap/Closed/Up","NamCap/Closed/Right","NamCap/Closed/Down","NamCap/Closed/Left")
    ;
    
    
    private static final String path = "/sprites/%s.png";
    private BufferedImage[] images;
    
    Textures(boolean translate, String... paths) {
        images = new BufferedImage[paths.length];
        for (int i = 0 ; i < paths.length ; i++) {
            try {
                images[i] = ImageIO.read(Textures.class.getResourceAsStream((String.format(path,paths[i]))));
            } catch (IOException | IllegalArgumentException e) {
                throw new RuntimeException(String.format(path,paths[i]),e);
            }
        }
        if(translate) {
            Util.translate(images);
        }
    }
    
    public BufferedImage get(int idx) {
        return images[idx%images.length];
    }
    
    public BufferedImage getNamCap(int idx, int frame) {
        return images[idx%4 + 4*(frame%2)];
    }
    
}
