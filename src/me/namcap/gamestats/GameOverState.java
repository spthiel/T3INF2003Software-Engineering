package me.namcap.gamestats;

import java.awt.*;
import java.awt.event.KeyEvent;

import me.namcap.main.Config;

public class GameOverState implements IGamestate {
    
    private boolean won;
    private GameState state;
    private IGamestate nextState = this;
    
    public GameOverState(boolean won, GameState gameState) {
        this.won = won;
        this.state = gameState;
    }
    
    @Override
    public boolean update() {
        return false;
    }
    
    private static Color gray = new Color(0,0,0,.6f);
    private static Font font = Config.font.deriveFont(Font.BOLD, 60);
    
    @Override
    public void draw(Graphics g) {
        
        state.draw(g);
        Rectangle rect = g.getClipBounds();
        g.setColor(gray);
        g.fillRect(0, 0, (int)rect.getWidth(), (int)rect.getHeight());
    
        int centerx = (int)rect.getWidth()/2;
        int centery = (int)rect.getHeight()/2;
        g.setFont(font);
        g.setColor(Color.GREEN);
        String s = won ? "GAME" : "GAME OVER";
        FontMetrics metrics = g.getFontMetrics(font);
        int startx = centerx - metrics.stringWidth(s)/2;
        int starty = centery - (metrics.getLeading() + metrics.getAscent())/2;
        g.drawString(s, startx, starty);
    }
    
    @Override
    public Dimension getPreferredSize() {
        
        return null;
    }
    
    @Override
    public IGamestate nextState() {
        
        return nextState;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            nextState = new MenuState();
        }
    
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    
    }
}
