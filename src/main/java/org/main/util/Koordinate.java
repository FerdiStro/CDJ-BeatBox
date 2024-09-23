package org.main.util;

public class Koordinate {
    private int x;
    private int y;


    private String name;

    public Koordinate(int x, int y) {
        this.x = x;
        this.y = y;
        name = x +"-"+y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }



}
