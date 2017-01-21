package eu.kingconquest.conquest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.database.YmlStorage;

public class Message{

	public Message(Player player, MessageType type, String message){
		switch(type){
		case CHAT:
			if (Validate.notNull(player))
				player.sendMessage(getMessage(message));
			else
				new Message(null, MessageType.CONSOLE, "&4ERROR: Tried to send a chat message without a player to send to");
			break;
		case DEBUG:
			Bukkit.getLogger().warning(getMessage("{Prefix}&6[&4Debug&6]" + message));
			break;
		case BROADCAST:
			Bukkit.broadcastMessage(getMessage("&6[&cBroadCast&6]" + message));
			break;
		case CONSOLE:
		default:
			Bukkit.getConsoleSender().sendMessage(getMessage("{Prefix}" + message));
			break;
		}
	}

	private static String translate(String text){
		try{
			Matcher match = Pattern.compile("\\{(.*?)\\}").matcher(text);
			while (match.find()) {
				text = text.contains(match.group().replaceAll("\\{|", "").replaceAll("\\}","")) 					? text.replace(match.group(), YmlStorage.getStr(match.group().replaceAll("\\{|", "").replaceAll("\\}",""))) 					: text.replace(match.group(), "");
			}
			match = Pattern.compile("\\{(.*?)\\}").matcher(text);
			while (match.find()){
				text = text.contains("TeleportDelay") 		? text.replace(match.group(), String.valueOf(Cach.tpDelay)) 																						: text.replace(match.group(), "");
				text = text.contains("town") 						? text.replace(match.group(), String.valueOf(Cach.StaticTown.getName())) 																: text.replace(match.group(), "");
				text = text.contains("village") 					? text.replace(match.group(), String.valueOf(Cach.StaticVillage.getName()))															: text.replace(match.group(), "");
				text = text.contains("kingdom") 				? text.replace(match.group(), String.valueOf(Cach.StaticKingdom.getName())) 														: text.replace(match.group(), "");
				text = text.contains("color") 						? text.replace(match.group(), Cach.StaticKingdom.getColorSymbol()) 																		: text.replace(match.group(), "");
			}
		}catch(Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(getMessage("&4ERROR: &cA placeholder failed!"));
		}
		return text;
	}	

	public static String getMessage(String text){
		return ChatColor.translateAlternateColorCodes('&', 
				extendColorCodes(text)).replace("_", " ");
	}

	/**
	 * Workaround for ColorCodes
	 * @param string - Text to Check
	 * @return String
	 */
	public static String extendColorCodes(String text){
		text = translate(text);
		String[] words = text.split("\\s");
		if (words.length < 2)
			return text;

		// StringBuilder > String concatenation
		StringBuilder builder = new StringBuilder();
		String lastColor = null;
		for (String word : words){
			if (lastColor != null && !lastColor.isEmpty())
				builder.append(lastColor);
			builder.append(word).append(' ');
			if (lastColor != null && !lastColor.isEmpty())
				word = lastColor + word;
			lastColor = ChatColor.getLastColors(word);
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}
}
