package me.namcap.gamestats;

import java.awt.*;

import me.namcap.game.DataToObject;
import me.namcap.game.MapLoader;
import me.namcap.main.Constants;

public class GameState implements IGamestate {
    
    private DataToObject[][] map;
    private Dimension size;
    
    public GameState() {
        map = MapLoader.getRandomMap().getMap();
        size = new Dimension(map.length * Constants.blocksize, map[0].length * Constants.blocksize);
    }
    
    @Override
    public void update() {
    
    }
    
    @Override
    public void draw(Graphics g) {
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                switch (map[x][y]) {
                    case NONE:
                        continue;
                    case WALL:
                        g.setColor(Color.gray);
                        break;
                    case DOOR:
                        g.setColor(Color.cyan);
                        break;
                    case GHOST:
                        g.setColor(Color.green);
                        break;
                    case PLAYER:
                        g.setColor(Color.blue);
                        break;
                    case BITCOIN:
                        g.setColor(Color.yellow);
                        break;
                    case COIN:
                        g.setColor(Color.orange);
                        break;
                    default:
                        continue;
                }
                g.fillRect(x * Constants.blocksize, y * Constants.blocksize, Constants.blocksize, Constants.blocksize);
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        
        return size;
    }
}
