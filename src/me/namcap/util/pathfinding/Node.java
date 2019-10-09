package me.namcap.Util.pathfinding;

import me.namcap.Util.Direction;

public class Node {
    
    private enum State{
        UNVISITED,
        OPEN,
        CLOSED;
    }
    private enum Type {
        NORMAL,
        OBSTACLE;
    }
    
    private int x,y;
    private int rootdstart;
    private Type                        type;
    private State                       state;
    private Direction                   cameFrom;
    
    public Node(boolean obstacle, int x, int y) {
    
        this.type = obstacle ? Type.OBSTACLE : Type.NORMAL;
        reset();
        this.x = x;
        this.y = y;
    }
    
    
    
    public void setObstacle(boolean bool) {
        this.type = bool ? Type.OBSTACLE : Type.NORMAL;
    }
    
    public void reset() {
        state = State.UNVISITED;
        cameFrom = null;
    }
    
    public void setDStart(int dstart) {
        this.rootdstart = dstart;
    }
    
    public int getX() {
        
        return x;
    }
    
    public int getY() {
        
        return y;
    }
    
    public State getState() {
        
        return state;
    }
    
    public void setState(boolean open) {
    
        this.state = open ? State.OPEN : State.CLOSED;
    }
    
    public int getRootDStart() {
        
        return rootdstart;
    }
    
    public void setCameFrom(Direction wayTo) {
        
        this.cameFrom = wayTo.otherWay();
    }
    
    public Direction getCameFrom() {
        
        return cameFrom;
    }
    
    public boolean isVisited() {
        return state != State.UNVISITED;
    }
    
    public boolean isObstacle() {
        return type == Type.OBSTACLE;
    }
    
    @Override
    public String toString() {
        
        return "Node[" + x + "|" + y + "]";
    }
}
