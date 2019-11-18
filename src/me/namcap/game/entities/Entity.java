package me.namcap.game.entities;

import java.awt.image.BufferedImage;

import me.namcap.util.Direction;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.gamestats.GameState;
import me.namcap.main.Config;

import static me.namcap.util.Util.bounds;

public abstract class Entity {
    
    protected Direction direction = Direction.WEST;
    protected Direction tryDir = Direction.WEST;
    protected int x,y;
    protected float   progress = 0;
    protected boolean stop = false;
    protected Map     map;
    private float velocity;
    protected GameState state;
    
    public Entity(Map map) {
        this(null, Config.VELOCITY, 0, 0, map);
    }
    
    public Entity(GameState state, float velocity, int x, int y, Map map) {
        
        this.x = x;
        this.y = y;
        this.map = map;
        this.velocity = velocity;
        this.state = state;
    }
    
    protected void setVelocity(float velocity) {
        this.velocity = velocity;
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
            x = bounds(x + direction.getDx(), map.getWidth()-1);
            y = bounds(y + direction.getDy(), map.getHeight()-1);
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
        return x + (progress * direction.getDx());
    }
    
    public float getY() {
        
        return y + (progress * direction.getDy());
    }
    
    protected boolean checkBlock(Direction direction) {
        if(direction == null) {
            System.exit(0);
        }
        return checkBlock(direction.getDx(), direction.getDy()) || checkGhost(direction.getDx(), direction.getDy());
    }
    
    private boolean checkBlock(int dx, int dy) {
        int x = bounds(this.x + dx, map.getWidth()-1);
        int y = bounds(this.y + dy, map.getHeight()-1);
        return map.getBlock(x,y).equals(DataToObject.WALL);
    }
    
    private Ghost getGhostAt(Direction dir) {
        int x = bounds(this.x + dir.getDx(), map.getWidth()-1);
        int y = bounds(this.y + dir.getDy(), map.getHeight()-1);
        return state.getGhostAt(x,y);
    }
    
    private boolean checkGhost(int dx, int dy) {
        int x = bounds(this.x + dx, map.getWidth()-1);
        int y = bounds(this.y + dy, map.getHeight()-1);
        Ghost g = state.getGhostAt(x,y);
        return g != null && !g.isReturning();
    }
    
    public abstract BufferedImage getImage();
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getIX() {
        return x;
    }
    
    public int getIY() {
        return y;
    }
    
    public Direction getDirection() {
        
        return direction;
    }
}
