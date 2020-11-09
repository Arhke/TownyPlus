package com.Arhke.District.Towns;

import com.Arhke.District.FileIO.FileManager;
import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.LandClaim.Claim;
import com.Arhke.District.Main;
import com.Arhke.District.TUsers.TUser;
import com.Arhke.District.Utils.MainBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class Town extends MainBase {

	private String _name;
	private double _bank;
	private String _founderName;
	private UUID _founderId;
	/**
	 * add the members each time the plugin is loaded
	 */
	private Map<UUID, TUser> _members = new HashMap<>();
	private Claim _claim;
	private boolean _townOpen;
	private Location _spawn;
	private DataManager _dm;
	private FileManager _cm;
	public static final String NameKey = "name", BankKey = "bank", FounderIDKey = "founderid", ClaimKey = "claim",
	TownOpenKey = "townopen", SpawnKey = "spawn";
	Town(Main Instance, DataManager dm, FileManager cm) {
		super(Instance);
		_dm = dm;
		_cm = cm;
		_name = dm.getString(NameKey);
		_bank = dm.getDouble(BankKey);
		_founderId = dm.getUUID(FounderIDKey);
		_founderName = Bukkit.getOfflinePlayer(_founderId).getName();
		_claim = new Claim(dm.getDataManager(ClaimKey));
		_townOpen = dm.getBoolean(TownOpenKey);
		_spawn = dm.getLocation(SpawnKey);
		if(_name.length() <= 0 || _spawn == null || _founderId == null || getPlugin().getClaimManager().claimOverlaps(_claim)) {
			dm.delete(cm);
			cm.save();
			exceptDisable("Disabling Plugin, Town Data was Tampered with, Removing this Town Entry");
		}
		getPlugin().getClaimManager().registerClaim(_claim);

	}
	Town(Main Instance, TUser founder, String name, Location spawn, Claim c, FileManager cm) {
		super(Instance);
		_name = name;
		_bank = 0.0;
		_founderName = founder.getName();
		_founderId = founder.getID();
		_founderName = Bukkit.getOfflinePlayer(_founderId).getName();
		_claim = c;
		getPlugin().getClaimManager().registerClaim(c);
		_spawn = new Location(spawn.getWorld(), round(spawn.getX()), Math.floor(spawn.getY()), round(spawn.getZ()));
		_cm = cm;
		_dm = cm.getDataManager().getDataManager(_name);
	}

	public String getName() {
		return _name;
	}

	public LevelUtil getLevel() {
		Main.Values values = getPlugin().getValues();
		return new LevelUtil(values.LevelStart, values.LevelMult, values.LevelAdd,
				_members.size()*values.PlayerPoints + getClaim().getClaimBlocks() * values.ClaimPoints);
	}

	public void bankDeposit(double Amount){
		_bank += Amount;
	}
	public double getBank(){
		return _bank;
	}

	public Collection<TUser> getMembers() {
		return _members.values();
	}
	public int countOnlineMembers() {
		int ret = 0;
		for(TUser tu: _members.values()){
			if(tu.isOnline())
				ret++;
		}
		return ret;
	}
	public void registerMember(TUser user) {
		_members.put(user.getID(), user);
	}
	public void unregisterMember(TUser user) {
		_members.remove(user.getID());
	}

	public String getFounder() {
		return _founderName;
	}

	public Claim getClaim() {
		return _claim;
	}

	public void setTownOpen(boolean open){
		_townOpen = open;
	}
	public boolean isTownOpen(){
		return _townOpen;
	}
	public Location getSpawn() {
		return _spawn;
	}
	public void setSpawn(Location spawn) {
		_spawn = new Location(spawn.getWorld(), spawn.getX(), Math.floor(spawn.getY()), spawn.getZ());
	}


	public void remove() {
		getPlugin().getClaimManager().unregisterClaim(_claim);
		for(TUser tu: getMembers()){
			tu.setTown(null);
		}
		_dm.delete(_cm);
		_cm.save();
	}
	public void write() {
		_dm.set(_name, NameKey);
		_dm.set(_bank, BankKey);
		_dm.set(_founderId.toString(), FounderIDKey);
		_claim.write(_dm.getDataManager(ClaimKey));
		_dm.set(_townOpen, TownOpenKey);
		_dm.setLocation(_spawn, SpawnKey);
	}
	public void save() {
		write();
		_cm.save();
	}


}
