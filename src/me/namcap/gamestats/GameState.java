package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import me.namcap.assets.Textures;
import me.namcap.util.ConnectedTextures;
import me.namcap.util.Direction;
import me.namcap.util.pathfinding.PathFinder;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.game.MapLoader;
import me.namcap.game.entities.Ghost;
import me.namcap.game.entities.Player;
import me.namcap.main.Config;

public class GameState implements IGamestate {
    
    private static GameState currentInstance;
    private boolean gameWon;
    
    public static Dimension getSize() {
        return currentInstance.size;
    }
    
    Map mapObject;
    ConnectedTextures connectedTextures;
    DataToObject[][] map;
    Dimension size;
    Player p;
    
    ArrayList<Ghost> ghosts;
    private boolean gameOver;
    private int coins = 0;
    
    public GameState() {
        currentInstance = this;
        mapObject = MapLoader.getRandomMap();
        map = mapObject.getMap();
        connectedTextures = new ConnectedTextures(mapObject);
        size = new Dimension(map.length * Config.blocksize, map[0].length * Config.blocksize);
        ghosts = new ArrayList<>();
        findPlayerAndGhosts();
        updateMap(mapObject);
    }
    
    GameState(Object ignored) {}
    
    private void updateMap(Map map) {
        if(Ghost.finder == null || !Ghost.finder.uses(map)) {
            Ghost.finder = new PathFinder(map);
        }
    }
    
    private void findPlayerAndGhosts() {
        p = new Player(this, mapObject);
        for (int x = 0 ; x < map.length ; x++) {
            for (int y = 0 ; y < map[0].length ; y++) {
                if (map[x][y].equals(DataToObject.PLAYER)) {
                    map[x][y] = DataToObject.NONE;
                    if (p.getX() == 0 && p.getY() == 0) {
                        p.setPosition(x,y);
                    }
                } else if (map[x][y].equals(DataToObject.GHOST)) {
                    map[x][y] = DataToObject.NONE;
                    ghosts.add(new Ghost(this, x, y, p, mapObject));
                } else if (map[x][y].equals(DataToObject.DOOR)) {
                    map[x][y] = DataToObject.NONE;
                } else if (map[x][y].equals(DataToObject.COIN) || map[x][y].equals(DataToObject.BITCOIN)) {
                    coins++;
                }
            }
        }
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
    private long time = 0L;
    
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
        if(System.currentTimeMillis() < time + 1000) {
            return false;
        }
        
        p.update();
        int px = bounds(Math.round(p.getX()), map.length);
        int py = bounds(Math.round(p.getY()), map[0].length);
        processCoin(DataToObject.COIN, px, py);
        processCoin(DataToObject.BITCOIN, px, py);
        ghosts.forEach(Ghost::update);
        
        checkCollisions();
        
        return false;
    }
    
    private void checkCollisions() {
        int px = p.getIX();
        int py = p.getIY();
        Direction dir = p.getDirection();
        int pdx = px + dir.getDx();
        int pdy = py + dir.getDy();
        for(Ghost g : ghosts) {
            int gx = g.getIX();
            int gy = g.getIY();
            if(pdx == gx && pdy == gy) {
                processCollision(g);
            }
            Direction d = g.getDirection();
            int gdx = gx + d.getDx();
            int gdy = gx + d.getDy();
            if(gdx == px && gdy == py) {
                processCollision(g);
            }
        }
    }
    
    private void processCollision(Ghost g) {
        if(g.isReturning()) {
            return;
        }
        if(g.isVincible()) {
            g.kill();
        } else {
            kill();
        }
    }
    
    private void processCoin(DataToObject coin, int px, int py) {
        if(map[bounds(px, map.length-1)][bounds(py, map[0].length-1)] == coin) {
            map[bounds(px, map.length-1)][bounds(py, map[0].length-1)] = DataToObject.NONE;
            coins--;
            if (coins == 0) {
                gameOver = true;
                gameWon = true;
            }
            if(coin == DataToObject.BITCOIN) {
                ghosts.forEach(Ghost :: setVincible);
            }
        }
    }
    
    private int bounds(int value, int max) {
        if(value < 0) {
            value = max;
        }
        if(value > max) {
            value = 0;
        }
        return value;
    }
    
    @Override
    public void draw(Graphics g) {
        
        drawMap(g);
        drawPlayer(g);
        drawGhosts(g);
    }
    
    void drawMap(Graphics g) {
        for (int x = 0 ; x < map.length ; x++) {
            for (int y = 0 ; y < map[0].length ; y++) {
                draw(g, x, y, map[x][y]);
            }
        }
    }
    
    private void drawGhosts(Graphics g) {
        for(Ghost ghost : ghosts) {
    
            int x = (int) (ghost.getX() * Config.blocksize);
            int y = (int) (ghost.getY() * Config.blocksize);
            drawOverlap(g, ghost.getImage(), x, y, g.getClipBounds().width, g.getClipBounds().height);
            drawOverlap(g, ghost.getEye(), x, y, g.getClipBounds().width, g.getClipBounds().height);
        }
    }
    
    private void drawPlayer(Graphics g) {
        
        int x = (int) (p.getX() * Config.blocksize);
        int y = (int) (p.getY() * Config.blocksize);
        BufferedImage texture = p.getImage();
        drawOverlap(g, texture, x, y, g.getClipBounds().width, g.getClipBounds().height);
    }
    
    private void drawOverlap(Graphics g, BufferedImage texture, int x, int y, int width, int height) {
        if(x < 0) {
            int cx = width + x;
            g.drawImage(texture, cx, y, Config.blocksize, Config.blocksize, null);
        }
        if(y < 0) {
            int cy = height + y;
            g.drawImage(texture, x, cy, Config.blocksize, Config.blocksize, null);
        }
        if(x < 0 && y < 0) {
            int cx = width + x;
            int cy = height + y;
            g.drawImage(texture, cx, cy, Config.blocksize, Config.blocksize, null);
        }
        if(x + Config.blocksize  > width) {
            int cx = x - width;
            g.drawImage(texture, cx, y, Config.blocksize, Config.blocksize, null);
        }
        if(y + Config.blocksize > height) {
            int cy = y - height;
            g.drawImage(texture, x, cy, Config.blocksize, Config.blocksize, null);
        }
        if(x + Config.blocksize > width && y + Config.blocksize  > height) {
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
            if (object == DataToObject.DOOR) {
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
            } else {
                texture = connectedTextures.getImage(x, y);
            }
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
    
    private int lastBlocksize = 0;
    
    @Override
    public Dimension getPreferredSize() {
    
        if(Config.blocksize != lastBlocksize) {
            size = new Dimension(map.length * Config.blocksize, map[0].length * Config.blocksize);
            lastBlocksize = Config.blocksize;
        }
        return size;
    }
    
    @Override
    public IGamestate nextState() {
        
        if(gameOver) {
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
