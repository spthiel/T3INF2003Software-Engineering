package me.namcap.game.entities;

import java.util.Queue;

import me.namcap.game.DataToObject;
import me.namcap.util.Vec2I;
import me.namcap.util.Vec2O;

import static me.namcap.util.Util.bounds;

public class Door {
    
    private int x, y;
    private boolean valid;
    private int dirIdentifier = 0;
    private int width, height;
    
    private Queue<Ghost> lockedGhosts;
    
    public Door(int x, int y, DataToObject[][] map) {
        this.x = x;
        this.y = y;
        valid = checkValidity(map);
    }
    
    public void release() {
        Ghost g = lockedGhosts.remove();
        g.setDoorAITo(new Vec2I(x,y));
    }
    
    public int size() {
        return lockedGhosts.size();
    }
    
    public void setLockedGhosts(Queue<Ghost> lockedGhosts) {
        
        this.lockedGhosts = lockedGhosts;
        lockedGhosts.forEach(Ghost::setInDoor);
    }
    
    public int getX() {
        
        return x;
    }
    
    public int getY() {
        
        return y;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setInvalid() {
        valid = false;
    }
    
    public void setValid() {
        valid = true;
    }
    
    public boolean isMe(int x, int y) {
        return this.x == x && this.y == y;
    }
    
    private boolean checkValidity(DataToObject[][] map) {
        int adjacent = 0;
        width = map.length;
        height = map[0].length;
        if(map[bounds(x-1, width)][y].equals(DataToObject.WALL)) {
            adjacent = 0b0001;
        }
        if(map[bounds(x+1, width)][y].equals(DataToObject.WALL)) {
            adjacent |= 0b0010;
        }
        if(map[x][bounds(y-1, height)].equals(DataToObject.WALL)) {
            adjacent |= 0b0100;
        }
        if(map[x][bounds(y+1, height)].equals(DataToObject.WALL)) {
            adjacent |= 0b1000;
        }
        dirIdentifier = adjacent;
        return adjacent == 3 || adjacent == 12;
    }
    
    public Vec2O<Vec2I> getSides() {
        if (dirIdentifier == 12) {
            return new Vec2O<>(new Vec2I(bounds(x+1, width),y),new Vec2I(bounds(x-1,width),y));
        } else {
            return new Vec2O<>(new Vec2I(x,bounds(y+1,height)), new Vec2I(x,bounds(y-1,height)));
        }
    }
    
}
