package org.main.settings.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class LibrarySettings extends AbstractSettings {
    private static final String PATH = "src/main/resources/configs/libraryConfig.json";


    ArrayList<String> soundKindListPaths = new ArrayList<>();


    ArrayList<String> supportetFormat = new ArrayList<>();


    @Override
    public String getPATH() {
        return PATH;
    }

}
