package com.Arhke.District.TUsers;

import java.util.*;

import com.Arhke.District.FileIO.FileManager;
import com.Arhke.District.FileIO.DirectoryManager;
import com.Arhke.District.Utils.MainBase;
import com.Arhke.District.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TUserManager extends MainBase {

	
	Map<UUID, TUser> _userData = new HashMap<UUID, TUser>();
	DirectoryManager _dm;
	public TUserManager(Main Instance, DirectoryManager DM) {
		super(Instance);
		_dm = DM;
		for (FileManager cm: DM.getCMList()){
			TUser tu = new TUser(getPlugin(), cm);
			_userData.put(tu.getID(), tu);
		}
	}

	/**
	 * @param ID
	 * @return TUser if ID found, null If not found
	 */
	public TUser getTUser(UUID ID) {
		return _userData.get(ID);
	}
	public TUser getTUser(OfflinePlayer Player) {
		return getTUser(Player.getUniqueId());
	}
	public TUser getOrNewTUser(OfflinePlayer player) {
		TUser tu;
		if((tu = getTUser(player.getUniqueId())) == null){
			_userData.put(player.getUniqueId(), tu = new TUser(getPlugin(), player, _dm.getOrNewCM(player.getUniqueId().toString())));
			tu.save();
		}
		return tu;
	}

	
	public boolean userExists(Player player) {
		if(getTUser(player) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public Collection<TUser> GetAllUsers() {
		return _userData.values();
	}

	
//	public int GetOnlineUsersInTownAmount(Town t) {
//		int am = 0;
//		for(TUser user : UserData) {
//			if(user.Online) {
//				if(t.ContainsPlayer(user)) {
//					am++;
//				}
//			}
//		}
//		return am;
//	}
//
//	public int GetOfflineUsersInTownAmount(Town t) {
//		int am = 0;
//		for(TUser user : UserData) {
//			if(user.Online == false) {
//				if(t.ContainsPlayer(user)) {
//					am++;
//				}
//			}
//		}
//		return am;
//	}
	

	public void saveUser(UUID ID) {
		TUser tu;
		if((tu = getTUser(ID)) != null){
			info("Successfully saved data for userID " + ID.toString());
			tu.save();
		}else{
			warn("You are trying to save data for an User ID that Doesn't exist");
		}
	}
	
	public void saveAllUsers() {
		for(TUser tu: _userData.values()){
			tu.save();
		}
		info("Sucessfully Saved Data for All Users");
	}
	
}
