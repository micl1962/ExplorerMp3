package ru.evpa;

import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class DialogForMp3 extends JDialog {

    private JTextField tfArtist;
    private  JTextField tfAlbum ;
    private  JTextField tfSong ;
    private  JCheckBox checkBoxForAll;
    private  JCheckBox checkBoxNameFromFile;

    private String filesDirName;
    private int selectedFile;
    private String[][] fileList;
    private static int FILE_NAME = 0;
    private static int ARTIST = 1;
    private static int ALBUM = 2;
    private static int SONG = 3;



    DialogForMp3(JFrame owner, String filesDirName, String[][] fileList, int selectedFile){
        super(owner);

        this.filesDirName = filesDirName;
        this.fileList = fileList;
        this.selectedFile = selectedFile;
        setLocationRelativeTo(owner);
    }
    void CreateAndShowDialog() {
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setTitle(filesDirName +"\\"+ fileList[selectedFile][FILE_NAME]);
        GridBagConstraints c = new GridBagConstraints();
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());
        addComponentsToPane(pane,c);
        pack();
        setVisible(true);
    }

    private void addComponentsToPane(Container pane, GridBagConstraints c) {

        tfArtist = new JTextField(fileList[selectedFile][ARTIST]);
        tfArtist.setPreferredSize( new Dimension( 200, 24 ) );
        tfAlbum = new JTextField(fileList[selectedFile][ALBUM]);
        tfSong = new JTextField(fileList[selectedFile][SONG]);
        checkBoxForAll = new JCheckBox("Применять Artist и Album ко всем файлам папки");
        //при общем трансофме нельзя менять все имена песен - только на имена файлов
        checkBoxForAll.addItemListener(e -> {
            if ((e.getStateChange() == ItemEvent.SELECTED)) {
                tfSong.setVisible(false);
            } else {
                tfSong.setVisible(true);
            }
        });
        checkBoxNameFromFile = new JCheckBox("Называть песни по именам файлов");
        JButton buttonOk = new JButton("Произвести редактирование");
        buttonOk.addActionListener(e -> TransformFiles(pane));
        showRow(pane,c,"  Artist : ",tfArtist,0,0);
        showRow(pane,c,"  Album : ",tfAlbum,0,1);
        showRow(pane,c,"  Song : ",tfSong,0,2);
        showCol(pane,c, checkBoxForAll,0,3,2);
        showCol(pane,c, checkBoxNameFromFile,0,4,2);
        //showCol(pane,c, new JLabel("Чтобы атрибут не изменился оставьте соответствующее поле пустым"),0,5,2);
        showCol(pane,c,buttonOk,0,6,1);
    }
    private  void showCol(Container pane, GridBagConstraints c,Component component, int row , int col, int gridwidth) {
        c.weighty = 2.0;
        c.gridx = row;
        c.gridy = col;
        c.gridwidth = gridwidth;
        pane.add(component,c);
    }
    private  void showRow(Container pane, GridBagConstraints c,String labelText, JTextField textField , int row , int col){
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel label = new JLabel(labelText);
        c.weighty = 2.0;
        c.gridx = row;
        c.gridy = col;
        pane.add(label,c);

        c.weighty = 2.0;
        c.gridx = row+1;
        c.gridy = col;
        pane.add(textField,c);
    }
    private  void TransformFiles(Container pane) {
        String oneOrAll ;
        String[][] transformList ;
        if(!checkBoxForAll.isSelected()) {
                transformList = new String[][]{fileList[selectedFile]};
                oneOrAll = "one";
        } else {
                transformList = fileList;
                oneOrAll = "all";
        }

        for (String[] transformFile : transformList) {
                String tmpSongName =checkBoxForAll.isSelected()?transformFile[SONG]:tfSong.getText();
                String songName =
                        checkBoxNameFromFile.isSelected() ? transformFile[FILE_NAME].replaceFirst("[.][^.]+$", "") : tmpSongName;
                String[] fileNewAttr = {tfArtist.getText(), tfAlbum.getText(), songName};
                Mp3FileTools.SetMp3Info(filesDirName + "\\" + transformFile[FILE_NAME], fileNewAttr);
        }
        JOptionPane.showMessageDialog(pane,"All done for "+oneOrAll+" file(s)","Result",JOptionPane.INFORMATION_MESSAGE);

    }


}
