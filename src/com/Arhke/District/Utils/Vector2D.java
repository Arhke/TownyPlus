package com.Arhke.District.Utils;

import com.Arhke.District.FileIO.DataManager;

public class Vector2D {

	private double _x;
	private double _z;


	public static String XKey = "x", ZKey = "z";
	public Vector2D(DataManager dm){
		_x = dm.getDouble(XKey);
		_z = dm.getDouble(ZKey);
	}
	public Vector2D(int x, int z) {
		_x = x;
		_z = z;
	}


	public Vector2D(Vector2D vector) {
		_x = vector.getX();
		_z = vector.getZ();
	}
	public void write(DataManager dm){
		dm.set(_x, XKey);
		dm.set(_z, ZKey);
	}
	public double getX() {
		return _x;
	}
	public double getZ() {
		return _z;
	}
	public void setX(double x) {
		_x = x;
	}
	public void setZ (double z){
		_z = z;
	}
	
}
