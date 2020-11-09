package com.Arhke.District.TUsers;

import java.util.*;

import com.Arhke.District.FileIO.FileManager;
import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.Main;
import com.Arhke.District.Utils.MainBase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.Arhke.District.MiscClasses.Rank;
import com.Arhke.District.Towns.Town;

public class TUser extends MainBase {

	private UUID _id;
	private String _name;
	private Rank _rank;
	private Town _town;
	private double _claims;
	private boolean _isOnline;
	private Set<String> _invites = new HashSet<>();
	private FileManager _cm;
	public static final String IDKey = "id", RankKey = "rank", TownKey = "town", ClaimsKey = "claims", InviteKey = "invites";

	/**
	 * from yml
	 * @param Instance
	 * @param fm
	 */
	TUser(Main Instance, FileManager fm) {
		super(Instance);
		_cm = fm;
		DataManager dm = fm.getDataManager();
		_id = dm.getUUID(IDKey);
		if(_id == null){
			_cm.deleteFile();
			exceptDisable(_cm.getFileName() + " has messed up Config, File has been Deleted");
			return;
		}
		_name = Bukkit.getOfflinePlayer(_id).getName();

		_town = getPlugin().getTownsManager().getTown(dm.getString(TownKey));
		if (_town != null){
			_town.registerMember(this);
		}
		try{
			_rank = Rank.valueOf(dm.getString(RankKey));
		}catch(IllegalArgumentException e){
			if(_town == null)
				_rank = Rank.NOMAD;
			else {
				_rank = Rank.RECRUIT;
			}
			dm.set(_rank.name(), RankKey);
		}
		_claims = dm.getDouble(ClaimsKey);
		_invites.addAll(dm.getStringList(InviteKey));
		_cm.save();
		_isOnline = false;
	}
	TUser(Main Instance, OfflinePlayer player, FileManager fm) {
		super(Instance);
		_id = player.getUniqueId();
		_name = player.getName();
		_rank = Rank.NOMAD;
		_isOnline = false;
		_cm = fm;

	}
	public UUID getID() {
		return _id;
	}
	public String getName() {
		return _name;
	}

	public Rank getRank() {
		return _rank;
	}
	public void setRank(Rank Rank){
		_rank = Rank;
	}

	public Town getTown() {
		return _town;
	}
	public void setTown(Town Town){
		if(Town != null)
			Town.registerMember(this);
		else{
			_rank = Rank.NOMAD;
		}
		_town = Town;
	}
	public void removeFromTown(){
		if(isInTown()){
			_town.unregisterMember(this);
		}
		_town = null;
		_rank = Rank.NOMAD;

	}
	public boolean isInTown(){
		return _town != null;
	}

	public double getClaims() {
		return _claims;
	}
	public void addClaimAmount(double Amount) {
		_claims += Amount;
	}

	public void setOnline(boolean online){
		_isOnline = online;
	}
	public boolean isOnline() {
		return _isOnline;
	}

	public void addInvite(String invite) {
		_invites.add(invite);
	}
	public Set<String> getInvites(){
		return _invites;
	}
	public boolean hasInvite(String invite) {
		return _invites.contains(invite);
	}
	public void removeInvite(String invite){
		_invites.remove(invite);
	}
	public void clearInvites(){
		_invites.clear();
	}

	public void save() {
		DataManager dm = _cm.getDataManager();
		dm.set(_id.toString(), IDKey);
		dm.set(_rank.name(), RankKey);
		dm.set(_town == null?null:_town.getName(), TownKey);
		dm.set(_claims, ClaimsKey);
		dm.set(new ArrayList<>(_invites), InviteKey);
		_cm.save();
	}
	
}
