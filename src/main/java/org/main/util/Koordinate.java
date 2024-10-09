package org.main.util;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Koordinate {
    private int x;
    private int y;


    private final String name;

    public Koordinate(int x, int y) {
        this.x = x;
        this.y = y;
        name = x +"-"+y;
    }




}
