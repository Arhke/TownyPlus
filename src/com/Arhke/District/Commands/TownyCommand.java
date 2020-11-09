package com.Arhke.District.Commands;

import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.LandClaim.Claim;
import com.Arhke.District.Main;
import com.Arhke.District.MiscClasses.Rank;
import com.Arhke.District.MiscClasses.Rank.RankPerm;
import com.Arhke.District.TUsers.TUser;
import com.Arhke.District.Towns.Town;
import com.Arhke.District.Utils.Direction;
import com.Arhke.District.Utils.Vector2D;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TownyCommand extends CommandsBase implements CommandExecutor{
	DataManager _config;
	public TownyCommand(Main Instance) {
		super(Instance, new String[] {
				"/towny create <TownName> - Creates a Town with the name <TownName>",
				"/towny disband - Disband the town you are in",

				"/towny invite <Player> - Invite a player to your town.",
				"/towny kick <Player> - Kick a player from your town.",

				"/towny spawn - Sends you to your Town Spawn",
				"/towny setspawn - Set the spawn for your Town",

				"/towny deposit <Amount> - Deposit money into your Town's bank",
				"/towny withdraw <Amount> - Withdraw money from your Town's bank",

				"/towny list - List all existing towns",
				"/towny listOpen - List all existing open towns",

				"/towny info <TownName> - See Stats about a specific town",
				"/towny playerinfo <Player> - See Stats about a specific player",
				"/towny claim <Direction> <Amount> - Adjust claims",
				"/towny showclaim - Show particles for claim border",


				"/towny promote <UserName> <Rank> - Promote/Demote a player to the given rank",
				"/towny openTown <true/false> - Set the 'open' status of your town"
		});
		_config = getPlugin().getConfigManager().getDataManager("Commands", "Towny");
		List<String> helpList = _config.getStringList("Help");
		if(helpList.size() != 0){
			_helpString = new String[helpList.size()];
			for(int i = 0; i < helpList.size(); i++){
				_helpString[i] = helpList.get(i);
			}
			_helpString = tcm(_helpString);
		}
	}
	@Override
	public boolean onCommand(CommandSender Sender, Command cmd, String arg, String[] args) {

		if(isPlayer(Sender)) {
			Player Player = (Player)Sender;
			if(args.length > 0) {
				MultiArray ma;
				if(args[0].equalsIgnoreCase("create")){
					if((ma = verifyArgs(Sender, args, new Class[]{String.class}, "/"+getCmd()+" create <Town Name>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						if(user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Create", "AlreadyInTown")));
							return true;
						}
						if (getPlugin().getHook().getMoney(Player) < getPlugin().getValues().TownCreationCost) {
							Player.sendMessage(tcm(_config.getString("Create", "NotEoughMoney"),
									getPlugin().getHook().getCurrencyPlural(), getPlugin().getValues().TownCreationCost));
							return true;
						}
						String name = ma.getString(0);
						if (getPlugin().getTownsManager().getTown(name) != null) {
							Player.sendMessage(tcm(_config.getString("Create", "TownAlreadyExists")));
							return true;
						}
						if (name.length() < 3 || name.length() > 17) {
							Player.sendMessage(tcm(_config.getString("Create", "NameLength")));
							return true;
						}
						Claim c = new Claim(new Vector2D(round(Player.getLocation().getX()-5),round(Player.getLocation().getZ()-5)),
								new Vector2D(round(Player.getLocation().getX()+5),round(Player.getLocation().getZ()+5)));
						if(getPlugin().getClaimManager().claimOverlaps(c)){
							Player.sendMessage(tcm(_config.getString("Create", "ClaimOverlaps")));
							return true;
						}
						if(!getPlugin().getHook().withdrawMoney(Player, getPlugin().getValues().TownCreationCost).transactionSuccess()){
							Player.sendMessage(tcm(_config.getString("Create", "NotEnoughFunds")));
							return true;
						}
						if(user.getClaims() < 100){
							Player.sendMessage(tcm(_config.getString("Create", "NotEnoughClaims")));
							return true;
						}

						Town town = getPlugin().getTownsManager().createTown(user, name, Player.getLocation(), c);
						user.setRank(Rank.FOUNDER);
						user.setTown(town);
						user.addClaimAmount(-100);
						town.getClaim().showClaim(getPlugin(), Player);
						town.save();
						user.save();
						Player.sendMessage(tcm(_config.getString("Create", "SuccessPaid"), getPlugin().getValues().TownCreationCost, getPlugin().getHook().getCurrencyPlural()));
						Player.sendMessage(tcm(_config.getString("Create", "SuccessCreate"), name));
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("disband")) {
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
					if(!user.isInTown()) {
						Player.sendMessage(tcm(_config.getString("Disband", "NotInTown")));
						return true;
					}
					getPlugin().getTownsManager().removeTown(user.getTown());
					Player.sendMessage(tcm(_config.getString("Disband", "Success")));
				}

				else if(args[0].equalsIgnoreCase("spawn")) {
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
					if(!user.isInTown()) {
						Player.sendMessage(tcm(_config.getString("Spawn", "NotInTown")));
						return true;
					}
					Player.sendMessage(tcm(_config.getString("Spawn", "Success")));
					Player.teleport(user.getTown().getSpawn());

				}
				else if(args[0].equalsIgnoreCase("setSpawn")) {
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
					if(!user.isInTown()) {
						Player.sendMessage(tcm(_config.getString("SetSpawn", "NotInTown")));
						return true;
					}
					if (user.getRank().checkPerm(Player, RankPerm.SETSPAWN)) {
						if (user.getTown().getClaim().isInside(Player.getLocation())) {
							user.getTown().setSpawn(Player.getLocation());
							Player.sendMessage(tcm(_config.getString("SetSpawn", "Success")));
						} else {
							Player.sendMessage(tcm(_config.getString("SetSpawn", "NotInClaim")));
						}
					}

				}

				else if(args[0].equalsIgnoreCase("deposit")) {
					if((ma = verifyArgs(Sender, args, new Class[]{double.class}, "/" + getCmd() + " deposit <Amount>")) != null) {
						double dmoney = ma.getDouble(0);
						if (dmoney > getPlugin().getHook().getMoney(Player)) {
							Player.sendMessage(tcm(_config.getString("Deposit", "NotEnoughMoney")));
							return true;
						}
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						if(!user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Deposit", "NotEnoughMoney")));
							return true;
						}
						if(!user.getRank().checkPerm(Player, RankPerm.DEPOSIT)) return true;
						if(getPlugin().getHook().withdrawMoney(Player, dmoney).transactionSuccess()) {
							user.getTown().bankDeposit(dmoney);
							Player.sendMessage(tcm(_config.getString("Deposit", "Success"), dmoney, getPlugin().getHook().getCurrencyPlural(), user.getTown().getBank()));
							user.getTown().save();
						}
						else{
							Player.sendMessage(tcm(_config.getString("Deposit", "SomethingWrong")));
							return true;
						}
					}
				}
				else if(args[0].equalsIgnoreCase("withdraw")) {
					if((ma = verifyArgs(Sender, args, new Class[]{double.class}, "/" + getCmd() + " withdraw <Amount>")) != null) {

						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						if(!user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Withdraw", "NotInTown")));
							return true;
						}
						if(!user.getRank().checkPerm(Player, RankPerm.DEPOSIT)) return true;
						double wmoney = ma.getDouble(0);
						if (wmoney > user.getTown().getBank()) {
							Player.sendMessage(tcm(_config.getString("Withdraw", "NotEnoughMoney")));
							return true;
						}
						if(getPlugin().getHook().withdrawMoney(Player, wmoney).transactionSuccess()) {
							user.getTown().bankDeposit(-1d*wmoney);
							Player.sendMessage(tcm(_config.getString("Withdraw", "Success"), wmoney, getPlugin().getHook().getCurrencyPlural(), user.getTown().getBank()));
							user.getTown().save();
						}
						else{
							Player.sendMessage(tcm(_config.getString("Withdraw", "SomethingWrong")));
							return true;
						}
					}
				}

				else if(args[0].equalsIgnoreCase("open")) {
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);

					if (!user.isInTown()) {
						Player.sendMessage(tcm(_config.getString("Open", "NotInTown")));
						return true;
					}
					if (user.getRank().checkPerm(Player, RankPerm.OPENTOWN)) {
						user.getTown().setTownOpen(true);
						Player.sendMessage(tcm(_config.getString("Open", "Success")));

					}
					user.getTown().save();
				}
				else if(args[0].equalsIgnoreCase("close")) {
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);

					if (!user.isInTown()) {
						Player.sendMessage(tcm(_config.getString("Close", "NotInTown")));
						return true;
					}
					if (user.getRank().checkPerm(Player, RankPerm.OPENTOWN)) {
						user.getTown().setTownOpen(false);
						Player.sendMessage(tcm(_config.getString("Close", "Success")));
						user.getTown().save();
					}
				}
				else if(args[0].equalsIgnoreCase("playerinfo")) {
					if ((ma = verifyArgs(Sender, args, new Class[]{Player.class}, "/townyplus playerinfo <player>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(ma.getPlayer(0));

						Player.sendMessage(tcm(_config.getString("PlayerInfo", "SuccessName"), user.getName()));
						if (user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("PlayerInfo", "SuccessInTown"),
									user.getRank().getName(), user.getTown().getName(), user.getTown().getMembers().size()));
						}
						Player.sendMessage(tcm(_config.getString("PlayerInfo", "SuccessPlayer"),
								user.getClaims(), getPlugin().getHook().getMoney(ma.getPlayer(0))));
					}
				}
				else if(args[0].equalsIgnoreCase("info")) {
					if(args.length > 1) {
						Town t = getPlugin().getTownsManager().getTown(args[1]);
						int temp;
						if(t != null) {
							LevelUtil lu = t.getLevel();
							Player.sendMessage(tcm(_config.getString("Info", "Success"),  t.getName(), t.getMembers().size(),
									temp = t.countOnlineMembers(), t.getMembers().size()-temp, t.getFounder(),
									t.getClaim().getV2().getX()-t.getClaim().getV1().getX(), t.getClaim().getV2().getZ()-t.getClaim().getV1().getZ(),
									lu.getLevel()));
						} else {
							Player.sendMessage(tcm(_config.getString("Info", "NoTownFound")));
						}
					} else {
						Player.sendMessage(tcm(_config.getString("Info", "NoTownSpecified")));
					}
				}

				else if(args[0].equalsIgnoreCase("invite")) {
					if ((ma = verifyArgs(Sender, args, new Class[]{Player.class}, "/" + cmd.getName() + " invite <OnlinePlayer>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						TUser kuser = getPlugin().getTUserManager().getOrNewTUser(ma.getPlayer(0));
						if (!user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Invite", "NotInTown")));
							return true;
						}
						if (kuser.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Invite", "AlreadyInTown")));
							return true;
						}
						if (user.getRank().checkPerm(Player, RankPerm.INVITE)) {
							kuser.addInvite(user.getTown().getName());
							Player.sendMessage(tcm(_config.getString("Invite", "SuccessSender"), ma.getPlayer(0).getName()));
							Player.sendMessage(tcm(_config.getString("Invite", "SuccessReceiver"), user.getTown().getName()));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("kick")) {
					if((ma = verifyArgs(Sender, args, new Class[]{OfflinePlayer.class}, "/" + cmd.getName() + " kick <OfflinePlayer>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						TUser kuser = getPlugin().getTUserManager().getTUser(ma.getOfflinePlayer(0));
						if(!user.isInTown()){
							Player.sendMessage(tcm(_config.getString("Kick", "NotInTown")));
							return true;
						}

						if (kuser == null || !kuser.isInTown() || !user.getTown().getName().equals(kuser.getTown().getName())) {
							Player.sendMessage(tcm(_config.getString("Kick", "NotInYourTown")));
							return true;
						}
						if (user.getRank().checkPerm(Player, RankPerm.KICK)) {
							if (user.equals(kuser)) {
								Player.sendMessage(tcm(_config.getString("Kick", "KickSelf")));
								return true;
							}
							if(ma.getOfflinePlayer(0).isOnline()){
								ma.getOfflinePlayer(0).getPlayer().sendMessage(tcm(_config.getString("Kick", "KickNotification")));
							}
							Player.sendMessage(tcm(_config.getString("Kick", "Success"), ma.getOfflinePlayer(0).getName()));
							kuser.removeFromTown();
							kuser.save();
						}
					}
				}

				else if(args[0].equalsIgnoreCase("promote")) {
					if ((ma = verifyArgs(Sender, args, new Class[]{OfflinePlayer.class}, "/" + cmd.getName() + " promote <OfflinePlayer>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						TUser kuser = getPlugin().getTUserManager().getOrNewTUser(ma.getPlayer(0));
						if (!user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Promote", "NotInTown")));
							return true;
						}
						if (!kuser.isInTown() || !kuser.getTown().getName().equals(user.getTown().getName())) {
							Player.sendMessage(tcm(_config.getString("Promote", "NotInYourTown")));
							return true;
						}
						if (user.getRank().checkPerm(Player, RankPerm.PROMOTEDEMOTE)) {
							if(user.getRank().getID() <= kuser.getRank().getID() + 1){
								Player.sendMessage(tcm(_config.getString("Promote", "NotHighEnoughRank")));
								return true;
							}
							Rank rank = Rank.getRank(kuser.getRank().getID() + 1);
							kuser.setRank(rank == null? kuser.getRank():rank);
							Player.sendMessage(tcm(_config.getString("Promote", "Success"), kuser.getName(), kuser.getRank().getName()));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("demote")) {
					if ((ma = verifyArgs(Sender, args, new Class[]{OfflinePlayer.class}, "/" + cmd.getName() + " promote <OfflinePlayer>")) != null) {
						if(Player.getUniqueId().equals(ma.getOfflinePlayer(0).getUniqueId())){
							Player.sendMessage(ChatColor.RED + "None Shall Demote Thyself");
							return true;
						}
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						TUser kuser = getPlugin().getTUserManager().getOrNewTUser(ma.getPlayer(0));
						if (!user.isInTown()) {
							Player.sendMessage(tcm(_config.getString("Demote", "NotInTown")));
							return true;
						}
						if (!kuser.isInTown() || !kuser.getTown().getName().equals(user.getTown().getName())) {
							Player.sendMessage(tcm(_config.getString("Demote", "NotInYourTown")));
							return true;
						}
						if (user.getRank().checkPerm(Player, RankPerm.PROMOTEDEMOTE)) {
							if(user.getRank().getID() <= kuser.getRank().getID()){
								Player.sendMessage(tcm(_config.getString("Demote", "NotHighEnoughRank")));
								return true;
							}
							if(kuser.getRank().getID() <= 0){
								Player.sendMessage(tcm(_config.getString("Demote", "CantBeDemoted")));
								return true;
							}
							Rank rank = Rank.getRank(kuser.getRank().getID() - 1);
							kuser.setRank(rank == null? kuser.getRank():rank);
							Player.sendMessage(tcm(_config.getString("Demote", "Success"), kuser.getName(), kuser.getRank().getName()));
						}
					}
				}

				else if(args[0].equalsIgnoreCase("list")) {
                    Player.sendMessage(tcm(_config.getString("List", "Header")));
                    for (Town t : getPlugin().getTownsManager().getTowns()){
						Player.sendMessage(tcm(_config.getString("List", "Content"), t.getName(), t.getMembers().size()));
					}
				}
				else if(args[0].equalsIgnoreCase("listopen")) {
                    Player.sendMessage(tcm(_config.getString("ListOpen", "Header")));
                    for (Town t : getPlugin().getTownsManager().getTowns()){
                        if(t.isTownOpen()) {
                            Player.sendMessage(tcm(_config.getString("ListOpen", "Content"), t.getName(), t.getMembers().size()));
                        }
                    }
				}

				else if(args[0].equalsIgnoreCase("claim")) {
					if((ma = verifyArgs(Sender, args, new Class[]{Direction.class, Integer.class}, "/" + cmd.getName() + " claim <Direction> <Amount>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						if(!user.isInTown()){
							Player.sendMessage(tcm(_config.getString("Claim", "NotInTown")));
							return true;
						}
						if (user.getRank().checkPerm(Player, RankPerm.CLAIM)){
							int claimsNeeded = 0;
							if(user.getClaims() < (claimsNeeded = user.getTown().getClaim().claimsNeeded(ma.getDirection(0), ma.getInt(1)))){
								Player.sendMessage(tcm(_config.getString("Claim", "NotEnoughClaims")));
								return true;
							}
							if (claimsNeeded*-1 > user.getTown().getClaim().getClaimBlocks()-1){
								Player.sendMessage(ChatColor.RED + "You need at least 1 block left in your claim");
								return true;
							}
							user.addClaimAmount(-1*claimsNeeded);
							user.getTown().getClaim().expand(ma.getDirection(0), ma.getInt(1));
							Player.sendMessage(tcm(_config.getString("Claim", "Success"), ma.getDirection(0).getName(), ma.getInt(1)));
							user.getTown().save();
							user.save();
							user.getTown().getClaim().showClaim(getPlugin(), Player);
						}
					}
				}
				else if(args[0].equalsIgnoreCase("showclaim")){
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
					if(!user.isInTown()){
						Player.sendMessage(tcm(_config.getString("ShowClaim", "NotInTown")));
						return true;
					}
					Player.sendMessage(tcm(_config.getString("ShowClaim", "Success")));
					user.getTown().getClaim().showClaim(getPlugin(), Player);
				}
				else if(args[0].equalsIgnoreCase("join")) {
					if((ma = verifyArgs(Sender, args, new Class[]{String.class}, "/" + cmd.getName() + " join <TownName>")) != null) {
						TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
						if(user.isInTown()){
							Player.sendMessage(tcm(_config.getString("Join", "AlreadyInTown")));
							return true;
						}
						Town town = getPlugin().getTownsManager().getTown(ma.getString(0));
						if(town == null){
							Player.sendMessage(tcm(_config.getString("Join", "TownNotFound")));
							return true;
						}
						if(town.isTownOpen() || user.hasInvite(town.getName())){
							Player.sendMessage(tcm(_config.getString("Join", "Success"),town.getName()));
							user.clearInvites();
							user.setTown(town);
							user.setRank(Rank.RECRUIT);
							town.registerMember(user);
							town.save();
							user.save();
						}else{
							Player.sendMessage(tcm(_config.getString("Claim", "CantJoin")));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("leave")) {
					TUser user = getPlugin().getTUserManager().getOrNewTUser(Player);
					if(!user.isInTown()){
						Player.sendMessage(tcm(_config.getString("Leave", "NotInTown")));
						return true;
					}
					if(user.getRank() == Rank.FOUNDER){
						Player.sendMessage(tcm(_config.getString("Leave", "FounderLeave")));
						return true;
					}
					user.removeFromTown();
					user.save();
					Player.sendMessage(tcm(_config.getString("Leave", "Success")));

				}
				else if(args[0].equalsIgnoreCase("help")) {
					Player.sendMessage(getHelp());
				}
				else {
					Player.sendMessage(ChatColor.RED + "That is not an existing subcommand!");
					Player.sendMessage(ChatColor.DARK_RED + "try '/townyplus help' for help");
				}
			}
			else {
				Player.sendMessage(getHelp());
			}
		} else {
			Sender.sendMessage(ChatColor.RED + "You need to be a player to use these commands!");
		}
		return true;
	}
}
