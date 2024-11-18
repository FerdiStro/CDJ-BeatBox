package org.main.util;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coordinates {
    private int x;
    private int y;


    private final String name;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
        name = x + "-" + y;
    }


}
