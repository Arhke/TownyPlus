package com.Arhke.District.FileIO;

import com.Arhke.District.Utils.Base;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManager extends Base {
    ConfigurationSection _config;
    ConfigurationSection _default;

    public DataManager(ConfigurationSection CS){

        _config = CS;
    }
    public DataManager(ConfigurationSection CS, ConfigurationSection Default){
        this(CS);
        _default = Default;
    }

    public ConfigurationSection getConfig(){
        return _config;
    }

    public boolean getBoolean(String... Path) {
        String path = pathOf(Path);
        if (_config.isBoolean(path))
            return _config.getBoolean(path);
        else
            _config.set(path, true);

        return true;
    }
    public int getInt(String... Path){
        String path = pathOf(Path);
        if(_config.isInt(path))
            return _config.getInt(path);
        else
            _config.set(path, 0);
        return 0;
    }
    public long getLong(String... Path){
        String path = pathOf(Path);
        if(_config.isLong(path)){
            return _config.getLong(path);
        }else
            _config.set(path, 0L);
        return 0L;
    }
    public double getDouble(String... Path){
        String path = pathOf(Path);
        if(_config.isDouble(path))
            return _config.getDouble(path);
        else
            _config.set(path, 0d);
        return 0d;
        }
    public String getString (String... Path){
        String path = pathOf(Path);
        if(_config.isString(path))
            return _config.getString(path);
        else
            _config.set(path, "");
        return "";

    }
    public List<String> getStringList(String... Path){
        String path = pathOf(Path);
        if(_config.isList(path)){
            return _config.getStringList(path);
        }else{
            _config.set(path ,new ArrayList<String>());
        }return new ArrayList<>();
    }
    public void set(Object value, String... Path){
        String path = pathOf(Path);
        _config.set(path, value);
    }

    /**
     * returns Null if UUID doesnt exist
     * @param Path
     * @return
     */
    @Nullable
    public UUID getUUID(String... Path){
        String path = pathOf(Path);
        if(_config.isString(path)){
            try {
                return UUID.fromString(_config.getString(path));
            }catch(IllegalArgumentException exception){
                return null;
            }
        }else{
            _config.set(path, null);
            return null;
        }
    }

    /**
     * note this method doesnt fill in a default location
     * @param Path
     * @return
     */
    @Nullable
    public Location getLocation(String... Path) {
        ConfigurationSection cs = getConfigurationSection(Path);
        if (!cs.isString(WorldKey)) {
            set(null, Path);
            return null;
        }
        World world;
        if ((world = Bukkit.getWorld(cs.getString(WorldKey))) == null) {
            set(null, Path);
            return null;
        }
        if (!(cs.isDouble(XKey) && cs.isDouble(YKey) && cs.isDouble(ZKey))){
            set(null, Path);
            return null;
        }
        return new Location(world, cs.getDouble(XKey), cs.getDouble(YKey), cs.getDouble(ZKey));
    }
    public void setLocation(Location Location, String... Path){
        ConfigurationSection cs = getConfigurationSection(Path);
        cs.set(WorldKey, Location.getWorld().getName());
        cs.set(XKey, Location.getX());
        cs.set(YKey, Location.getY());
        cs.set(ZKey, Location.getZ());
    }
    private ConfigurationSection getConfigurationSection(String... Path) {
        String path = pathOf(Path);
        return _config.isConfigurationSection(path)?_config.getConfigurationSection(path):
                _config.createSection(path);
    }

    public void delete(FileManager cm) {
        cm.getDataManager().set(null, _config.getCurrentPath());
    }
    public DataManager getDataManager(String... Path){
        return new DataManager(getConfigurationSection(Path));
    }
    //Location Keys
    public static final String WorldKey = "worldLoc", XKey = "xLoc", YKey = "yLoc", ZKey = "zLoc";



}

