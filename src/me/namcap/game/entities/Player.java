package me.namcap.game.entities;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import me.namcap.assets.Textures;
import me.namcap.util.Direction;
import me.namcap.game.Map;
import me.namcap.gamestats.GameState;
import me.namcap.main.Config;

public class Player extends Entity implements KeyListener {
    
    public Player(Map map) {
        super(map);
    }
    
    public Player(GameState state, Map map) {
        this(state, 0, 0, map);
    }
    
    public Player(GameState state, int x, int y, Map map) {
        super(state, Config.VELOCITY, x, y, map);
    }
    
    @Override
    public BufferedImage getImage() {
    
        return Textures.NAMCAP.getNamCap(direction.getValue(),frame/20);
    }
    
    private int frame = 0;
    
    @Override
    public boolean update() {
        
        super.update();
        setVelocity(Config.VELOCITY);
        frame++;
        if(frame > 40) {
            frame = 0;
        }
        return false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    
        //TODO: KeyStates
        
        boolean changed = true;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                tryDir = Direction.NORTH;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                tryDir = Direction.EAST;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                tryDir = Direction.SOUTH;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                tryDir = Direction.WEST;
                break;
            default:
                changed = false;
        }
        if(changed && !checkBlock(tryDir)) {
            if(stop) {
                direction = tryDir;
            }
            if(direction.isOtherway(tryDir)) {
    
                x += direction.getDx();
                y += direction.getDy();
                progress = 1-progress;
                direction = tryDir;
            }
            stop = false;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    
    }
}
