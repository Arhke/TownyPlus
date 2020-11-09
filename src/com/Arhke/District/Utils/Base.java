package com.Arhke.District.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class Base {
    public static long TimeOut = 5000L;
    public static String translateColorCodes(String Msg) {
        return Msg.replace('&', ChatColor.COLOR_CHAR).replace("\\n", "\n");
    }
    public static String tcm(String msg, Object... objList){
        String ret = translateColorCodes(msg);
        for (int i = 0; i < objList.length; i++){
            ret = ret.replace("{"+i+"}", objList[i].toString());
        }
        return ret;
    }
    public static String[] tcm(String[] msg, Object... objList){
        for(int j = 0; j< msg.length; j++) {
            msg[j] = tcm(msg[j], objList);
        }
        return msg;
    }
    public static boolean isInBetween(double a, double b, double between){
        return (a >= between && b <= between) || (a <= between && b >= between);
    }

    public static int round(double num){
        return (int)Math.floor(num+0.5);
    }

    public static String pathOf(String... Path) {
        if (Path.length == 0)
            return "";
        else if (Path.length == 1)
            return Path[0];
        else {
            String ret = Path[0];
            for (int i = 1; i < Path.length; i++) {
                ret += "." + Path[i];
            }
            return ret;
        }
    }
    public static String pathOf(String[] ParentPath, String... Path){
        String parentpath = pathOf(ParentPath);
        String path = pathOf(Path);
        if (parentpath.length() == 0)
            return path;
        else
            return path.length() == 0? parentpath:parentpath + "." + path;
    }




    public static void info(String Msg) {
        Bukkit.getLogger().info(Msg);
    }
    public static void warn(String Msg) {
        Bukkit.getLogger().warning(Msg);
    }
    public static void error(String Msg){
        Bukkit.getLogger().warning(Msg);
    }
    public static void except(String Msg){
        throw new RuntimeException(Msg);
    }
    protected class LevelUtil{
        int _level;
        double _points;
        double _extrapoints;
        public LevelUtil(double initial, double multiplier, double increment, double points){
            _points = points;
            double levelpass = initial;
            int level = 1;
            long timestamp = System.currentTimeMillis();
            while(points >= levelpass){
                level++;
                points-=levelpass;
                levelpass*=multiplier;
                levelpass+=increment;
                if(System.currentTimeMillis()-timestamp > TimeOut || levelpass < 1){
                    _level = 1;
                    _extrapoints = 0;
                    return;
                }
            }
            _level = level;
            _extrapoints = points;
        }
        public int getLevel() {
            return _level;
        }
        public double getExtraPoints() {
            return _extrapoints;
        }
        public double getPoints() {
            return _points;
        }
    }
}