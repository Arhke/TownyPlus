package com.Arhke.District;

import com.Arhke.District.Commands.ClaimsCommand;
import com.Arhke.District.Commands.CommandsBase;
import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.FileIO.FileManager;
import com.Arhke.District.FileIO.DirectoryManager;
import com.Arhke.District.LandClaim.ClaimManager;
import com.Arhke.District.MiscClasses.Rank;
import com.Arhke.District.Utils.Base;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.Arhke.District.Helpers.Hook;
import com.Arhke.District.Commands.TownyCommand;
import com.Arhke.District.TUsers.TUserManager;
import com.Arhke.District.Towns.TownsManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

	ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
	
	private TownsManager _townsManager;
	private TUserManager _tuserManager;
	private ClaimManager _claimManager;
	private DataManager _config;
	private Values _values;
	
	private Hook _hook;


	File _configFile;
	File _townDataFile;
	File _claimsDataFile;
	File _tUserDataFolder;
	public static long TimerTick = 1200;
	public static List<Double> GiveArray = new ArrayList<>();
	public void onEnable() {

		//File Address Initialization
		_configFile = Paths.get(getDataFolder().toString(), "config.yml").toFile();
		_townDataFile = Paths.get(getDataFolder().toString(), "towns.yml").toFile();
		_tUserDataFolder = Paths.get(getDataFolder().toString(), "TUser").toFile();

		//Load Data and Config
		loadConfigData();

		//registers all of the hooks
		_hook = new Hook(this);

		//Register Commands
		CommandsBase userCommand = new TownyCommand(this), claimCommand = new ClaimsCommand(this);
		getCommand(userCommand.getCmd()).setExecutor(userCommand);
		getCommand(claimCommand.getCmd()).setExecutor(claimCommand);

		getServer().getPluginManager().registerEvents(new Listeners(this), this);
		consoleSender.sendMessage("[Main] " + ChatColor.GREEN + "Towny+ started loading...");
//		api = new API(this);
		new TaskTimer(GiveArray).runTaskTimerAsynchronously(this, 600, 1200);
		consoleSender.sendMessage("[Main] " + ChatColor.GREEN + "Towny+ is fully loaded!");
	}
	
	public void onDisable() {
		//save everything to config
		getTUserManager().saveAllUsers();
	}
	public TownsManager getTownsManager() {
		return _townsManager;
	}
	public TUserManager getTUserManager() {
		return _tuserManager;
	}
	public ClaimManager getClaimManager() { return _claimManager;}
	public DataManager getConfigManager() {return _config;}
	public Values getValues() {
		return _values;
	}
	public Hook getHook() {return _hook;}
	
	public void loadConfigData() {
		saveResource(_configFile.getName(), false);
		_config = new FileManager(_configFile).getDataManager();
		_values = new Values(_config.getDataManager("Values"));
		_claimManager = new ClaimManager(this);
		_townsManager = new TownsManager(this, new FileManager(_townDataFile));
		_tuserManager = new TUserManager(this, new DirectoryManager(_tUserDataFolder));

	}
	public class Values {
		public double TownCreationCost, PlayerPoints, ClaimPoints, LevelStart, LevelMult, LevelAdd;
		public Particle ClaimParticle = Particle.REDSTONE;
		public Values(DataManager dm) {
			TownCreationCost = dm.getDouble("TownCreationCost");
			PlayerPoints = dm.getDouble("PlayerPoints");
			ClaimPoints = dm.getDouble("ClaimPoints");
			LevelStart = dm.getDouble("LevelStart");
			LevelMult = dm.getDouble("LevelMult");
			LevelAdd = dm.getDouble("LevelAdd");
			try {
				ClaimParticle = Particle.valueOf(dm.getString("ClaimParticle"));
			}catch(IllegalArgumentException e){
				Base.error("Invalid ClaimParticle Configuration");
				e.printStackTrace();
			}
			DataManager rankdm = dm.getDataManager("Ranks");
			Rank.RECRUIT.setPerm(rankdm.getStringList("Recruit"));
			Rank.RESIDENT.setPerm(rankdm.getStringList("Resident"));
			Rank.COUNCIL.setPerm(rankdm.getStringList("Council"));
			Rank.FOUNDER.setPerm(rankdm.getStringList("Founder"));
			DataManager claimmultidm = dm.getDataManager("ClaimMulti");
			for(String key: claimmultidm.getConfig().getKeys(false)){
				GiveArray.add(claimmultidm.getDouble(key));
			}
		}
	}
	class TaskTimer extends BukkitRunnable {
		List<Double> _give;
		long _timeStamp = System.currentTimeMillis();
		public TaskTimer(List<Double> give){
			_give = give;
		}
		@Override
		public void run() {	
			for(Player player:Bukkit.getOnlinePlayers()){
				for(int i = 0; i < _give.size(); i++) {
					if(player.hasPermission("claimgive." + i)) {
						getTUserManager().getOrNewTUser(player).addClaimAmount(_give.get(i));
						break;
					}
				}
			}
		}

	}
	
}
