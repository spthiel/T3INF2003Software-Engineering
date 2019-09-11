package me.namcap.game.entities;

import java.awt.image.BufferedImage;

import me.namcap.Util.BoolConsumer;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.main.Constants;

public abstract class Entity {
    
    protected enum Direction {
        
        NORTH(0,0,-1),
        EAST(1,1,0),
        SOUTH(2,0,1),
        WEST(3,-1,0);
        
        private int value,dx,dy;
        
        Direction(int value, int dx, int dy) {
            this.value = value;
            this.dx = dx;
            this.dy = dy;
        }
        
        public boolean isOtherway(Direction d) {
            switch(this) {
                case NORTH:
                    return d.equals(SOUTH);
                case EAST:
                    return d.equals(WEST);
                case SOUTH:
                    return d.equals(NORTH);
                case WEST:
                    return d.equals(EAST);
            }
            return false;
            //return this.dx == (-d.dx) && this.dy == (-d.dy);
        }
    
        public int getValue() {
        
            return value;
        }
    
        public int getDx() {
        
            return dx;
        }
    
        public int getDy() {
        
            return dy;
        }
    }
    
    protected Direction direction = Direction.WEST;
    protected Direction tryDir = Direction.WEST;
    protected int x,y;
    protected float   progress = 0;
    protected boolean stop = false;
    protected Map     map;
    private float velocity;
    private BoolConsumer isGhostAt;
    
    public Entity(Map map) {
        this((x,y) -> false, Constants.VELOCITY, 0, 0, map);
    }
    
    public Entity(BoolConsumer isGhostAt, float velocity, int x, int y, Map map) {
        
        this.x = x;
        this.y = y;
        this.map = map;
        this.velocity = velocity;
        this.isGhostAt = isGhostAt;
    }
    
    private boolean move() {
        if(stop) {
            return false;
        }
        if(checkBlock(direction)) {
            stop = true;
            return true;
        }
        progress += velocity;
        if(progress >= 1) {
            x = bounds(x + direction.dx, 0, map.getWidth()-1);
            y = bounds(y + direction.dy, 0, map.getHeight()-1);
            progress = 0;
            if(!checkBlock(tryDir)) {
                direction = tryDir;
            } else if(checkBlock(direction)) {
                stop = true;
            }
            return true;
        }
        return false;
    }
    
    public boolean update() {
        return move();
    }
    
    public float getX() {
        return x + (progress * direction.dx);
    }
    
    public float getY() {
        
        return y + (progress * direction.dy);
    }
    
    protected boolean checkBlock(Direction direction) {
        if(direction == null) {
            System.exit(0);
        }
        return checkBlock(direction.dx, direction.dy) || checkGhost(direction.dx, direction.dy);
    }
    
    private boolean checkBlock(int dx, int dy) {
        int x = bounds(this.x + dx, 0, map.getWidth()-1);
        int y = bounds(this.y + dy, 0, map.getHeight()-1);
        return map.getBlock(x,y).equals(DataToObject.WALL);
    }
    
    private boolean checkGhost(int dx, int dy) {
        int x = bounds(this.x + dx, 0, map.getWidth()-1);
        int y = bounds(this.y + dy, 0, map.getHeight()-1);
        return isGhostAt.accept(x,y);
    }
    
    protected int bounds(int value, int min, int max) {
        if(value < min) {
            value = max;
        }
        if(value > max) {
            value = min;
        }
        return value;
    }
    
    public abstract BufferedImage getImage();
}
