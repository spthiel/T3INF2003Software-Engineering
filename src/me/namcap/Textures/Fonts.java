package me.namcap.Textures;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public enum Fonts {
    
    COURIER("Courier-Normal_Regular"),
    VT323("VT323-Regular"),
    MANSALVAREGULAR("Mansalva-Regular"),
    AMATICSC("AmaticSC-Regular"),
    AUDIOWIDE("Audiowide-Regular"),
    DANCINGSCRIPT("DancingScript-Regular"),
    FREDERICKATHEGREAT("FrederickatheGreat-Regular"),
    MONOTON("Monoton-Regular"),
    PACIFICO("Pacifico-Regular"),
    PRESSSTART2P("PressStart2P-Regular"),
    SAIRASTENCIL("SairaStencilOne-Regular"),
    TURRETROAD("TurretRoad-Regular"),
    VIBES("Vibes-Regular");
    
    private Font font;
    private static final String pathFormat = "/Fonts/%s.ttf";
    
    Fonts(String path) {
        InputStream is = Fonts.class.getResourceAsStream(String.format(pathFormat,path));
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(String.format(pathFormat,path), e);
        }
    }
    
    public Font getFont() {
        return font;
    }
    
    public Font deriveFont(int style, float size) {
        return font.deriveFont(style, size);
    }
    
}
