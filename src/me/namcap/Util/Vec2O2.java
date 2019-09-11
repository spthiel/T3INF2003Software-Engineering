package me.namcap.Util;

public class Vec2O2<T1,T2> {
    
    public T1 x1;
    public T2 x2;
    
    public Vec2O2() {
        this.x1 = null;
        this.x2 = null;
    }
    
    public Vec2O2(T1 x1, T2 x2) {
        
        this.x1 = x1;
        this.x2 = x2;
    }
}
