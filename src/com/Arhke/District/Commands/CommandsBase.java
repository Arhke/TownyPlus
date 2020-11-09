package com.Arhke.District.Commands;

import com.Arhke.District.Utils.Direction;
import com.Arhke.District.Utils.MainBase;
import com.Arhke.District.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CommandsBase extends MainBase implements CommandExecutor {
    private String _commandName;
    protected String[] _helpString;

    public CommandsBase(Main Instance){
        this(Instance, null);
    }
    public CommandsBase(Main Instance, String[] HelpString) {
        super(Instance);
        _commandName = this.getClass().getSimpleName().toLowerCase().replace("command", "");
        _helpString = new String[]{ChatColor.RED + "[Error] Unknown SubCommand Please Type /" + getCmd() + " Help or Plugin Documentation."};
        _helpString = HelpString == null? _helpString: helpBuilder(HelpString);
    }

    public String getCmd() {
        return _commandName;
    }
    public String[] getHelp() {
        return _helpString;
    }

    public static boolean isPlayer(CommandSender Sender){
        return Sender instanceof Player;
    }
    private String[] helpBuilder(String[] SubCommands){
        String[] ret = new String[SubCommands.length+1];
        ret[0] = ChatColor.GOLD + "[" + getPlugin().getName() + " Command Help] " + "/" + getCmd() + " Usage:";
        for(int i = 0; i < SubCommands.length; i++){
            ret[i+1] = ChatColor.AQUA + SubCommands[i].replace("-", ChatColor.DARK_AQUA + "-");
        }
        return ret;
    }

    // ==<Helper Methods>==

    /**
     * Returns null if the arguments counts is not verified
     */
    public MultiArray verifyArgs(CommandSender Sender, String[] Arguments, Class[] Types, String Usage){
        if(Arguments.length < 1){
            Sender.sendMessage(getHelp());
            return null;
        }

        String[] argsList = Arrays.copyOfRange(Arguments, 1, Arguments.length);
        if(argsList.length == Types.length){
            MultiArray ma = new MultiArray();
            for(int i = 0; i < Types.length; i++){
                String arg = argsList[i];
                if (Types[i].equals(int.class) || Types[i].equals(Integer.class)){
                    try {
                        ma.add(Integer.parseInt(arg));
                    } catch (NumberFormatException exception) {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is not a Integer, Please Type an Integer.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(double.class) || Types[i].equals(Double.class)){
                    try {
                        ma.add(Double.parseDouble(arg));
                    } catch (NumberFormatException exception) {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is not a Double, Please Type an Double.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(boolean.class) || Types[i].equals(Boolean.class)){
                    if (arg.equalsIgnoreCase("true")){
                        ma.add(Boolean.TRUE);
                    }else if(arg.equalsIgnoreCase("false")){
                        ma.add(Boolean.FALSE);
                    }else {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is not Valid, Please Type True or False.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(OfflinePlayer.class)){
                    ma.add(Bukkit.getOfflinePlayer(argsList[i]));
                }
                else if(Types[i].equals(Player.class)){
                    Player player;
                    if ((player = Bukkit.getPlayerExact(argsList[i])) != null){
                        ma.add(player);
                    }else {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is Not Valid Online Player, Please Try Again.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(Direction.class)){
                    try{
                        ma.add(Direction.valueOf(argsList[i].toUpperCase()));
                    }catch(IllegalArgumentException e){
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is Not Valid Direction, Please Type N, E, S, or W.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else{
                    ma.add(arg);
                }
            }
            return ma;
        }else {
            Sender.sendMessage(ChatColor.RED + "[Error] Incorrect Number Of Arguments. Usage: " + Usage);
            return null;
        }
    }
    protected class MultiArray {
        ArrayList<Object> _list = new ArrayList<>();
        public void add(Object Object){
            _list.add(Object);
        }
        public Integer getInt(int Index){
            return (Integer)_list.get(Index);
        }
        public Double getDouble(int Index){
            return (Double)_list.get(Index);
        }
        public Boolean getBoolean(int Index){
            return (Boolean)_list.get(Index);
        }
        public String getString(int Index){
            return (String)_list.get(Index);
        }
        public OfflinePlayer getOfflinePlayer(int Index){
            return (OfflinePlayer)_list.get(Index);
        }
        public Player getPlayer(int Index){
            return (Player)_list.get(Index);
        }
        public Direction getDirection(int Index){
            return (Direction)_list.get(Index);
        }

    }

}