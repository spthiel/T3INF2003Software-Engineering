package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import me.namcap.Textures.Textures;
import me.namcap.Util.ConnectedTextures;
import me.namcap.game.DataToObject;
import me.namcap.game.Map;
import me.namcap.game.MapLoader;
import me.namcap.game.entities.Ghost;
import me.namcap.game.entities.Player;
import me.namcap.main.Constants;

public class GameState implements IGamestate {
    
    private Map mapObject;
    private ConnectedTextures connectedTextures;
    private DataToObject[][] map;
    private Dimension size;
    private Player p;
    
    private ArrayList<Ghost> ghosts;
    
    public GameState() {
        
        mapObject = MapLoader.getRandomMap();
        map = mapObject.getMap();
        connectedTextures = new ConnectedTextures(mapObject);
        size = new Dimension(map.length * Constants.blocksize, map[0].length * Constants.blocksize);
        ghosts = new ArrayList<>();
        findPlayerAndGhosts();
    }
    
    private void findPlayerAndGhosts() {
        
        for (int x = 0 ; x < map.length ; x++) {
            for (int y = 0 ; y < map[0].length ; y++) {
                if (map[x][y].equals(DataToObject.PLAYER)) {
                    map[x][y] = DataToObject.NONE;
                    if (p == null) {
                        p = new Player(this::isGhostAt, x, y, mapObject);
                    }
                } else if (map[x][y].equals(DataToObject.GHOST)) {
                    map[x][y] = DataToObject.NONE;
                    ghosts.add(new Ghost(this::isGhostAt, x, y, mapObject));
                } else if (map[x][y].equals(DataToObject.DOOR)) {
                    map[x][y] = DataToObject.NONE;
                }
            }
        }
        if (p == null) {
            p = new Player(mapObject);
        }
    }
    
    public boolean isGhostAt(int x, int y) {
        return ghosts.stream().anyMatch(ghost -> ghost.getX() == x && ghost.getY() == y);
    }
    
    @Override
    public void update() {
        p.update();
        int px = bounds(Math.round(p.getX()), 0, map.length);
        int py = bounds(Math.round(p.getY()), 0, map[0].length);
        if(map[bounds(px, 0, map.length-1)][bounds(py, 0, map[0].length-1)] == DataToObject.COIN) {
            map[bounds(px, 0, map.length-1)][bounds(py, 0, map[0].length-1)] = DataToObject.NONE;
            System.out.println("Coin");
        }
        ghosts.forEach(Ghost::update);
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
    
    @Override
    public void draw(Graphics g) {
        
        for (int x = 0 ; x < map.length ; x++) {
            for (int y = 0 ; y < map[0].length ; y++) {
                draw(g, x, y, map[x][y]);
            }
        }
        drawPlayer(g);
        drawGhosts(g);
    }
    
    private void drawGhosts(Graphics g) {
        for(Ghost ghost : ghosts) {
    
            int x = (int) (ghost.getX() * Constants.blocksize);
            int y = (int) (ghost.getY() * Constants.blocksize);
            drawOverlap(g, ghost.getImage(), x, y, g.getClipBounds().width, g.getClipBounds().height);
            drawOverlap(g, ghost.getEye(), x, y, g.getClipBounds().width, g.getClipBounds().height);
        }
        
    }
    
    private void drawPlayer(Graphics g) {
        
        int x = (int) (p.getX() * Constants.blocksize);
        int y = (int) (p.getY() * Constants.blocksize);
        BufferedImage texture = p.getImage();
        drawOverlap(g, texture, x, y, g.getClipBounds().width, g.getClipBounds().height);
    }
    
    private void drawOverlap(Graphics g, BufferedImage texture, int x, int y, int width, int height) {
        if(x < 0) {
            int cx = width + x;
            g.drawImage(texture, cx, y, Constants.blocksize, Constants.blocksize, null);
        }
        if(y < 0) {
            int cy = height + y;
            g.drawImage(texture, x, cy, Constants.blocksize, Constants.blocksize, null);
        }
        if(x < 0 && y < 0) {
            int cx = width + x;
            int cy = height + y;
            g.drawImage(texture, cx, cy, Constants.blocksize, Constants.blocksize, null);
        }
        if(x + Constants.blocksize  > width) {
            int cx = x - width;
            g.drawImage(texture, cx, y, Constants.blocksize, Constants.blocksize, null);
        }
        if(y + Constants.blocksize > height) {
            int cy = y - height;
            g.drawImage(texture, x, cy, Constants.blocksize, Constants.blocksize, null);
        }
        if(x + Constants.blocksize > width && y + Constants.blocksize  > height) {
            int cx = x - width;
            int cy = y - height;
            g.drawImage(texture, cx, cy, Constants.blocksize, Constants.blocksize, null);
        }
        g.drawImage(texture, x, y, Constants.blocksize, Constants.blocksize, null);
    }
    
    private void draw(Graphics g, int x, int y, DataToObject object) {
        
        int ax = x * Constants.blocksize;
        int ay = y * Constants.blocksize;
        
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
            g.drawImage(texture, ax, ay, Constants.blocksize, Constants.blocksize, null);
            return;
        }
        
        if (object == DataToObject.NONE) {
            return;
        }
        
        if (object == DataToObject.COIN || object == DataToObject.BITCOIN) {
            int size;
            if (object == DataToObject.COIN) {
                g.setColor(Constants.coin);
                size = Constants.coinsize;
            } else {
                g.setColor(Constants.bitcoin);
                size = Constants.bitcoinsize;
            }
            g.fillOval(ax + Constants.blocksize / 2 - size / 2, ay + Constants.blocksize / 2 - size / 2, size, size);
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        
        return size;
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
