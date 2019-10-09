package me.namcap.gamestats;

import java.awt.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Consumer;

import me.namcap.assets.Fonts;
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
    
    private NumberFormat format = new DecimalFormat("###,##0.00");
    
    private Field field;
    private Type type;
    private String key;
    private int min, max, step;
    private float minF, maxF, stepF;
    private int steps;
    private Object cachedValue;
    
    private boolean instantupdate = false;
    private Consumer<Object> callOnUpdate = null;
    
    private void initialize(String key, String fieldname, Type type) {
    
        this.type = type;
        this.key = key;
        try {
            this.field = Config.class.getField(fieldname);
            if(type.equals(INT)) {
                int currentValue = (int)field.get(null);
                if(currentValue < min) {
                    cachedValue = min;
                } else if(currentValue > max) {
                    cachedValue = max;
                } else {
                    cachedValue = min;
                    int closest = Integer.MAX_VALUE;
                    for (int i = min ; i <= max ; i += step) {
                        if (Math.abs(currentValue - i) < Math.abs(closest - i)) {
                            closest = currentValue;
                            cachedValue = closest;
                        }
                    }
                }
            } else if(type.equals(FLOAT)) {
    
                float currentValue = (float)field.get(null);
                if(currentValue < minF) {
                    cachedValue = minF;
                } else if(currentValue > maxF) {
                    cachedValue = maxF;
                } else {
                    cachedValue = minF;
                    float closest = Integer.MAX_VALUE;
                    for (float i = minF ; i <= maxF ; i += stepF) {
                        if (Math.abs(currentValue - i) < Math.abs(closest - i)) {
                            closest = currentValue;
                            cachedValue = closest;
                        }
                    }
                }
            } else if(type.equals(FONT)) {
                Fonts currentValue = (Fonts)field.get(null);
                for(Fonts f : Fonts.values()) {
                    if(f.equals(currentValue)) {
                        cachedValue = f;
                        break;
                    }
                }
                if (cachedValue == null) {
                    cachedValue = Fonts.values()[0];
                }
            } else if(type.equals(DIFFICULTY)) {
                Config.Difficulty currentValue = (Config.Difficulty) field.get(null);
                for(Config.Difficulty d : Config.Difficulty.values()) {
                    System.out.println(d + " " + currentValue);
                    if(d.equals(currentValue)) {
                        cachedValue = d;
                        break;
                    }
                }
                if (cachedValue == null) {
                    cachedValue = Config.Difficulty.values()[0];
                }
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
        if(type != INT) {
            throw new IllegalArgumentException("You may not use any Type besides int with int Parameters.");
        }
        this.min = min;
        this.max = max;
        this.step = step;
        initialize(key, fieldname, type);
    }
    
    public Option(String key, String fieldname, Type type, float min, float max, float step) {
        if(type != FLOAT) {
            throw new IllegalArgumentException("You may not use any Type besides float with float Parameters.");
        }
        this.minF = min;
        this.maxF = max;
        this.stepF = (max-min)/(float)Math.floor(((max-min)/step));
        initialize(key, fieldname, type);
    }
    
    public Option setInstantupdate() {
        instantupdate = true;
        return this;
    }
    
    public Option onUpdate(Consumer<Object> consumer) {
        callOnUpdate = consumer;
        return this;
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
        
        switch (type) {
            case INT:
            case DIFFICULTY:
            case FONT:
                g.drawString(cachedValue.toString(), x, y);
                break;
            case FLOAT:
                g.drawString(format.format((float)cachedValue), x, y);
                break;
            default:
        }
    }
    
    public void next() {
        switch (type) {
            case INT:
                cachedValue = (int)cachedValue + step;
                if((int)cachedValue > max) {
                    cachedValue = min;
                }
                break;
            case FLOAT:
                cachedValue = (float)cachedValue + stepF;
                if((float)cachedValue > maxF) {
                    cachedValue = minF;
                }
                break;
            case FONT:
                cachedValue = ((Fonts)cachedValue).next();
                break;
            case DIFFICULTY:
                switch ((Config.Difficulty)cachedValue) {
                    case EASY:
                        cachedValue = Config.Difficulty.MEDIUM;
                        break;
                    case MEDIUM:
                        cachedValue = Config.Difficulty.HARD;
                        break;
                    case HARD:
                        cachedValue = Config.Difficulty.EASY;
                        break;
                }
                break;
        }
        if (instantupdate) {
            update();
        }
    }
    
    public void previous() {
    
        switch (type) {
            case INT:
                cachedValue = (int)cachedValue - step;
                if((int)cachedValue < min) {
                    cachedValue = max;
                }
                break;
            case FLOAT:
                cachedValue = (float)cachedValue - stepF;
                if((float)cachedValue < minF) {
                    cachedValue = maxF;
                }
                break;
            case FONT:
                cachedValue = ((Fonts)cachedValue).previous();
                break;
            case DIFFICULTY:
                switch ((Config.Difficulty)cachedValue) {
                    case EASY:
                        cachedValue = Config.Difficulty.HARD;
                        break;
                    case MEDIUM:
                        cachedValue = Config.Difficulty.EASY;
                        break;
                    case HARD:
                        cachedValue = Config.Difficulty.MEDIUM;
                        break;
                }
                break;
        }
        if (instantupdate) {
            update();
        }
    }
    
    public void update() {
        try {
            if (field != null) {
                field.set(null, cachedValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (callOnUpdate != null) {
            callOnUpdate.accept(cachedValue);
        }
    }
    
    @Override
    public String toString() {
        
        switch (type) {
            case FLOAT:
                return String.format("Option[\"type\":\"%s\",\"field\":\"%s\",\"key\":\"%s\",\"min\":%d,\"max\":%f,\"step\":%f,\"cachedValue\":%s]", type.name(), field.getName(), key, minF, maxF, stepF, cachedValue.toString());
            case DIFFICULTY:
            case FONT:
                return String.format("Option[\"type\":\"%s\",\"field\":\"%s\",\"key\":\"%s\",\"cachedValue\":%s]", type.name(), field.getName(), key, cachedValue.toString());
            case INT:
                return String.format("Option[\"type\":\"%s\",\"field\":\"%s\",\"key\":\"%s\",\"min\":%d,\"max\":%d,\"step\":%d,\"cachedValue\":%s]", type.name(), field.getName(), key, min, max, step, cachedValue.toString());
            case COLOR:
                return String.format("Option[\"type\":\"%s\",\"field\":\"%s\",\"key\":\"%s\"]", type.name(), field.getName(), key);
            case BACK:
                return String.format("Option[\"type\":\"%s\",\"key\":\"%s\"]", type.name(), key);
            default:
                return "Option[Error]";
        }
        
    }
}
