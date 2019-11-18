package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;

import me.namcap.assets.Textures;
import me.namcap.game.entities.Door;
import me.namcap.assets.ConnectedTextures;
import me.namcap.util.Direction;
import me.namcap.util.Vec2I;
import me.namcap.util.Vec2O;
import me.namcap.util.pathfinding.PathFinder;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.game.MapLoader;
import me.namcap.game.entities.Ghost;
import me.namcap.game.entities.Player;
import me.namcap.main.Config;

import static me.namcap.util.Util.bounds;

public class GameState implements IGamestate {
    
    private static GameState currentInstance;
    private        boolean   gameWon;
    
    public static Dimension getSize() {
        
        return currentInstance.size;
    }
    
    Map               mapObject;
    ConnectedTextures connectedTextures;
    DataToObject[][]  map;
    Dimension         size;
    Player            p;
    
    ArrayList<Ghost> ghosts;
    private boolean gameOver;
    private int     coins = 0;
    
    private Queue<Door> doors;
    private ArrayList<Door> validDoors;
    private ArrayList<Door> filledDoors;
    
    public GameState() {
        
        currentInstance = this;
        mapObject = MapLoader.getRandomMap();
        map = mapObject.getMap();
        debug = new Color[map.length][map[0].length];
        size = new Dimension(map.length * Config.blocksize, map[0].length * Config.blocksize);
        ghosts = new ArrayList<>();
        doors = new LinkedList<>();
        validDoors = new ArrayList<>();
        filledDoors = new ArrayList<>();
        findPlayerAndGhosts();
        setupDoorAI();
        updateMap(mapObject);
    }
    
    GameState(Object ignored) {
    
    }
    
    private void updateMap(Map map) {
        
        if (Ghost.finder == null || !Ghost.finder.uses(map)) {
            Ghost.finder = new PathFinder(map);
        }
    }
    
    private void setupDoorAI() {
    
        System.out.println(doors.size());
        HashMap<Door, DataToObject> replaceTable = new HashMap<>();
        while (!doors.isEmpty()) {
            Door d = doors.remove();
            if (!d.isValid()) {
                replaceTable.put(d, DataToObject.WALL);
                continue;
            }
            Queue<Ghost> ghosts = getLockedGhosts(d);
            if(d.isValid()) {
                d.setLockedGhosts(ghosts);
                validDoors.add(d);
                if(d.size() > 0) {
                    filledDoors.add(d);
                }
                replaceTable.put(d, DataToObject.NONE);
            } else {
                replaceTable.put(d, DataToObject.WALL);
            }
        }
        
        replaceTable.forEach((d,o) -> {
            if(o.equals(DataToObject.WALL)) {
                changeDoorTo(d,o);
            }
        });
    
        connectedTextures = new ConnectedTextures(mapObject);
    
        replaceTable.forEach((d,o) -> {
            if(o.equals(DataToObject.NONE)) {
                changeDoorTo(d,o);
            }
        });
    }
    
    private void changeDoorTo(Door d, DataToObject newTile) {
        map[d.getX()][d.getY()] = newTile;
    }
    
    private Queue<Ghost> getLockedGhosts(Door door) {
    
        Queue<Ghost> oneSide = new LinkedList<>();
        Queue<Ghost> otherSide = new LinkedList<>();
        Node[][] nodes   = new Node[map.length][map[0].length];
        Queue<Node>      toCheck = new LinkedList<>();
        Vec2O<Vec2I> sides = door.getSides();
        {
            Node n = new Node(sides.x1.x1, sides.x1.x2, State.FLOOD1);
            nodes[n.x][n.y] = n;
            toCheck.add(n);
            Ghost g = getGhostAt(n.x, n.y);
            if (g != null) {
                oneSide.add(g);
            }
        }
        {
            Node n = new Node(sides.x2.x1, sides.x2.x2, State.FLOOD2);
            nodes[n.x][n.y] = n;
            toCheck.add(n);
            Ghost g = getGhostAt(n.x,n.y);
            if(g != null) {
                otherSide.add(g);
            }
        }
        int pacOn = 0;
        while(!toCheck.isEmpty()) {
            Node n = toCheck.remove();
            if(pacOn != 0) {
                if(pacOn == 1 && n.state.equals(State.FLOOD1)) {
                    continue;
                } else if(pacOn == 2 && n.state.equals(State.FLOOD2)) {
                    continue;
                }
            }
            
            for(Direction d : Direction.values()) {
                int nx = bounds(n.x + d.getDx(),map.length-1);
                int ny = bounds(n.y + d.getDy(),map[0].length-1);
                
                if(nodes[nx][ny] != null) {
                    Node node = nodes[nx][ny];
                    if(node.state.equals(State.WALL)) {
                        continue;
                    } else if(!node.state.equals(n.state)) {
                        door.setInvalid();
                        return null;
                    }
                    continue;
                }
                if(map[nx][ny].equals(DataToObject.WALL)) {
                    nodes[nx][ny] = new Node(nx, ny, State.WALL);
                    continue;
                }
                if(map[nx][ny].equals(DataToObject.DOOR)) {
                    nodes[nx][ny] = new Node(nx, ny, State.WALL);
                    if(!door.isMe(nx,ny)) {
                        for (Door door1 : doors) {
                            if (door1.isMe(nx,ny)) {
                                door1.setInvalid();
                                break;
                            }
                        }
                        map[nx][ny] = DataToObject.WALL;
                    }
                    continue;
                }
                Node node = new Node(nx, ny, n.state);
                nodes[nx][ny] = node;
                toCheck.add(node);
                if(p.getIX() == nx && p.getIY() == ny) {
                    switch (n.state) {
                        case FLOOD2:
                            pacOn++;
                        case FLOOD1:
                            pacOn++;
                            break;
                    }
                }
                Ghost g = getGhostAt(nx,ny);
                if(g != null) {
                    if(n.state.equals(State.FLOOD1)) {
                        oneSide.add(g);
                    } else {
                        otherSide.add(g);
                    }
                }
            }
        }
        switch (pacOn) {
            case 0:
                for (Ghost g : oneSide) {
                    ghosts.remove(g);
                }
                for (Ghost g : otherSide) {
                    ghosts.remove(g);
                }
                door.setInvalid();
                return null;
            case 1:
                return otherSide;
            default:
                return oneSide;
        }
    }
    
    private enum State {
        FLOOD1,
        FLOOD2,
        WALL;
    }
    
    private static class Node {
        
        private State state;
        private int x, y;
        
        public Node(int x, int y, State state) {
            this.x = x;
            this.y = y;
            this.state = state;
        }
    
        @Override
        public String toString() {
        
            return String.format("Node[x:%d,y:%d,state:%s]", this.x, this.y, this.state.name());
        }
    }
    
    private void findPlayerAndGhosts() {
        
        p = new Player(this, mapObject);
        for (int x = 0 ; x < map.length ; x++) {
            for (int y = 0 ; y < map[0].length ; y++) {
                if (map[x][y].equals(DataToObject.PLAYER)) {
                    map[x][y] = DataToObject.NONE;
                    if (p.getX() == 0 && p.getY() == 0) {
                        p.setPosition(x, y);
                    }
                } else if (map[x][y].equals(DataToObject.GHOST)) {
                    map[x][y] = DataToObject.NONE;
                    ghosts.add(new Ghost(this, x, y, p, mapObject));
                } else if (map[x][y].equals(DataToObject.DOOR)) {
                    doors.add(new Door(x, y, map));
                } else if (map[x][y].equals(DataToObject.COIN) || map[x][y].equals(DataToObject.BITCOIN)) {
                    coins++;
                }
            }
        }
    }
    
    public void changeDifficulty() {
        ghosts.forEach(Ghost::changeDifficulty);
    }
    
    public void kill() {
        
        gameOver = true;
    }
    
    public boolean isGhostAt(int x, int y) {
        
        for (Ghost ghost : ghosts) {
            if (ghost.getX() == x && ghost.getY() == y) {
                return true;
            }
        }
        return false;
    }
    
    public Ghost getGhostAt(int x, int y) {
        
        for (Ghost ghost : ghosts) {
            if (ghost.getX() == x && ghost.getY() == y) {
                return ghost;
            }
        }
        return null;
    }
    
    private boolean first = true;
    private long    time  = 0L;
    
    private long frames = 0L;
    private boolean initialRelease;
    
    @Override
    public boolean update() {
        
        if (gameOver) {
            return false;
        }
        if (first) {
            time = System.currentTimeMillis();
            first = false;
            return true;
        }
        if (System.currentTimeMillis() < time + 1000) {
            return false;
        }
        
        frames++;
    
        if(initialRelease && (frames-Config.releaseDelay)%Config.releaseTime == 0) {
            releaseGhost();
        }
        
        if(frames > Config.releaseDelay && !initialRelease) {
            releaseGhost();
            initialRelease = true;
        }
        
        if(Config.difficulty == Config.Difficulty.MEDIUM) {
            checkVisibility();
        }
        
        p.update();
        int px = bounds(Math.round(p.getX()), map.length - 1);
        int py = bounds(Math.round(p.getY()), map[0].length - 1);
        processCoin(DataToObject.COIN, px, py);
        processCoin(DataToObject.BITCOIN, px, py);
        ghosts.forEach(Ghost :: update);
        
        checkCollisions();
        
        return false;
    }
    
    private void checkVisibility() {
        int px = p.getIX();
        int py = p.getIY();
        for(Direction d : Direction.values()) {
            checkDirection(d, px, py);
        }
    }
    
    private void checkDirection(Direction d, int x, int y) {
    
        int initialX = x, initialY = y;
        do {
            x += d.getDx();
            y += d.getDy();
            x = bounds(x, map.length - 1);
            y = bounds(y, map[0].length - 1);
            debug[x][y] = Color.CYAN;
            if(map[x][y] == DataToObject.WALL) {
                debug[x][y] = Color.MAGENTA;
                return;
            }
            Ghost g = getGhostAt(x,y);
            if(g != null) {
                g.seesPlayer();
            }
            for (Door door : validDoors) {
                if(door.isMe(x,y)) {
                    return;
                }
            }
            
        } while((x != initialX || y != initialY /* || d != initialD || running != 90 */));
    }
    
    private Random random = new Random();
    
    private void releaseGhost() {
        if(filledDoors.size() == 0) {
            return;
        }
        Door d = filledDoors.get(random.nextInt(filledDoors.size()));
        d.release();
        if(d.size() == 0) {
            filledDoors.remove(d);
        }
    }
    
    private void checkCollisions() {
        
        int       px  = p.getIX();
        int       py  = p.getIY();
        Direction dir = p.getDirection();
        int       pdx = px + dir.getDx();
        int       pdy = py + dir.getDy();
        for (Ghost g : ghosts) {
            int gx = g.getIX();
            int gy = g.getIY();
            if (pdx == gx && pdy == gy) {
                processCollision(g);
            }
            Direction d   = g.getDirection();
            int       gdx = gx + d.getDx();
            int       gdy = gy + d.getDy();
            if (gdx == px && gdy == py) {
                processCollision(g);
            }
        }
    }
    
    private void processCollision(Ghost g) {
        
        if (g.isReturning()) {
            return;
        }
        if (g.isVincible()) {
            g.kill();
        } else {
            kill();
        }
    }
    
    private void processCoin(DataToObject coin, int px, int py) {
        
        if (map[bounds(px, map.length - 1)][bounds(py, map[0].length - 1)] == coin) {
            map[bounds(px, map.length - 1)][bounds(py, map[0].length - 1)] = DataToObject.NONE;
            coins--;
            if (coins == 0) {
                gameOver = true;
                gameWon = true;
            }
            if (coin == DataToObject.BITCOIN) {
                ghosts.forEach(Ghost :: setVincible);
            }
        }
    }
    
    @Override
    public void draw(Graphics g) {
        
        drawMap(g);
        drawDoors(g);
        drawPlayer(g);
        drawGhosts(g);
    }
    
    private Color[][] debug;
    
    private void clearDebug() {
        for(int x = 0; x < debug.length; x++) {
            for(int y = 0; y < debug[0].length; y++) {
                debug[x][y] = null;
            }
        }
    }
    
    private void drawDebug(Graphics g) {
        for(int x = 0; x < debug.length; x++) {
            for(int y = 0; y < debug[0].length; y++) {
                if(debug[x][y] != null) {
                    g.setColor(debug[x][y]);
                    drawRect(g,x * Config.blocksize, y * Config.blocksize, Config.blocksize, Config.blocksize, 3);
                }
            }
        }
    }
    
    private void drawRect(Graphics g, int x, int y, int height, int width, int linewidth) {
    
        if ((width < 0) || (height < 0)) {
            return;
        }
    
        if (height == 0 || width == 0) {
            g.drawLine(x, y, x + width, y + height);
        } else {
            g.fillRect(x,y,width,linewidth);
            g.fillRect(x,y,linewidth,height);
            g.fillRect(x,y+height-linewidth,width,linewidth);
            g.fillRect(x+width-linewidth,y,linewidth,height);
        }
    }
    
    void drawMap(Graphics g) {
        
        for (int x = 0 ; x < map.length ; x++) {
            for (int y = 0 ; y < map[0].length ; y++) {
                draw(g, x, y, map[x][y]);
            }
        }
    }
    
    private void drawGhosts(Graphics g) {
        
        for (Ghost ghost : ghosts) {
            
            int x = (int) (ghost.getX() * Config.blocksize);
            int y = (int) (ghost.getY() * Config.blocksize);
            drawOverlap(g, ghost.getImage(), x, y, g.getClipBounds().width, g.getClipBounds().height);
            drawOverlap(g, ghost.getEye(), x, y, g.getClipBounds().width, g.getClipBounds().height);
        }
    }
    
    private void drawPlayer(Graphics g) {
        
        int           x       = (int) (p.getX() * Config.blocksize);
        int           y       = (int) (p.getY() * Config.blocksize);
        BufferedImage texture = p.getImage();
        drawOverlap(g, texture, x, y, g.getClipBounds().width, g.getClipBounds().height);
    }
    
    private void drawOverlap(Graphics g, BufferedImage texture, int x, int y, int width, int height) {
        
        if (x < 0) {
            int cx = width + x;
            g.drawImage(texture, cx, y, Config.blocksize, Config.blocksize, null);
        }
        if (y < 0) {
            int cy = height + y;
            g.drawImage(texture, x, cy, Config.blocksize, Config.blocksize, null);
        }
        if (x < 0 && y < 0) {
            int cx = width + x;
            int cy = height + y;
            g.drawImage(texture, cx, cy, Config.blocksize, Config.blocksize, null);
        }
        if (x + Config.blocksize > width) {
            int cx = x - width;
            g.drawImage(texture, cx, y, Config.blocksize, Config.blocksize, null);
        }
        if (y + Config.blocksize > height) {
            int cy = y - height;
            g.drawImage(texture, x, cy, Config.blocksize, Config.blocksize, null);
        }
        if (x + Config.blocksize > width && y + Config.blocksize > height) {
            int cx = x - width;
            int cy = y - height;
            g.drawImage(texture, cx, cy, Config.blocksize, Config.blocksize, null);
        }
        g.drawImage(texture, x, y, Config.blocksize, Config.blocksize, null);
    }
    
    private void draw(Graphics g, int x, int y, DataToObject object) {
        
        int ax = x * Config.blocksize;
        int ay = y * Config.blocksize;
        
        if (object == DataToObject.WALL || object == DataToObject.DOOR) {
            BufferedImage texture;
            texture = connectedTextures.getImage(x, y);
            g.drawImage(texture, ax, ay, Config.blocksize, Config.blocksize, null);
            return;
        }
        
        if (object == DataToObject.NONE) {
            return;
        }
        
        if (object == DataToObject.COIN || object == DataToObject.BITCOIN) {
            int size;
            if (object == DataToObject.COIN) {
                g.setColor(Config.coin);
                size = Config.coinsize;
            } else {
                g.setColor(Config.bitcoin);
                size = Config.bitcoinsize;
            }
            g.fillOval(ax + Config.blocksize / 2 - size / 2, ay + Config.blocksize / 2 - size / 2, size, size);
        }
    }
    
    private void drawDoors(Graphics g) {
    
        for(Door d : validDoors) {
            int x = d.getX();
            int y = d.getY();
            int ax = x * Config.blocksize;
            int ay = y * Config.blocksize;
            BufferedImage texture;
            switch (connectedTextures.get(x, y)) {
                case VERTICAL:
                    texture = Textures.VDOOR.get(0);
                    break;
                case HORIZONTAL:
                    texture = Textures.HDOOR.get(0);
                    break;
                default:
                    texture = null;
            }
            g.drawImage(texture, ax, ay, Config.blocksize, Config.blocksize, null);
        }
    }
    
    private int lastBlocksize = 0;
    
    @Override
    public Dimension getPreferredSize() {
        
        if (Config.blocksize != lastBlocksize) {
            size = new Dimension(map.length * Config.blocksize, map[0].length * Config.blocksize);
            lastBlocksize = Config.blocksize;
        }
        return size;
    }
    
    @Override
    public IGamestate nextState() {
        
        if (gameOver) {
            return new GameOverState(gameWon, this);
        }
        
        return this;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        
        p.keyTyped(e);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
        p.keyPressed(e);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
        p.keyPressed(e);
    }
}
