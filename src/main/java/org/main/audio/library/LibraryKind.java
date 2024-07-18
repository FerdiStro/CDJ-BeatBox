package org.main.audio.library;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryKind {

    private String name;
    private TYPE type;


    private boolean selected;

    private JScrollPane  tree;
    private String selectedTitel;
    private List<String> filePaths = new ArrayList<>();



    public LibraryKind(String name, boolean selected, JScrollPane tree, TYPE type) {
        this.name = name;
        this.selected = selected;
        this.tree = tree;
        this.type = type;
    }


    public void setSelected(boolean selected) {
        this.tree.setVisible(selected);
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public JScrollPane getTree() {
        return tree;
    }

    public String getSelectedTitel() {
        return selectedTitel;
    }

    public void setSelectedTitel(String selectedTitel) {
        this.selectedTitel = selectedTitel;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public TYPE getType() {
        return type;
    }
}
