package ru.evpa;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class ExplorerMp3 extends JPanel implements ActionListener{
    private JTextField tfPathDir;
    private JTextArea jta;
    private JTree tree;
    private JButton refresh;
    private JTable tableFiles;
    private JScrollPane scrollTree;
    private JScrollPane scrollTableFiles;

    String currDirectory=null;

    final String[] colHeads={"File Name","Artist","Album","Song"};
    String[][]data={{"","","","",""}};

    ExplorerMp3(String path) {
        tfPathDir=new JTextField();

        refresh=new JButton("Choice directory");
        File temp=new File(path);
        DefaultMutableTreeNode top=createTree(temp);
        tree=new JTree(top);
        scrollTree =new JScrollPane(tree);

        tableFiles =new JTable(data, colHeads);
        scrollTableFiles =new JScrollPane(tableFiles);

        setLayout(new BorderLayout());
        add(tfPathDir,BorderLayout.NORTH);
        add(scrollTree,BorderLayout.WEST);
        add(scrollTableFiles,BorderLayout.CENTER);
        add(refresh,BorderLayout.SOUTH);

        tree.addMouseListener(
                new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent me)
                    {
                        doMouseClicked(me);
                    }
                });
        tfPathDir.addActionListener(this);
        refresh.addActionListener(this);

    }
    @Override
    public void actionPerformed(ActionEvent ev) {

        File temp=new File(tfPathDir.getText());
        DefaultMutableTreeNode newtop=createTree(temp);
        if(newtop!=null)
            tree=new JTree(newtop);
        remove(scrollTree);
        scrollTree =new JScrollPane(tree);
        setVisible(false);
        add(scrollTree,BorderLayout.WEST);
        tree.addMouseListener(
                new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent me)
                    {
                        doMouseClicked(me);
                    }
                });

        setVisible(true);

    }
    DefaultMutableTreeNode createTree(File temp)
    {
        DefaultMutableTreeNode top=new DefaultMutableTreeNode(temp.getPath());
        if(!(temp.exists() && temp.isDirectory()))
            return top;

        fillTree(top,temp.getPath());

        return top;
    }
    void fillTree(DefaultMutableTreeNode root, String filename)
    {
        File temp=new File(filename);

        if(!(temp.exists() && temp.isDirectory()))
            return;
//System.out.println(filename);
        File[] filelist=temp.listFiles();

        for(int i=0; i<filelist.length; i++)
        {
            if(!filelist[i].isDirectory())
                continue;
            final DefaultMutableTreeNode tempDmtn=new DefaultMutableTreeNode(filelist[i].getName());
            root.add(tempDmtn);
            final String newfilename=new String(filename+"\\"+filelist[i].getName());
            Thread t=new Thread()
            {
                public void run()
                {
                    fillTree(tempDmtn,newfilename);
                }//run
            };//thread
            if(t==null)
            {System.out.println("no more thread allowed "+newfilename);return;}
            t.start();
        }//for
    }//function
    void doMouseClicked(MouseEvent me)
    {
        TreePath tp=tree.getPathForLocation(me.getX(),me.getY());
        if(tp==null) return;

        String s=tp.toString();
        s=s.substring(1, s.length()-1);
        s=s.replace(", ","\\");
        tfPathDir.setText(s);
        showFiles(s);
    }
    void showFiles(String dirName)
    {
        File temp=new File(dirName);
        data=new String[][]{{"","","",""}};
        remove(scrollTableFiles);
        tableFiles =new JTable(data, colHeads);
        scrollTableFiles =new JScrollPane(tableFiles);
        setVisible(false);
        add(scrollTableFiles,BorderLayout.CENTER);
        setVisible(true);

        if(!temp.exists() || !temp.isDirectory()) return;

        String[] listOfNames = Arrays.stream(temp.listFiles()).filter(n-> !n.isDirectory() && n.getName().length()>4 && n.getName().substring(n.getName().length()-4).equals(".mp3")).map(n->n.getName()).toArray(String[]::new);
        if(listOfNames.length==0) return;
        //"File Name","Artist","Album","Song"
        data=new String[listOfNames.length][4];
        for (int i = 0; i < listOfNames.length; i++) {
            data[i][0]=listOfNames[i];
            String[] fileMp3info = Mp3FileTools.GetMp3Info(tfPathDir.getText()+"\\"+listOfNames[i]);
            data[i][1]=fileMp3info[0]+"";
            data[i][2]=fileMp3info[1];
            data[i][3]=fileMp3info[2];
        }

        remove(scrollTableFiles);
        tableFiles =new JTable(data, colHeads);
        scrollTableFiles =new JScrollPane(tableFiles);
        tableFiles.setCellSelectionEnabled(true);
        ListSelectionModel select = tableFiles.getSelectionModel();
        select.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        select.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()) return; //чтобы не выскакивало (...)ды - ведь событий мыши м б много )))
                int row = tableFiles.getSelectedRow();
                if(data[row][1].equals("NoTag!")) {
                    JOptionPane.showMessageDialog(null, "Can't be transformed", "Message", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                DialogForMp3 fm = new DialogForMp3(Mp3InfoChange.et,dirName, data, row);
                fm.CreateAndShowDialog();
            }
        });
        setVisible(false);
        add(scrollTableFiles,BorderLayout.CENTER);
        setVisible(true);
    }

}
