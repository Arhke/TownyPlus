package com.Arhke.District.Commands;

import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.Main;
import com.Arhke.District.TUsers.TUser;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class
ClaimsCommand extends CommandsBase implements CommandExecutor{
    DataManager _config;
    public ClaimsCommand(Main Instance) {
        super(Instance, new String[] {
                "/claims buy <amount> - Buy a specified amount of claims",
                "/claims give <amount> <Player> - Give a Player a Specified Amount of Claim Blocks"
        });
        _config = getPlugin().getConfigManager().getDataManager("Commands", "Claims");
        List<String> helpList = _config.getStringList("Help");
        if(helpList.size() != 0){
            _helpString = new String[helpList.size()];
            for(int i = 0; i < helpList.size(); i++){
                _helpString[i] = helpList.get(i);
            }
            _helpString = tcm(_helpString);
        }

    }

    public double ClaimPrice = getPlugin().getConfigManager().getDouble("Values", "ClaimPrice");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("buy")) {
                if(!isPlayer(sender)){
                    sender.sendMessage(tcm(_config.getString("Buy", "NotPlayer")));
                    return true;
                }
                Player player = (Player)sender;
                MultiArray ma;
                if ((ma = verifyArgs(sender, args, new Class[]{int.class}, "/claims buy <amount>")) != null) {
                    if(getPlugin().getHook().getMoney(player) < ma.getInt(0) * ClaimPrice){
                        player.sendMessage(tcm(_config.getString("Buy", "NotEnoughMoney")));
                        return true;
                    }
                    if(!getPlugin().getHook().withdrawMoney(player, ma.getInt(0) * ClaimPrice).transactionSuccess()){
                        player.sendMessage(tcm(_config.getString("Buy","SomethingWrong")));
                        return true;
                    }
                    TUser tu;
                    (tu = getPlugin().getTUserManager().getOrNewTUser(player)).addClaimAmount(ma.getInt(0));
                    tu.save();
                }
            }
            else if (args[0].equalsIgnoreCase("give")) {
                MultiArray ma;
                if ((ma = verifyArgs(sender, args, new Class[]{int.class, OfflinePlayer.class}, "/claims give <amount> <Player>")) != null) {
                    if(sender.isOp() || sender.hasPermission(_config.getString("Give", "Permission"))){
                        TUser tu = getPlugin().getTUserManager().getOrNewTUser(ma.getOfflinePlayer(1));
                        tu.addClaimAmount(ma.getInt(0));
                        if(ma.getOfflinePlayer(1).isOnline()){
                            ma.getOfflinePlayer(1).getPlayer().sendMessage(tcm(_config.getString("Give", "SuccessReceiver"), ma.getInt(0)));
                        }
                        tu.save();
                    }else{
                        sender.sendMessage(tcm(_config.getString("Give", "NotEnoughPermission")));
                        return true;
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("send")) {
                MultiArray ma;
                if ((ma = verifyArgs(sender, args, new Class[]{int.class, OfflinePlayer.class}, "/claims send <amount> <Player>")) != null) {
                    if(!isPlayer(sender)){
                        sender.sendMessage(tcm(_config.getString("Send", "NotPlayer")));
                        return true;
                    }
                    TUser tu = getPlugin().getTUserManager().getOrNewTUser((Player)sender);
                    if(tu.getClaims() < ma.getInt(0)){
                        sender.sendMessage(tcm(_config.getString("Send", "NotEnoughClaims")));
                        return true;
                    }
                    TUser gtu = getPlugin().getTUserManager().getOrNewTUser(ma.getOfflinePlayer(1));
                    tu.addClaimAmount(-1*ma.getInt(0));
                    gtu.addClaimAmount(ma.getInt(0));
                    sender.sendMessage(tcm(_config.getString("Send", "SuccessSender"), ma.getInt(0), ma.getOfflinePlayer(1).getName()));
                    if(ma.getOfflinePlayer(1).isOnline()){
                        ma.getOfflinePlayer(1).getPlayer().sendMessage(tcm(_config.getString("Send", "SuccessReceiver"), ma.getInt(0), sender.getName()));
                    }
                    tu.save();
                    gtu.save();
                }
            }
            else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(getHelp());
            }
            else {
                sender.sendMessage(tcm(_config.getString("UnknownCommand")));
            }
        } else {
            sender.sendMessage(getHelp());
        }


        return true;
    }
}
