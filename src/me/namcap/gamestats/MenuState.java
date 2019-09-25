package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import me.namcap.Textures.Fonts;
import me.namcap.main.Config;

public class MenuState implements IGamestate {
    
    private GameState nextGame = new GameState();
    private IGamestate nextState = this;
    
    public MenuState() {
    
    }
    
    @Override
    public boolean update() {
        return false;
    }
    
    private FontMetrics metrics;
    private int height;
    private int frame = 0;
    private int colorframe = 0;
    private static final int framesPerFrame = 50;
    
    private int selected = 0;
    private String[] fields = {"Start", "Options", "Quit"};
    
    private static final Color gray = new Color(0,0,0,.4f);
    
    private static Font font = Config.font.deriveFont(Font.BOLD, 40f);
    
    @Override
    public void draw(Graphics g) {
    
        advanceFrames();
        
        nextGame.drawMap(g);
        Rectangle rect = g.getClipBounds();
        g.setColor(gray);
        g.fillRect(0, 0, (int)rect.getWidth(), (int)rect.getHeight());
        
        g.setFont(font);
        metrics = g.getFontMetrics();
        height = metrics.getHeight();
        drawStrings(g, fields);
    }
    
    private void advanceFrames() {
        frame++;
        if(frame >= 1000000) {
            frame = 0;
        }
        colorframe++;
        if(colorframe >= 360) {
            colorframe = 0;
        }
    }
    
    private void drawStrings(Graphics g, String... strings) {
    
        Rectangle rect = g.getClipBounds();
        int centerx = (int)rect.getWidth()/2;
        int centery = (int)rect.getHeight()/2;
        
        boolean drawTriangle = ((frame%(2*framesPerFrame))/framesPerFrame) == 0;
        int[] lengths = getStringLengths(strings);
        
        int starty = (int)((height+5) * strings.length/2f);
        starty = centery - starty;
        
        for (int i = 0 ; i < strings.length ; i++) {
            boolean drawTri = drawTriangle && i == selected;
            if(i == selected) {
                g.setColor(Color.getHSBColor(colorframe/360f, 1, 1));
            } else {
                g.setColor(Color.YELLOW);
            }
            String s      = strings[i];
            int    startx = centerx - lengths[i]/2;
            g.drawString(s, startx, starty);
            if(drawTri) {
                int size = metrics.stringWidth(">");
                g.drawString(">", startx-size-5, starty);
            }
            starty += height + 5;
        }
        
    }
    
    private int[] getStringLengths(String[] strings) {
    
        int[] lengths = new int[strings.length];
        for (int i = 0 ; i < strings.length; i++) {
            lengths[i] = metrics.stringWidth(strings[i]);
        }
        return lengths;
    }
    
    @Override
    public Dimension getPreferredSize() {
        
        return nextGame.size;
    }
    
    @Override
    public IGamestate nextState() {
        
        return nextState;
    }
    
    private int f = 0;
    
    public void onKey(int key) {
        if(key == 2) {
            selected++;
            if(selected >= fields.length) {
                selected = 0;
            }
            frame = 0;
        } else if(key == 0) {
            selected--;
            if(selected < 0) {
                selected = fields.length-1;
            }
            frame = 0;
        } else if(key == 4 || key == 1) {
            if(selected == 0) {
                nextState = nextGame;
            } else if(selected == 1) {
                nextState = new OptionState();
            } else if(selected == 2) {
                System.exit(0);
            }
        } else if(key == 3) {
             f++;
             if(f >= Fonts.values().length) {
                 f = 0;
             }
             font = Fonts.values()[f].deriveFont(Font.BOLD, 40);
             Config.font = Fonts.values()[f];
        }
    }
    
    private boolean[] keyStates = new boolean[5];
    
    @Override
    public void keyTyped(KeyEvent e) {
    
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
     
        int dir = translateKeyCode(e.getKeyCode());
        if(dir >= 0 && !keyStates[dir]) {
            keyStates[dir] = true;
            onKey(dir);
        }
        
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    
        int dir = translateKeyCode(e.getKeyCode());
    
        if(dir >= 0 && keyStates[dir]) {
            keyStates[dir] = false;
        }
    }
    
    private int translateKeyCode(int keycode) {
    
        int dir = -1;
        switch (keycode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                dir = 0;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                dir = 1;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                dir = 2;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                dir = 3;
                break;
            case KeyEvent.VK_ENTER:
                dir = 4;
                break;
        }
        return dir;
    }
}
