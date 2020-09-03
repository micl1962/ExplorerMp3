package ru.evpa;

import javax.swing.*;
import java.awt.*;

public class Mp3InfoChange extends JFrame {
    static Mp3InfoChange et;
    Mp3InfoChange(String path){
        super("выбор директории для преобразования");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width-50,screenSize.height-50);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JLabel pleaseWait = new JLabel(new ImageIcon("src/main/resources/index.gif"));
        setLayout(new BorderLayout());
        add(pleaseWait,BorderLayout.CENTER);
        setVisible(true);
        ExplorerMp3 explorer = new ExplorerMp3(path);
        remove(pleaseWait);
        add(explorer,"Center");
        revalidate();

    }

    public static void main(String[] args) {
//выбор
     et = new Mp3InfoChange("d:\\torr");
    }
}
