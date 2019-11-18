package me.namcap.colorpicker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;

import me.namcap.main.Main;
import me.namcap.util.Direction;
import me.namcap.util.Util;

public class ColorPicker extends JDialog implements KeyListener {
    
    private RedArea       redArea;
    private GreenBlueArea greenBlueArea;
    private JPanel colorpanel;
    private JTextPane[] textpanes = new JTextPane[2];
    private Consumer<Color> onComplete;
    
    private int red, green, blue;
    private int selected = 0;
    private Border borderSelected = BorderFactory.createLineBorder(Color.red);
    private Border empty = BorderFactory.createEmptyBorder();
    
    public ColorPicker(int scale) {
    
        setLayout(null);
        setUndecorated(true);
        Main.placeCentered(this, 2 + 255 + 2 + 100 + 2, 2 + 255 + 2 + 20 + 2);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addKeyListener(this);
        
        redArea = new RedArea(scale, this::onRed);
        greenBlueArea = new GreenBlueArea(scale, this::onGreenBlue);
        greenBlueArea.setBorder(borderSelected);
        
        add(greenBlueArea);
        greenBlueArea.setBounds(2, 2, greenBlueArea.getWidth(), greenBlueArea.getHeight());
        add(redArea);
        redArea.setBounds(2, 259, redArea.getWidth(), redArea.getHeight());
        setFocusTraversalKeysEnabled(false);
        
        colorpanel = new JPanel();
        colorpanel.setBounds(259, 2, 100, 100);
        colorpanel.setBackground(Color.black);
        add(colorpanel);
        
        textpanes[0] = new JTextPane();
        textpanes[0].setBounds(259, 104, 100, 20);
        textpanes[0].setText("#000000");
        textpanes[0].setEditable(false);
        textpanes[0].setHighlighter(null);
        textpanes[0].addKeyListener(this);
        textpanes[0].setFocusTraversalKeysEnabled(false);
        add(textpanes[0]);
        
        textpanes[1] = new JTextPane();
        textpanes[1].setBounds(259, 126, 100, 20);
        textpanes[1].setText("rgb(0, 0, 0)");
        textpanes[1].setEditable(false);
        textpanes[1].setHighlighter(null);
        textpanes[1].addKeyListener(this);
        textpanes[1].setFocusTraversalKeysEnabled(false);
        add(textpanes[1]);
        
        setVisible(true);
    }
    
    public ColorPicker(int scale, Color color) {
        this(scale);
        redArea.setColor(color.getRed(), color.getGreen(), color.getBlue());
        greenBlueArea.setColor(color.getRed(), color.getGreen(), color.getBlue());
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        updateColor();
    }
    
    public void subscribe(Consumer<Color> consumer) {
        this.onComplete = consumer;
    }
    
    private void updateColor() {
        colorpanel.setBackground(new Color(red, green, blue));
        textpanes[1].setText(String.format("rgb( %d, %d, %d)", red, green , blue));
        textpanes[0].setText(Util.getHexString(red, green, blue));
    }
    
    public void onRed(int red) {
        this.red = red;
        greenBlueArea.setRed(red);
        repaint();
        selected = 1;
        redArea.setBorder(borderSelected);
        greenBlueArea.setBorder(empty);
        selected = 1;
        updateColor();
    }
    
    public void onGreenBlue(int green, int blue) {
        this.green = green;
        this.blue = blue;
        redArea.setGreenBlue(green, blue);
        repaint();
        greenBlueArea.setBorder(borderSelected);
        redArea.setBorder(empty);
        selected = 0;
        updateColor();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    
        if(e.getKeyCode() == KeyEvent.VK_TAB) {
            onTab();
        } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            onEnter();
        } else {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_KP_UP:
                    onDirection(Direction.NORTH);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_KP_RIGHT:
                    onDirection(Direction.EAST);
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_KP_DOWN:
                    onDirection(Direction.SOUTH);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_KP_LEFT:
                    onDirection(Direction.WEST);
                    break;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                redArea.resetSpeed();
                greenBlueArea.resetSpeed();
                break;
        }
    }
    
    private void onTab() {
        selected = (selected+1)%2;
        switch (selected) {
            case 0:
                greenBlueArea.setBorder(borderSelected);
                redArea.setBorder(empty);
                break;
            case 1:
                greenBlueArea.setBorder(empty);
                redArea.setBorder(borderSelected);
                break;
        }
    }
    
    private void onEnter() {
        this.dispose();
        if(this.onComplete != null) {
            this.onComplete.accept(new Color(red, green, blue));
        }
    }
    
    private void onDirection(Direction d) {
        switch (selected) {
            case 0:
                greenBlueArea.move(d);
                break;
            case 1:
                redArea.move(d);
                break;
        }
    }
}
