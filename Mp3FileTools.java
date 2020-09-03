package ru.evpa;
import com.mpatric.mp3agic.*;

import java.io.IOException;

public class Mp3FileTools  {
    private static Mp3File mp3File;

    static String[] GetMp3Info(String fileFullName) {
        mp3File=null;
        String[] infoEr = {"", "-", "-"};
        infoEr[0]=OpenMp3File(fileFullName);
        if (!infoEr[0].equals("Ok")){
            return infoEr;
        }
        ID3v2 id3v2 = mp3File.getId3v2Tag();
        return new String[]{id3v2.getArtist(), id3v2.getAlbum(), id3v2.getTitle()};
    }
    static boolean SetMp3Info(String fileFullName, String[] fileNewInfo) {
        mp3File=null;
        if(!OpenMp3File(fileFullName).equals("Ok"))
            return false;
        ID3v2 id3v2 = mp3File.getId3v2Tag();
        id3v2.setArtist(fileNewInfo[0]);
        id3v2.setAlbum(fileNewInfo[1]);
        id3v2.setTitle(fileNewInfo[2]);
        mp3File.setId3v2Tag(id3v2);

        String newFile = new StringBuilder(fileFullName).insert(fileFullName.length()-4, "_").toString();
        try {
            mp3File.save(newFile);
        } catch (IOException | NotSupportedException e) {
            e.printStackTrace();
        }
        return true;
    }


    private static String OpenMp3File(String fileFullName){
        try {
            mp3File = new Mp3File(fileFullName);
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        } catch (java.nio.file.InvalidPathException e) {
            e.printStackTrace();
            return "InvalidPathException!";
        }  catch (UnsupportedTagException e) {
            e.printStackTrace();
            return "UnsupportedTagException";
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return "IOException!";    }

        if(!mp3File.hasId3v2Tag()) {
            return "NoTag!";
        }
        return "Ok";

    }

}
