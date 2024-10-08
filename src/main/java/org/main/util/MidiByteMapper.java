package org.main.util;

public class MidiByteMapper {

    public static Integer padMapper(int originalPad){
        return switch (originalPad) {
            case 36 -> 1;
            case 37 -> 2;
            case 38 -> 3;
            case 39 -> 4;
            case 40 -> 5;
            case 41 -> 6;
            case 42 -> 7;
            case 43 -> 8;
            default -> 0;
        };
    }
}
