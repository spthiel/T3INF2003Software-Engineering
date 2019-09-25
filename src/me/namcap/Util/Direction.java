package me.namcap.Util;

public enum Direction {
    
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
        return this.equals(otherWay(d));
    }
    
    public Direction otherWay() {
        return otherWay(this);
    }
    
    public static Direction otherWay(Direction d) {
        switch(d) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
        }
        return null;
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
