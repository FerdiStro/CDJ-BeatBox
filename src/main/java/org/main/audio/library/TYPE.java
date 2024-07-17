package org.main.audio.library;

public enum TYPE {
    SOUND("Sound"),
    ONESHOOT ("OnShoot");

    private final String text;

    TYPE(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TYPE getType(String name){
        switch (name){
            case "Sound": return  SOUND;
            case "OnShoot": return ONESHOOT;
        }
        return null;
    }


}
