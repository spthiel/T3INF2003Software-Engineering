package me.namcap.game;

public enum DataToObject {
    
    NONE(true,true,true,true),
    GHOST(false, true, true, true),
    WALL(false, false, false, true),
    DOOR(false, true, false, true),
    COIN(true, true, false, true),
    BITCOIN(true, false, false, true),
    PLAYER(false, false, true, true);
    
    private boolean red, green, blue, alpha;
    
    DataToObject(boolean red, boolean green, boolean blue, boolean alpha) {
        
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public static DataToObject getObject(int red, int green, int blue, int alpha) {
        
        for (DataToObject object : values()) {
            if(
                    object.red   ==   red > 0x7f &&
                    object.blue  ==  blue > 0x7f &&
                    object.green == green > 0x7f &&
                    (object.alpha == alpha > 0x7f || alpha == -1)) {
                return object;
            }
        }
        return NONE;
    }
    
}
