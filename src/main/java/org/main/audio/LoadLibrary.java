package org.main.audio;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


public class LoadLibrary {

    private static LoadLibrary INSTANCE;

    public static LoadLibrary getInstance(){
        if(INSTANCE==null){
            INSTANCE = new LoadLibrary();
        }
        return INSTANCE;
    }

    private Clip clip;
    private AudioInputStream audioStream = null;
    private final JScrollPane folderPanel;
    private final JTree tree;

    private String selectedSound;
    private final HashMap<String, SlotAudio> audioCache =  new HashMap<>();
    private final List<String> filePaths =  new ArrayList<>();


    private final DefaultMutableTreeNode root =  new DefaultMutableTreeNode("Root");;

    //Todo:  Write Library Loader
    private LoadLibrary(){
        Path dir = Paths.get("src/main/resources/");


        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(Files::isRegularFile).forEach(filePath -> {
                try {
                    filePaths.add(filePath.toString());

                    List<String> framePath = Arrays.stream(filePath.toString().replace("src/main/resources/", "").split("/")).toList();
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

        tree = new JTree(root);
        tree.setRootVisible(true);


        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if (node != null) {
                        String nodeName = node.toString();


                        if(nodeName.contains(".wav")) {
                            selectedSound =  nodeName;
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

        JScrollPane treeView = new JScrollPane(tree);

        this.folderPanel =  treeView;


    }


    private SlotAudio loadSoundInCache(){
        File audioFile = null;
        for(String path  : filePaths){
            if(path.contains(selectedSound)){
                audioFile   = new File(path);
            }
        }
        try {
            assert audioFile != null;
            return new SlotAudio(audioFile);
        } catch (Exception e) {
            return null;
        }
    }


    public SlotAudio getSelectedSound(){
        SlotAudio slotAudio = audioCache.get(selectedSound);
        if(slotAudio ==  null){
            slotAudio = loadSoundInCache();
        }
        return  slotAudio;
    }


    public AudioInputStream getAudioStream(){
        if(audioStream!= null){
            return audioStream;
        }
        return null;
    }


    public  JScrollPane getFolderView(){
        return folderPanel;
    }




}
