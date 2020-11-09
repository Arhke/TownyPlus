package com.Arhke.District.FileIO;

import com.Arhke.District.Utils.Base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirectoryManager extends Base {
    File _dir;
    List<FileManager> _cmList = new ArrayList<>();
    public DirectoryManager(File file){
        if(file.exists()){
            if(file.isDirectory()){
                _dir = file;
            }else{
                file.delete();
                except("[DirectoryManager.java] File in Constructor must be directory");
            }
        }else{
            file.mkdirs();
            _dir = file;
        }

        for (File dirfile:_dir.listFiles()){
            _cmList.add(new FileManager(dirfile));
        }
    }
    public File[] getFileList() {
        return _dir.listFiles();
    }
    public List<FileManager> getCMList(){
        return _cmList;
    }
    public void saveAllFiles() {
        for (FileManager cm: _cmList){
            cm.save();
        }
    }
    public FileManager getOrNewCM(String path){
        for(FileManager cm: _cmList){
            if(cm.getFileName().equals(path)){
                return cm;
            }
        }
        File file = Paths.get(_dir.toString(), path + ".yml").toFile();
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            except("Error Creating New File");
        }
        FileManager cm;
        _cmList.add(cm = new FileManager(file));
        return cm;

    }
}
