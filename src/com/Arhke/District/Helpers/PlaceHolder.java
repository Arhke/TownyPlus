package com.Arhke.District.Helpers;

import com.Arhke.District.Main;
import com.Arhke.District.TUsers.TUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceHolder extends PlaceholderExpansion {
    private Main _plugin;


    public PlaceHolder(Main plugin) {
        this._plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return _plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "TOWNYPLUS";
    }

    @Override
    public String getVersion() {
        return _plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(identifier.equalsIgnoreCase("TOWNNAME")){
            TUser user = _plugin.getTUserManager().getOrNewTUser(player);
            if(user.isInTown()){
                return user.getTown().getName();
            }else{
                return user.getRank().getName();
            }
        }else if(identifier.equalsIgnoreCase("CLAIMS")) {
            return Double.toString(_plugin.getTUserManager().getOrNewTUser(player).getClaims());
        }
        return null;
    }
}
