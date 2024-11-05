package org.main.audio.library;

public enum TYPE {
    SOUND("Sounds/Sound"),
    ONESHOOT ("Sounds/OnShoot"),

    ONE_BEST("1"),
    TWO_BEAT("2"),
    FOUR_BEAT("4");

    private final String text;

    TYPE(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getBeatHit(){
        return Integer.parseInt(this.text);
    }

    public static TYPE getType(String name){
        return switch (name) {
            case "Sound" -> SOUND;
            case "OnShoot" -> ONESHOOT;
            default -> null;
        };
    }


}
