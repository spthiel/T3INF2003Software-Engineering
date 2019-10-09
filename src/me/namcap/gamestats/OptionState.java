package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;

import me.namcap.assets.Fonts;

import static me.namcap.gamestats.Option.Type.*;

public class OptionState extends MenuState {
    
    private GameState nextGame;
    private MenuState menu;
    private boolean back;
    
    public OptionState(MenuState before, GameState state) {
        super(state);
        nextGame = state;
        menu = before;
    }
    
    @Override
    public IGamestate nextState() {
        if(back) {
            back = false;
            return menu;
        }
        return this;
    }
    
    @Override
    public boolean update() {
        return false;
    }
    
    
    @Override
    public void draw(Graphics g) {
    
        super.draw(g);
    }
    
    private Option[] fields = {
            new Option("Max UPS", "maxups", INT, 20, 60, 5).setInstantupdate(),
            new Option("Max FPS", "maxfps", INT, 20, 500, 5).setInstantupdate(),
            new Option("Blocksize", "blocksize", INT, 20,40).setInstantupdate(),
            new Option("Font", "font", FONT).setInstantupdate().onUpdate(this::onFontChange),
            new Option("Difficulty", "difficulty", DIFFICULTY).setInstantupdate(),
            new Option("Velocity", "VELOCITY", FLOAT, 0.05f, 1f, 0.05f).setInstantupdate(),
            new Option("Back")
    };
    
    private void onFontChange(Object object) {
        if(!(object instanceof Fonts)) {
            throw new RuntimeException("Illegal callback for onFontChange: " + object);
        }
        updateFont();
    }
    
    @Override
    void drawStrings(Graphics g, String... ignored) {
    
        
        String[] strings = new String[fields.length];
        for (int i = 0 ; i < fields.length ; i++) {
            strings[i] = fields[i].getKey();
        }
        Rectangle rect = g.getClipBounds();
        int centerx = (int)rect.getWidth()/2;
        int centery = (int)rect.getHeight()/2;
        
        boolean drawTriangle = ((frame%(2*framesPerFrame))/framesPerFrame) == 0;
        int[] lengths = getStringLengths(strings);

        int starty = (int)((height+5) * strings.length/2f);
        starty = centery - starty + metrics.getAscent();
    
        for (int i = 0 ; i < strings.length ; i++) {
            boolean drawTri = drawTriangle && i == selected;
            if(i == selected) {
                g.setColor(Color.getHSBColor(colorframe/360f, 1, 1));
            } else {
                g.setColor(Color.YELLOW);
            }
            String s      = strings[i];
            int startx;
            if(!fields[i].getType().equals(BACK)) {
                startx = centerx - lengths[i] - 10;
            } else {
                startx = centerx - lengths[i]/2;
            }
            
            fields[i].draw(g, centerx + 10, starty);
            
            g.drawString(s, startx, starty);
            if(drawTri) {
                int size = metrics.stringWidth(">");
                g.drawString(">", startx-size-5, starty);
            }
            starty += height + 5;
        }
        
    }
    
    @Override
    public void onKey(int key) {
        if(key == 2) {
            fields[selected].update();
            selected++;
            if(selected >= fields.length) {
                selected = 0;
            }
            frame = 0;
        } else if(key == 0) {
            selected--;
            if (selected < 0) {
                selected = fields.length - 1;
            }
            frame = 0;
        } else if(key == 1) {
            fields[selected].next();
        } else if(key == 3) {
            fields[selected].previous();
        } else if(key == 4) {
            if(selected == fields.length-1) {
                back = true;
            }
        }
    }
}
