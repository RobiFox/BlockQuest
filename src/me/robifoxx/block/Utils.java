package me.robifoxx.block;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Utils {
    public static boolean useUUID = true;
    public static String getIdentifier(Player p) {
        if(useUUID) {
            return p.getUniqueId().toString();
        } else {
            return p.getName();
        }
    }
    public static String getIdentifier(String p) {
        if(useUUID) {
            return p;
        } else {
            return Bukkit.getOfflinePlayer(UUID.fromString(p)).getName();
        }
    }
}
