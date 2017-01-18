package eu.kingconquest.conquest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Message{
	private String prefix ="{plugin_prefix}";

	public Message(Player player, MessageType type, String message){
		switch(type){
		case CHAT:
			player.sendMessage(getMessage(message));
			break;
		case DEBUG:
			
			break;
		case BROADCAST:
			Bukkit.broadcastMessage(getMessage(prefix + message));
			break;
		case CONSOLE:
			Bukkit.getConsoleSender().sendMessage(getMessage("&2" + message));
			break;
		case NONE:
		default:
			
			break;
		}
	}
	
	private static String translate(String text){
		try{
			 Matcher m = Pattern.compile("\\{(.*?)\\}")
			     .matcher(text);
			 while (m.find()) {
				 switch(m.group()){
					 case "TeleportDelay": 	text = text.replaceAll(m.group(), String.valueOf(Cach.tpDelay));
					 case "town": 					text = text.replaceAll(m.group(), String.valueOf(Cach.StaticTown.getName()));
					 case "village": 				text = text.replaceAll(m.group(), String.valueOf(Cach.StaticVillage.getName()));
					 case "kingdom": 			text = text.replaceAll(m.group(), String.valueOf(Cach.StaticKingdom.getName()));
					 case "color": 					text = text.replaceAll(m.group(), String.valueOf(Cach.StaticKingdom.getColorSymbol()));
				 }
			 }
		}catch(Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(getMessage("&4ERROR: &cA placeholder failed!"));
		}
		return text;
	}	
	
	public static String getMessage(String text){
		return ChatColor.translateAlternateColorCodes('&', translate(text));
	}
}
