package org.main.audio.library;

public enum TYPE {
    SOUND("Sounds/Sound"),
    ONESHOOT ("Sounds/OnShoot");

    private final String text;

    TYPE(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TYPE getType(String name){
        return switch (name) {
            case "Sound" -> SOUND;
            case "OnShoot" -> ONESHOOT;
            default -> null;
        };
    }


}
