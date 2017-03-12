package eu.kingconquest.conquest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.database.YmlStorage;

public class Message{


	public Message(Player target, MessageType type, String message){
		switch(type){
		case CHAT:
			if (Validate.notNull(target))
				target.sendMessage(getMessage("{Prefix} " + message));
			else
				new Message(null, MessageType.ERROR, "Tried to send a chat message without a player to send to");
			break;
		case DEBUG:
			Bukkit.getConsoleSender().sendMessage(getMessage(" {Prefix} &6[&c"+ type.getName() +"&6] &7" + message));
			break;
		case ERROR:
			Bukkit.getConsoleSender().sendMessage(getMessage(" {Prefix} &6[&c"+ type.getName() +"&6] &4" + message));
			break;
		case BROADCAST:
			Main.getInstance().getServer().broadcastMessage(getMessage("&6[&c"+ type.getName() +"&6] " + message));
			break;
		case CONSOLE:
		default:
			Bukkit.getConsoleSender().sendMessage(getMessage(" {Prefix} " + message));
			break;
		}
	}

	private static String translate(String text){
		Matcher match = Pattern.compile("\\{(.*?)\\}").matcher(text);
		
		while (match.find()) {
			 if (Validate.notNull(YmlStorage.getStr(match.group().replace("{", "").replace("}", ""))))
			 text = text.contains(match.group()) 					? text.replace(match.group(), YmlStorage.getStr(match.group().replace("{", "").replace("}", ""))) 		: text.replace(match.group(), "");
		}

		text = text.contains("{RewardNotReady}") 	? text.replace("{RewardNotReady}"	, Cach.StaticCooldownLeft.toString())										: text.replace("{RewardNotReady}", "");
		text = text.contains("{TeleportDelay}") 		? text.replace("{TeleportDelay}"		, Cach.tpDelay.toString())															: text.replace("{TeleportDelay}", "");
		text = text.contains("{kingdom}") 					? text.replace("{kingdom}"					, Cach.StaticKingdom.getName()) 												: text.replace("{kingdom}", "");
		text = text.contains("{village}") 						? text.replace("{village}"						, Cach.StaticVillage.getName())													: text.replace("{village}", "");
		text = text.contains("{reward}") 					? text.replace("{reward}"					, Cach.StaticReward.getName())	 												: text.replace("{reward}", "");
		text = text.contains("{town}") 						? text.replace("{town}"						, Cach.StaticTown.getName()) 													: text.replace("{town}", "");
		text = text.contains("{color}") 						? text.replace("{color}"						, Cach.StaticKingdom.getColorSymbol()) 									: text.replace("{color}", "");
		text = text.contains("{user}") 							? text.replace("{user}"							, Cach.StaticPlayer.getDisplayName())	 									: text.replace("{user}", "");
		text = text.contains("{cost}") 							? text.replace("{cost}"							, Cach.StaticReward.getCost().toString())	 								: text.replace("{cost}", "");
		
		Cach.nullify();
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
