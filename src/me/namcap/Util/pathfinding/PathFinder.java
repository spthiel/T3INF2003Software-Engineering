package me.namcap.Util.pathfinding;

import java.util.*;
import java.util.function.Consumer;

import me.namcap.Util.Direction;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.game.entities.Entity;
import me.namcap.game.entities.Ghost;

public class PathFinder {
    
    private Node[][] all;
    private Set<Node> ghostNodes;
    private DataToObject[][] map;
    
    public PathFinder(Map map) {
        ghostNodes = new HashSet<>();
        this.map = map.getMap();
        all = new Node[map.getWidth()][map.getHeight()];
        for(int x = 0; x < map.getWidth(); x++) {
            for(int y = 0; y < map.getHeight(); y++) {
                DataToObject object = this.map[x][y];
                all[x][y] = new Node(object == DataToObject.WALL || object == DataToObject.DOOR, x, y);
            }
        }
    }
    
    public void setGhosts(List<Ghost> ghosts) {
    
        clearGhosts();
        
        for (Ghost ghost : ghosts) {
            int x = ghost.getIX();
            int y = ghost.getIY();
            all[x][y].setObstacle(true);
            ghostNodes.add(all[x][y]);
        }
    }
    
    public void clearGhosts() {
        for(Node n : ghostNodes) {
            n.setObstacle(false);
        }
    }
    
    public void reset() {
        long start = System.nanoTime();
        for (Node[] nodes : all) {
            for (Node node : nodes) {
                node.reset();
            }
        }
        long end = System.nanoTime();
        long ms = (end-start);
//        System.out.println("Took: " + ms);
//        forEach(Node::reset);
    }
    
    public void forEach(Consumer<Node> consumer) {
        long start = System.nanoTime();
        for (Node[] nodes : all) {
            for (Node node : nodes) {
                consumer.accept(node);
            }
        }
        long end = System.nanoTime();
        long ms = (end-start);
        System.out.println("Took: " + ms);
    }
    
    public boolean uses(Map map) {
        return map.getMap().equals(this.map);
    }
    
    public int find(Entity e1, Entity e2) {
        return find(e1.getIX(), e1.getIY(), e2.getIX(), e2.getIY());
    }
    
    public int find(int startx, int starty, int endx, int endy) {
        Queue<Node> nodes = new LinkedList<>();
     
        if(outOfBounds(startx, starty) || outOfBounds(endx,endy)) {
            return -1;
        }
        
        if(startx == endx && starty == endy) {
            return -2;
        }
        
        Node start = all[startx][starty];
        nodes.add(start);
        start.setState(true);
        start.setDStart(0);
        
        int i = 0;
        
        while(!nodes.isEmpty()) {
            i++;
            Node current = nodes.remove();
            current.setState(false);
            for(Direction d : Direction.values()) {
                int x = current.getX() + d.getDx();
                int y = current.getY() + d.getDy();
                
                x = bounds(x, 0, all.length-1);
                y = bounds(y, 0, all[0].length-1);
                
                if(x == endx && y == endy) {
                    if(current == start) {
                        return d.getValue();
                    }
                    return backtrack(current, start).getValue();
                }
                
                Node n = all[x][y];
                
                if(n.isObstacle() || n.isVisited()) {
                    continue;
                }
                n.setCameFrom(d);
                nodes.add(n);
            }
            
        }
        return -1;
    }
    
    private Direction backtrack(Node n, Node start) {
        Direction last = n.getCameFrom();
        while(n != start) {
            int x = n.getX() + n.getCameFrom().getDx();
            int y = n.getY() + n.getCameFrom().getDy();
            x = bounds(x, 0, all.length-1);
            y = bounds(y, 0, all[0].length-1);
            if(n.getCameFrom() != null) {
                last = n.getCameFrom();
            }
            n = all[x][y];
        }
        return last.otherWay();
    }
    
    public boolean inBounds(int x, int y) {
        return !outOfBounds(x,y);
    }
    
    public boolean outOfBounds(int x, int y) {
    
        if (x < 0 || x >= all.length) {
            return true;
        }
        return y < 0 || y >= all[0].length;
    }
    
    private char map(Direction d) {
        if(d == null) {
            return ' ';
        }
        switch(d) {
            case WEST:
                return '<';
            case NORTH:
                return '^';
            case EAST:
                return '>';
            case SOUTH:
                return 'V';
            default:
                return ' ';
        }
    }
    
    private int bounds(int value, int min, int max) {
        if(value < min) {
            value = max;
        }
        if(value > max) {
            value = min;
        }
        return value;
    }
    
}
