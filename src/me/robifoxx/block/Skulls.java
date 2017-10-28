package me.robifoxx.block;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.UUID;

public class Skulls {

	 private static ItemStack create(Material material, byte data) {
		 
	        ItemStack itemStack = new MaterialData(material, data).toItemStack(1);
	        ItemMeta itemMeta = itemStack.getItemMeta();
	        itemStack.setItemMeta(itemMeta);
	        return itemStack;
	        
	}
	
	public static ItemStack createSkull(String urlToFormat) {

        String s = urlToFormat.replace("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv", "");
        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + s;
        ItemStack head = create(Material.SKULL_ITEM, (byte) 3);

        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
            
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
        	
            e1.printStackTrace();
            
        }
        
        head.setItemMeta(headMeta);
        return head;
        
    }
	
}
