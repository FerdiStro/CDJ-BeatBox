package org.main.settings.objects;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeatBoxWindowSettings extends AbstractSettings {
    private static final String PATH = "src/main/resources/configs/beatBoxWindowConfig.json";

    private boolean beatBoxWindowFullScreen;
    private boolean beatBoxWindowFullScreenInit;

    private boolean beatBoxWindowFullScreenBorderLess;
    private boolean beatBoxWindowFullScreenBorderLessInit;




    @Override
    public String getPATH() {
        return PATH;
    }

    @Override
    public void reset() {
        this.beatBoxWindowFullScreen = beatBoxWindowFullScreenInit;
        this.beatBoxWindowFullScreenBorderLess = beatBoxWindowFullScreenBorderLessInit;
    }
}
