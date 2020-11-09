package com.Arhke.District.Utils;

public enum Direction {
    N("North", 0, -1), E("East", 1, 0), S("South", 0, 1), W("West", -1, 0);
    String _name;
    int _x, _z;
    Direction(String name, int x, int z){
        _name = name;
        _x = x; _z = z;
    }
    public String getName(){
        return _name;
    }
    public int getX(){
        return _x;
    }
    public int getZ() {
        return _z;
    }
}
