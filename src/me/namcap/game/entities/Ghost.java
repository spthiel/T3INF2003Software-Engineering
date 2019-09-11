package me.namcap.game.entities;

import java.awt.image.BufferedImage;
import java.util.Random;

import me.namcap.Textures.Textures;
import me.namcap.Util.BoolConsumer;
import me.namcap.Util.Vec2O2;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.main.Constants;

public class Ghost extends Entity {
    
    public static final int boredom = 180;
    public static final int mortality = 600;
    private static int counter = 0;
    
    private boolean ohNoeTheresAGhost = false;
    private int           id;
    private BufferedImage img;
    private int boredCounter = 0;
    private int mortalTimer = 0;
    
    public Ghost(Map map) {
        this((x,y) -> false, 0, 0, map);
    }
    
    public Ghost(BoolConsumer isGhostAt,int x, int y, Map map) {
        super(isGhostAt, Constants.VELOCITY/3*2, x, y, map);
        id = counter++;
        img = Textures.GHOSTS.get(id);
    }
    
    @Override
    public boolean update() {
        stop = false;
        if(boredCounter > 0) {
            boredCounter--;
        }
        if(mortalTimer > 0) {
            mortalTimer--;
        }
        if(super.update()) {
            randomAI();
        }
        return false;
    }
    
    @Override
    public BufferedImage getImage() {
        
        return img;
    }
    
    public BufferedImage getEye() {
        if(stop || ohNoeTheresAGhost) {
            return Textures.EYES.get(4);
        }
        return Textures.EYES.get(direction.getValue());
    }
    
    private static final Random random = new Random();
    
    private void randomAI() {
        Vec2O2<Direction[],Direction> out = findDirections();
        Direction[] directions = out.x1;
        Direction otherWay = out.x2;
        if(directions.length == 0) {
            if(otherWay != null) {
                direction = otherWay;
                tryDir = direction;
            }
            return;
        }
        direction = directions[random.nextInt(directions.length)];
        tryDir = direction;
        stop = false;
    }
    
    private int count(Object[] array) {
        int sum = 0;
        for (Object object : array) {
            if(object != null) {
                sum++;
            }
        }
        return sum;
    }
    
    private Vec2O2<Direction[],Direction> findDirections() {
        Direction[] all = new Direction[4];
        Direction otherWay = null;
        for(Direction d : Direction.values()) {
            DataToObject block = map.getBlock(bounds(x + d.getDx(), 0, map.getWidth()-1), bounds(y + d.getDy(), 0, map.getHeight()-1));
            if(block != DataToObject.WALL && block != DataToObject.DOOR) {
                if(d.isOtherway(direction)) {
                    otherWay = d;
                } else {
                    all[d.getValue()] = d;
                }
            }
        }
        Direction[] out = new Direction[count(all)];
        int idx = -1;
        for (int i = 0 ; i < all.length ; ++i) {
            if(all[i] != null) {
                out[++idx] = all[i];
            }
        }
        return new Vec2O2<>(out,otherWay);
    }
    
    private void seekAI() {
    
    }
}
