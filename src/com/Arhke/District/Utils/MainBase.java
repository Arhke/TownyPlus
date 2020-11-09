package com.Arhke.District.Utils;

import com.Arhke.District.Main;
import org.bukkit.Bukkit;

public abstract class MainBase extends Base{
    private Main _plugin;
    public MainBase(Main Instance){
        _plugin = Instance;
    }
    public Main getPlugin(){
        return _plugin;
    }
    public void exceptDisable(String Msg){
        Bukkit.getPluginManager().disablePlugin(_plugin);
        throw new RuntimeException(Msg);
    }

}
