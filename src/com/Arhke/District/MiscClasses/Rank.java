package com.Arhke.District.MiscClasses;


import com.Arhke.District.Utils.Base;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Rank {
	NOMAD("Not In Town", -1),
	RECRUIT("NewComer", 0),
	RESIDENT("Resident", 1),
	COUNCIL("Council", 2),
	FOUNDER("Founder", 3);

	String _name;
	int _rankID;
	List<Integer> _perms = new ArrayList<>();

	Rank(String Name, int RankID) {
		_name = Name;
		_rankID = RankID;
	}
	public static Rank getRank(int id){
		for(Rank rank: Rank.values()){
			if(rank.getID() == id){
				return rank;
			}
		}
		return null;
	}
	public String getName() {
		return _name;
	}
	public int getID() {
		return _rankID;
	}
	public boolean checkPerm(Player Player, RankPerm Perm){
		if (_perms.stream().anyMatch(i -> i == Perm.getID()))
			return true;
		Player.sendMessage(ChatColor.RED + "You don't have permission to "+ Perm.getDesc() + "!");
		return false;
	}
	public boolean checkPerm(RankPerm Perm){
		return Arrays.asList(_perms).contains(Perm.getID());
	}
	public void setPerm(List<String> perms){
		_perms.clear();
		for(String perm:perms){
			try{
				_perms.add(RankPerm.valueOf(perm).getID());
			}catch(IllegalArgumentException e){
				Base.error("Incorrect Perm Config " + perm + " for " + this.getName());
				e.printStackTrace();
			}

		}
	}
	public enum RankPerm{
		CLAIM("Claim Land", 0), BUILD("Build", 1),
		SETSPAWN("Set Town Spawn", 2), TOWNSPAWN("Tp to Town Spawn", 3),
		KICK("Kick Members", 4), INVITE("Invite Members", 5),
		DEPOSIT("Deposit Money to Bank", 7), WITHDRAW("Withdraw Money from Bank", 8),
		OPENTOWN("Open/Close Town", 9), PROMOTEDEMOTE("Promote/Demote Town Members", 9);

		private String _permDesc;
		private int _permID;

		RankPerm(String PermDesc, int PermID) {
			_permDesc = PermDesc;
			_permID = PermID;
		}
		public int getID() {
			return _permID;
		}
		public String getDesc() {
			return _permDesc;
		}

	}
}

