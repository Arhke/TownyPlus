package com.Arhke.District.Towns;

import com.Arhke.District.FileIO.FileManager;
import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.LandClaim.Claim;
import com.Arhke.District.Utils.MainBase;
import com.Arhke.District.Main;
import com.Arhke.District.TUsers.TUser;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TownsManager extends MainBase {


	/**
	 * Key is the Name of the Town
	 * Value is the Town Object
	 */
	Map<String,Town> _towns = new HashMap<>();
	FileManager _cm;
	
	public TownsManager(Main Instance, FileManager cm) {
		super(Instance);
		_cm = cm;
		DataManager dataManager = cm.getDataManager();
		for(String key: dataManager.getConfig().getKeys(false)){
			Town t = new Town(getPlugin(), dataManager.getDataManager(key), _cm);
			_towns.put(t.getName(), t);
		}
	}
	
	public Town createTown(TUser user, String name, Location loc, Claim c) {
		Town t;
		_towns.put(name, t = new Town(getPlugin(), user,name, loc, c, _cm));
		return t;
	}

	public void removeTown(Town t){
		_towns.remove(t.getName());
		t.remove();
	}
	public void removeTown(String townname){
		Town t = getTown(townname);
		if (t != null){
			removeTown(t);
		}
	}

	public void saveTowns() {
		for (Town t: _towns.values()){
			t.write();
		}
		_cm.save();
	}

	public void saveTown(String name) {
		Town t;
		if((t = getTown(name)) != null){
			info("Successfully saved data for Town " + t.getName());
			t.write();
			_cm.save();
		}else{
			warn("You are trying to save data for an User ID that Doesn't exist");
		}
	}



	public Town getTown(String Name) {
		return _towns.get(Name);
	}

	public Collection<Town> getTowns() {
		return _towns.values();
	}
	
}
