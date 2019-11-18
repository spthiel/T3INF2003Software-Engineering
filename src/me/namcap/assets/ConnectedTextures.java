package me.namcap.assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.util.Util;

public class ConnectedTextures {
    
    public enum WallDirection {
        CROSS,
        NOTUP,
        NOTRIGHT,
        NOTDOWN,
        NOTLEFT,
        UPRIGHT,
        DOWNRIGHT,
        DOWNLEFT,
        UPLEFT,
        HORIZONTAL,
        VERTICAL,
        ENDUP,
        ENDRIGHT,
        ENDDOWN,
        ENDLEFT,
        ENDALL,
        NONE;
        
        private static final String path = "/sprites/Walls/";
        private BufferedImage img;
        
        WallDirection() {
    
            try {
                this.img = ImageIO.read(ConnectedTextures.class.getResourceAsStream(path + toString() + ".png"));
            } catch (IOException e) {
                throw new RuntimeException(path + toString() + ".png",e);
            }
            Util.translate(img);
        }
    
        public BufferedImage getImg() {
        
            return img;
        }
    
        public static void updateColor() {
            for (WallDirection t : values()) {
                try {
                    t.img = ImageIO.read(ConnectedTextures.class.getResourceAsStream(path + t.toString() + ".png"));
                } catch (IOException e) {
                    throw new RuntimeException(path + t.toString() + ".png",e);
                }
                Util.translate(t.img);
            }
        }
    }
    
    private WallDirection[][] walls;
    private DataToObject[][] map;
    
    public ConnectedTextures(Map map) {
        walls = new WallDirection[map.getWidth()][map.getHeight()];
        this.map = map.getMap();
        for(int x = 0; x < map.getWidth(); x++) {
            for(int y = 0; y < map.getHeight(); y++) {
                walls[x][y] = connect(x,y);
            }
        }
    }
    
    public WallDirection get(int x, int y) {
        return walls[x][y];
    }
    
    public BufferedImage getImage(int x, int y) {
        return walls[x][y].getImg();
    }
    
    @SuppressWarnings("ConstantConditions")
    public WallDirection connect(int x, int y) {
    
        if (!inBounds(x, y)) {
            return WallDirection.NONE;
        }
        
        boolean up = inBounds(x, y - 1) && (map[x][y - 1] == DataToObject.WALL || vDoor(x, y - 1));
        boolean right = inBounds(x + 1, y) && (map[x + 1][y] == DataToObject.WALL || hDoor(x + 1, y));
        boolean down = inBounds(x, y + 1) && (map[x][y + 1] == DataToObject.WALL || vDoor(x, y + 1));
        boolean left = inBounds(x - 1, y) && (map[x - 1][y] == DataToObject.WALL || hDoor(x - 1, y));
        
        if (map[x][y] == DataToObject.WALL) {
            // Cross
            if (up && right && down && left) {
                return WallDirection.CROSS;
            }
            // T-Section
            else if (!up && right && down && left) {
                return WallDirection.NOTUP;
            } else if (up && !right && down && left) {
                return WallDirection.NOTRIGHT;
            } else if (up && right && !down && left) {
                return WallDirection.NOTDOWN;
            } else if (up && right && down && !left) {
                return WallDirection.NOTLEFT;
            }
            // L-Section
            else if (up && right && !down && !left) {
                return WallDirection.UPRIGHT;
            } else if (!up && right && down && !left) {
                return WallDirection.DOWNRIGHT;
            } else if (!up && !right && down && left) {
                return WallDirection.DOWNLEFT;
            } else if (up && !right && !down && left) {
                return WallDirection.UPLEFT;
            }
            // Straight
            else if (up && !right && down && !left) {
                return WallDirection.HORIZONTAL;
            } else if (!up && right && !down && left) {
                return WallDirection.VERTICAL;
            }
            // End
            else if (up && !right && !down && !left) {
                return WallDirection.ENDUP;
            } else if (!up && right && !down && !left) {
                return WallDirection.ENDRIGHT;
            } else if (!up && !right && down && !left) {
                return WallDirection.ENDDOWN;
            } else if (!up && !right && !down && left) {
                return WallDirection.ENDLEFT;
            } else if (!up && !right && !down && !left) {
                return WallDirection.ENDALL;
            }
        } else if (map[x][y] == DataToObject.DOOR) {
            if (vDoor(x, y)) {
                return WallDirection.VERTICAL;
            } else if (hDoor(x, y)) {
                return WallDirection.HORIZONTAL;
            }
        }
        
        return WallDirection.NONE;
    }
    
    private boolean inBounds(int x, int y) {
        
        return x > -1 && x < map.length && y > -1 && y < map[0].length;
    }
    
    //  #
    //  |
    //  #
    private boolean hDoor(int x, int y) {
    
        if (map[x][y] != DataToObject.DOOR) {
            return false;
        }
        
        return inBounds(x - 1, y) &&
                inBounds(x + 1, y) &&
                map[x - 1][y] == DataToObject.WALL &&
                map[x + 1][y] == DataToObject.WALL;
    }
    
    // #-#
    private boolean vDoor(int x, int y) {
    
        if (map[x][y] != DataToObject.DOOR) {
            return false;
        }
        
        return inBounds(x, y - 1) &&
                inBounds(x, y + 1) &&
                map[x][y - 1] == DataToObject.WALL &&
                map[x][y + 1] == DataToObject.WALL;
    }
}
