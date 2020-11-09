package com.Arhke.District.Helpers;

import com.Arhke.District.Main;
import com.Arhke.District.Utils.MainBase;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Hook extends MainBase {
	
	private Economy _econ;
	private PlaceHolder _ph;
	public static String PHAPIName = "PlaceholderAPI", VaultName = "Vault";
	public Hook(Main Instance){
		super(Instance);
		if (!setUpVault()){
			exceptDisable("Disabed due to no Vault Dependency Found");
		}if (!setUpPHAPI()){
			exceptDisable("Disabed due to no PlaceHolderAPI Dependency Found");
		}

	}
	public Economy getEcon(){
		return _econ;
	}
	private boolean setUpVault() {
		if (Bukkit.getPluginManager().getPlugin(VaultName) == null){
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null){
			//s
			return false;
		}
		_econ = rsp.getProvider();
		return _econ != null;
	}
	private boolean setUpPHAPI() {
		if(Bukkit.getPluginManager().getPlugin(PHAPIName) == null){
			return false;
		}
		_ph = new PlaceHolder(getPlugin());
		_ph.register();
		return true;
	}
	public double getMoney(OfflinePlayer player) {
		return _econ.getBalance(player);
	}
	
	public EconomyResponse depositMoney(OfflinePlayer player, double money) {
		return _econ.depositPlayer(player, money);
	}
	public EconomyResponse withdrawMoney(OfflinePlayer player, double money){
		return _econ.withdrawPlayer(player, money);
	}
	public String getCurrencyPlural() {
		return _econ.currencyNamePlural();
	}
	
}
