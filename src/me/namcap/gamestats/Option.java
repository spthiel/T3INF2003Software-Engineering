package me.namcap.gamestats;

import java.awt.*;
import java.lang.reflect.Field;

import me.namcap.Textures.Fonts;
import me.namcap.main.Config;
import static me.namcap.gamestats.Option.Type.*;

public class Option {
    
    public enum Type {
        INT,
        COLOR,
        FLOAT,
        FONT,
        DIFFICULTY,
        BACK
    }
    
    private Field field;
    private Type type;
    private String key;
    private int min, max, step;
    private float minF, maxF, stepF;
    private int steps;
    private int selected;
    
    private void initialize(String key, String fieldname, Type type) {
    
        this.type = type;
        this.key = key;
        try {
            this.field = Config.class.getField(fieldname);
            if(type.equals(INT)) {
                int currentValue = (int)field.get(null);
                if(currentValue < min) {
                    selected = 0;
                } else if(currentValue > max) {
                    selected = 0;
                } else {
                    int closest = Integer.MIN_VALUE;
                    selected = 0;
                    int idx = 0;
                    for(int i = min; i <= max; i += step) {
                        if(currentValue - i < closest - i) {
                            closest = currentValue;
                            selected = idx;
                        }
                        idx++;
                    }
                }
            } else if(type.equals(FLOAT)) {
                float currentValue = (float)field.get(null);
                if(currentValue < min) {
                    selected = 0;
                } else if(currentValue > max) {
                    selected = 0;
                } else {
                    float closest = Integer.MIN_VALUE;
                    selected = 0;
                    int idx = 0;
                    for(int i = min; i <= max; i += step) {
                        if(currentValue - i < closest - i) {
                            closest = currentValue;
                            selected = idx;
                        }
                        idx++;
                    }
                }
            } else if(type.equals(FONT)) {
                Fonts currentValue = (Fonts)field.get(null);
                for(Fonts f : Fonts.values()) {
                    if(f.equals(currentValue)) {
                        selected = f.ordinal();
                        break;
                    }
                }
                selected = 0;
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            throw new IllegalArgumentException("Invalid field for " + key,e);
        }
    }
    
    public Option(String key) {
        this.type = Type.BACK;
        this.key = key;
    }
    
    public Option(String key, String fieldname, Type type) {
        if(type != COLOR && type != FONT && type != DIFFICULTY) {
            throw new IllegalArgumentException("You may not use any Type besides color, font and difficulty without Parameters.");
        }
        switch (type) {
            case FONT:
                this.step = 1;
                this.min = 0;
                this.max = Fonts.values().length;
                break;
            case DIFFICULTY:
                this.step = 1;
                this.min = 0;
                this.max = Config.Difficulty.values().length;
                break;
        }
        initialize(key, fieldname, type);
    }
    
    public Option(String key, String fieldname, Type type, int min, int max) {
        this(key, fieldname, type, min, max, 1);
    }
    
    public Option(String key, String fieldname, Type type,  int min, int max, int step) {
        if(type != INT && type != FONT) {
            throw new IllegalArgumentException("You may not use any Type besides int with two Parameters.");
        }
        this.min = min;
        this.max = max;
        this.step = step;
        initialize(key, fieldname, type);
    }
    
    public Option(String key, String fieldname, Type type, float min, float max, float step) {
        if(type != INT) {
            throw new IllegalArgumentException("You may not use any Type besides int with two Parameters.");
        }
        this.minF = min;
        this.maxF = max;
        this.stepF = (max-min)/(float)Math.floor(((max-min)/step));
        initialize(key, fieldname, type);
    }
    
    public Type getType() {
        
        return type;
    }
    
    public Number getMin() {
        switch(type) {
            case INT:
                return min;
            case FLOAT:
                return minF;
        }
        return null;
    }
    
    public Number getMax() {
        switch(type) {
            case INT:
                return max;
            case FLOAT:
                return maxF;
        }
        return null;
    }
    
    public float getStep() {
        if(type.equals(FLOAT) || type.equals(INT)) {
            return step;
        }
        return -1;
    }
    
    public String getKey() {
        return key;
    }
    
    public void draw(Graphics g, int x, int y) {
        
        int steps;
        
        switch(type) {
            case FLOAT:
                steps = (int)((maxF-minF)/stepF);
                break;
            case INT:
            case FONT:
            case DIFFICULTY:
                steps = (max-min)/step;
                break;
            default:
                return;
        }
        
//        int liney = y+height/2-1;
//        g.fillRect(x, liney, width, 4);
//        
//        float dx = (width-4f)/steps;
//
//        float newheight = height*0.5f;
//        int newy = y+(height-(int)newheight)/2;
//
//        Color before = g.getColor();
//
//        int idx = 0;
//        for (float i = x ; i < x+width ; i += dx) {
//            if(selected == idx) {
//                g.setColor(Color.red);
//            } else {
//                g.setColor(before);
//            }
//            g.fillRect((int)i, newy, 4, (int)newheight);
//            idx++;
//        }
        
    }
    
    public void update() {
    
    }
    
}
