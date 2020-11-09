package com.Arhke.District.FileIO;

import com.Arhke.District.Utils.Base;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager extends Base {
    private File _file;
    private YamlConfiguration _config;
    private DataManager _dataManager;
    public FileManager(File File){
        if(!File.exists()){
            try {
                File.getParentFile().mkdirs();
                File.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(!File.isFile()){
                File.delete();
                except("[DataReader.java] File must not be a Directory");
            }
        }
        _config = YamlConfiguration.loadConfiguration(File);
        _file = File;
        _dataManager = new DataManager(_config);
    }
    public String getFileName(){
        return _file.getName().replace(".yml","");
    }
    public YamlConfiguration getConfig() {
        return _config;
    }
    public DataManager getDataManager() {
        return _dataManager;
    }
    public void saveToFile(File File) {
        if(!File.exists()){
            try {
                File.getParentFile().mkdirs();
                File.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(!File.isFile()){
            except("FileManager.java saveToFile file has to be file and not directory");
        }
        try {
            _config.save(File);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save(){
        saveToFile(_file);
    }
    public void deleteFile(){
        _file.delete();
    }
}
