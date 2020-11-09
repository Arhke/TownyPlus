//package com.Arhke.District.API;
//
//import com.Arhke.District.Main;
//import org.bukkit.entity.Player;
//
//import com.Arhke.District.TUsers.TUser;
//import com.Arhke.District.Towns.Town;
//
//public class API {
////plugins API
//
//	Main main;
//
//	public API(Main m) {
//		main = m;
//	}
//
//	public boolean userExists(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	public boolean townExists(Player player) {
//		Town t = main.townsManager.getTown(player);
//		if(t != null) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public boolean userInTown(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return false;
//		} else {
//			if(u.town != null) {
//				return true;
//			} else {
//				return false;
//			}
//		}
//	}
//
//	public int getUserClaims(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return -1;
//		} else {
//			return (int)u.claimAmount;
//		}
//	}
//
//	public String getUserRank(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return "";
//		} else {
//			return u.rank.RankName;
//		}
//	}
//
//	public String getTownname(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return "";
//		} else {
//			if(u.town != null) {
//				return u.town.name;
//			} else {
//				return "";
//			}
//		}
//	}
//
//	public int getTownBank(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return -1;
//		} else {
//			if(u.town != null) {
//				return u.town.townBank;
//			} else {
//				return -1;
//			}
//		}
//	}
//
//	public int getTownPopulation(Player player) {
//		TUser u = main.TuserManager.getTUser(player);
//		if(u == null) {
//			return -1;
//		} else {
//			if(u.town != null) {
//				return u.town.Population();
//			} else {
//				return -1;
//			}
//		}
//	}
//
//	public int getTownOnlinePopulation(Player player) {
//		Town t = main.townsManager.getTown(player);
//		if(t != null) {
//			return main.TuserManager.GetOnlineUsersInTownAmount(t);
//		} else {
//			return -1;
//		}
//	}
//
//	public int getTownOfflinePopulation(Player player) {
//		Town t = main.townsManager.getTown(player);
//		if(t != null) {
//			return main.TuserManager.GetOfflineUsersInTownAmount(t);
//		} else {
//			return -1;
//		}
//	}
//
//}
