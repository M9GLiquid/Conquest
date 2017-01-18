package eu.kingconquest.conquest.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.database.Config;
import net.md_5.bungee.api.ChatColor;

public class ChatManager {

	/**
	 * Output to Chat
	 * @param p - Player Instance
	 * @param text - String
	 * @return void
	 */
	public static void Chat(Player p, String str){
		String text = "{plugin_prefix} " + str;
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				ColorManager.extendColorCodes(p, text)).replace("_", " "));
	}

	/**
	 * Player Specific Placeholders
	 * @param text - String
	 * @param p - Player Instance
	 * @return String
	 */
	public static String placeholders(Player p, String text){
		String tpDelay = String.valueOf(Config.getLong("TeleportDelay", p.getLocation()) / 20);
		text = text.contains("{tpDelay}") 			? text.replace("{tpDelay}", tpDelay)  : text.replace("{tpDelay}", "");
		text = placeholders(text);
		return text;
	}	

	/**
	 * General Placeholders
	 * @param text - String
	 * @return String
	 */
	public static String placeholders(String text){
		try {
			text = text.contains("{plugin_prefix}") 	? text.replace("{plugin_prefix}", Config.getStr("Prefix")) : text.replace("{plugin_prefix}", "");
			text = text.contains("{town}") 				? text.replace("{town}", Cach.StaticTown.getName()) : text.replace("{town}", "");
			text = text.contains("{village}") 			? text.replace("{village}", Cach.StaticVillage.getName()) : text.replace("{village}", "");
			text = text.contains("{kingdom}") 			? text.replace("{kingdom}", Cach.StaticKingdom.getName()) : text.replace("{kingdom}", "");
			text = text.contains("{color}") 			? text.replace("{color}", Cach.StaticKingdom.getColorSymbol()) : text.replace("{color}", "");
			//text = text.contains("{user}") 					? text.replace("{user}", Cach.StaticPlayer.getName()) : text;
		}catch(Exception e) {
			e.printStackTrace();
			Console("&4ERROR: A placeholder failed!");
		}
		return text;
	}

	/**
	 * Format Color Codes for Scoreboard & Console
	 * @param text - String
	 * @return String
	 */
	public static String Format(String text) {
		return ChatColor.translateAlternateColorCodes('&', placeholders(text));
	}

	/**
	 * Send Colored messages to console
	 * @param text - String
	 * @return void
	 */
	public static void Console(String string){
		String text = "&2" + string;
		Bukkit.getConsoleSender().sendMessage(Format(text));
	}
	
	/**
	 * Broadcast Colored messages to Server
	 * @param str
	 */
	public static void Broadcast(String str){
		String text = "{plugin_prefix} ";
		text += str;
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
				ColorManager.extendColorCodes(text)).replace("_", " "));
	}
}
