package com.Arhke.District;

import com.Arhke.District.MiscClasses.Rank.RankPerm;
import com.Arhke.District.TUsers.TUser;
import com.Arhke.District.Utils.MainBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners extends MainBase implements Listener{
	public Listeners(Main Instance){
		super(Instance);
	}
	@EventHandler
	void playerJoined(PlayerJoinEvent event) {

		getPlugin().getTUserManager().getOrNewTUser(event.getPlayer()).setOnline(true);
	}

	@EventHandler
	public void playerLeft(PlayerQuitEvent event) {
		getPlugin().getTUserManager().getOrNewTUser(event.getPlayer()).setOnline(false);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void placeBlock(BlockPlaceEvent event) {
		TUser tu = getPlugin().getTUserManager().getOrNewTUser(event.getPlayer());
		if (tu.isInTown() && tu.getTown().getClaim().isInside(event.getBlock().getLocation())) {
			if (!tu.getRank().checkPerm(event.getPlayer(), RankPerm.BUILD)) {
				event.setCancelled(true);
			}
		} else {
			if(getPlugin().getClaimManager().isClaimed(event.getBlock().getLocation())){
				event.getPlayer().sendMessage(ChatColor.RED + "You may not build here.");
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void breakBlock(BlockBreakEvent event) {
		TUser tu = getPlugin().getTUserManager().getOrNewTUser(event.getPlayer());
		if (tu.isInTown() && tu.getTown().getClaim().isInside(event.getBlock().getLocation())) {
			if (!tu.getRank().checkPerm(event.getPlayer(), RankPerm.BUILD)) {
				event.setCancelled(true);
			}
		} else {
			if(getPlugin().getClaimManager().isClaimed(event.getBlock().getLocation())){
				event.getPlayer().sendMessage(ChatColor.RED + "You may not build here.");
				event.setCancelled(true);
			}
		}
	}


}
