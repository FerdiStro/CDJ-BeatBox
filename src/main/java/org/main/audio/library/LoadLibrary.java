package org.main.audio.library;

import org.main.audio.playegrid.SlotAudio;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


public class LoadLibrary {

    private static LoadLibrary INSTANCE;

    public static LoadLibrary getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoadLibrary();
        }
        return INSTANCE;
    }

    private Clip clip;
    private final AudioInputStream audioStream = null;

    private final List<LibraryKind> folderView = new ArrayList<>();

    private final HashMap<String, SlotAudio> audioCache = new HashMap<>();




    //Todo:  Write Library Loader
    private LoadLibrary() {
        LibraryKind sound = generateTree("src/main/resources/Sound/");
        LibraryKind onShoot = generateTree("src/main/resources/OnShoot/");
        folderView.add(sound);
        folderView.add(onShoot);
        setSelectedLibrary(0);
    }


    public void setSelectedLibrary(int i ){
        folderView.forEach(x-> x.setSelected(false));
        folderView.get(i).setSelected(true);
    }


    private LibraryKind generateTree(String path) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        List<String> filePaths = new ArrayList<>();
        Path dir = Paths.get(path);
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(Files::isRegularFile).forEach(filePath -> {
                try {
                    filePaths.add(filePath.toString());

                    List<String> framePath = Arrays.stream(filePath.toString().replace(path, "").split("/")).toList();
                    DefaultMutableTreeNode parent = root;

                    for (String part : framePath) {
                        DefaultMutableTreeNode child = null;
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
                            if (node.getUserObject().equals(part)) {
                                child = node;
                                break;
                            }
                        }
                        if (child == null) {
                            child = new DefaultMutableTreeNode(part);
                            parent.add(child);
                        }
                        parent = child;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        JTree tree = new JTree(root);
        tree.setRootVisible(true);

        final JScrollPane soundView = new JScrollPane(tree);
        soundView.setVisible(false);

        String name = path.split("/")[path.split("/").length - 1];


        final LibraryKind sound = new LibraryKind(name, false, soundView, TYPE.getType(name));

        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if (node != null) {
                        String nodeName = node.toString();

                        if (nodeName.contains(".wav")) {
                            sound.setSelectedTitel(nodeName);
                        }

                        if (tree.isExpanded(path)) {
                            tree.collapsePath(path);
                        } else {
                            tree.expandPath(path);
                        }
                    }
                }
            }
        });
        sound.setFilePaths(filePaths);

        return sound;
    }



    private SlotAudio loadSoundInCache(){
        File audioFile = null;
        for(String path  : Objects.requireNonNull(getSelectedSound()).getFilePaths()){
            if(path.contains(Objects.requireNonNull(getSelectedSound()).getSelectedTitel())){
                audioFile   = new File(path);
                break;
            }
        }
        try {
            assert audioFile != null;
            return new SlotAudio(audioFile, getSelectedSound().getType() );
        } catch (Exception e) {
            return null;
        }
    }

    public LibraryKind getSelectedSound(){
        for (LibraryKind libraryKind : getFolderView()) {
            if (libraryKind.isSelected()) {
                return libraryKind;
            }
        }
        return null;
    }



    public SlotAudio getSelectedSloutAudio() {
                SlotAudio slotAudio = audioCache.get(Objects.requireNonNull(getSelectedSound()).getSelectedTitel());
                if (slotAudio == null) {
                    slotAudio = loadSoundInCache();
                }
                return slotAudio;
    }


    public AudioInputStream getAudioStream(){
        if(audioStream != null){
            return audioStream;
        }
        return null;
    }

    public List<LibraryKind> getFolderView(){
        return this.folderView;
    }

    public void updateVis(){
        for(LibraryKind libraryKind : getFolderView()){
            if(libraryKind.isSelected()){
                libraryKind.getTree().setVisible(true);
            }

        }
    }




}
