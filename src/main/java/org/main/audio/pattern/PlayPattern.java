package org.main.audio.pattern;

import lombok.Getter;
import lombok.Setter;
import org.main.audio.PlayerGrid;




public class PlayPattern {

    @Getter
    private final  String name;

    @Getter
    @Setter
    private PlayerGrid grid;


    public PlayPattern(String name) {
        this.name = name;
    }
}
