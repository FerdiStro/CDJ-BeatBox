package org.main.audio.library;

import lombok.Getter;
import org.main.audio.metadata.MetaDataFinder;
import org.main.audio.metadata.SlotAudioMetaData;
import org.main.audio.playegrid.SlotAudio;
import org.main.settings.Settings;
import org.main.settings.objects.LibrarySettings;
import org.main.util.Logger;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
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

    @Getter
    private final List<LibraryKind> folderView = new ArrayList<>();

    private final HashMap<String, SlotAudio> audioCache = new HashMap<>();


    private final LibrarySettings librarySettings;
    private final MetaDataFinder metaDataFinder;

    private LoadLibrary() {
        Logger.init(getClass());
        this.metaDataFinder =  MetaDataFinder.getInstance();
        this.librarySettings = Settings.getInstance().getLibrarySettings();

        for(String path: librarySettings.getSoundKindListPaths()){
            LibraryKind libraryKind = generateTree(path);
            folderView.add(libraryKind);
        }
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

                            if(soundSupportet(part)){
                                SlotAudioMetaData metaData = metaDataFinder.getMetaData(part);
                                if(metaData != null){
                                    child = new DefaultMutableTreeNode(  metaData.getShortName() + "  K:" + metaData.getKey() + " B: "+  metaData.getBpm() +"---"+ part);
                                }else{
                                    child = new DefaultMutableTreeNode(part);
                                }

                            }else{
                                child = new DefaultMutableTreeNode(part);

                            }
                            parent.add(child);
                        }
                        parent = child;
                    }

                    /*
                        todo:BPM and KEY
                     */

//                    File audioFile = new File("/Users/ferdinand/IdeaProjects/CDJ-BeatBox/src/main/resources/OnShoot/seq/Mzperx_SEQ_06_Accidd_135bpm.wav");
//                    new BpmCalculator().calculateBpm(audioFile);



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
                sound.stopRreListen();
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if (node != null) {
                        String nodeName = node.toString();
                        String soundName = nodeName;

                        if(nodeName.split("---").length >= 2){
                            soundName = nodeName.split("---")[1];
                        }

                        if (soundSupportet(soundName)) {
                            sound.setSelectedTitel(soundName);
                            sound.preListen();



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

    double firstOnset = -1;
    double lastOnset = -1;
    int onsetCount = 0;

    private double calculateBPM(double onsetCount, double lastOnset, double firstOnset) {
        if (onsetCount > 1) {
            double totalTime = lastOnset - firstOnset;
            double avgOnsetTime = totalTime / (onsetCount - 1);
            return 60 / avgOnsetTime;  // Umrechnung in BPM
        }
        return 0;
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
            SlotAudio audio =  new SlotAudio(audioFile, getSelectedSound().getType() );;
            this.audioCache.put(getSelectedSound().getSelectedTitel(), audio);
            return audio;
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



    public SlotAudio getSelectedSlotAudio() {
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

    public void updateVis(){
        for(LibraryKind libraryKind : getFolderView()){
            if(libraryKind.isSelected()){
                libraryKind.getTree().setVisible(true);
            }

        }
    }


    private boolean soundSupportet(String format){
        for (String supportedFormat : librarySettings.getSupportetFormat()){
           if(format.contains(supportedFormat)){
               return true;
           }
        }
        return false;
    }


    public void updateFont(Integer fontsize){
        for (LibraryKind libraryKind : folderView) {
            changeTreeFontSize(libraryKind.getTree(), fontsize);
        }
    }


    private void changeTreeFontSize(JScrollPane scrollPane, int fontSize) {
        JTree tree = (JTree) scrollPane.getViewport().getView();

        if (tree != null) {
            Font currentFont = tree.getFont();
            Font newFont = new Font(currentFont.getFontName(), currentFont.getStyle(), fontSize);
            tree.setFont(newFont);
            tree.setRowHeight(fontSize + 5);
            tree.repaint();
        }
    }





}
