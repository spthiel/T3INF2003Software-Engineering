package me.namcap.game.entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import me.namcap.Textures.Textures;
import me.namcap.Util.Direction;
import me.namcap.Util.Vec2O2;
import me.namcap.Util.pathfinding.PathFinder;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.gamestats.GameState;
import me.namcap.main.Config;

public class Ghost extends Entity {
    
    public static PathFinder finder;
    public static final BufferedImage empty = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
    
    static {
        Graphics g = empty.getGraphics();
        g.setColor(new Color(0,0,0,0));
        g.fillRect(0,0,32,32);
    }
    
    public static final int boredom = 180;
    public static final int mortality = 600;
    private static int counter = 0;
    
    private BufferedImage img;
    private int boredCounter = -1;
    private int mortalTimer = 0;
    private Player p;
    
    private int startx;
    private int starty;
    
    private boolean returning;
    
    public Ghost(GameState state, int x, int y, Player p, Map map) {
        super(state, Config.VELOCITY*2/3, x, y, map);
        img = Textures.GHOSTS.get(counter++);
        this.p = p;
        switch (Config.difficulty) {
            case HARD:
                boredCounter = -1;
                break;
            case MEDIUM:
            case EASY:
                boredCounter = 0;
                break;
        }
        this.startx = x;
        this.starty = y;
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
            if(returning) {
                backAI();
            } else if(boredCounter > 0 || boredCounter == -1) {
                seekAI();
            } else {
                randomAI();
            }
        }
        return false;
    }
    
    public boolean isVincible() {
        return mortalTimer > 0;
    }
    
    @Override
    public BufferedImage getImage() {
        
        return returning ? empty : mortalTimer > 0 ? Textures.MORTALGHOST.get(0) : img;
    }
    
    public BufferedImage getEye() {
        if(stop) {
            return Textures.EYES.get(4);
        }
        return Textures.EYES.get(direction.getValue());
    }
    
    private static final Random random = new Random();
    
    private void randomAI() {
        Vec2O2<Direction[],Direction> out        = findDirections();
        Direction[]                   directions = out.x1;
        Direction                     otherWay   = out.x2;
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
    
    public void kill() {
        returning = true;
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
    
    public void setVincible() {
        if(!returning) {
            this.mortalTimer = mortality;
        }
    }
    
    private void seekAI() {
    
        finder.reset();
        int dir = finder.find(this, p);
        if(dir == -2) {
            state.kill();
            return;
        }
        if(dir == -1) {
            return;
        }
        Direction d = Direction.values()[dir];
        direction = d;
        tryDir = d;
        
    }
    
    private void doorAI() {
    
    }
    
    private void backAI() {
    
        finder.reset();
        int dir = finder.find(x, y, startx, starty);
        if(dir == -2) {
            returning = false;
            mortalTimer = 0;
            return;
        }
        if(dir == -1) {
            System.out.println(x + " " + y + " " + startx + " " + starty);
            return;
        }
        Direction d = Direction.values()[dir];
        direction = d;
        tryDir = d;
    }
    
    public boolean isReturning() {
        
        return returning;
    }
}
