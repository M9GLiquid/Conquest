package eu.kingconquest.conquest.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
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
			text = text.contains("{plugin_prefix}") 	? text.replace("{plugin_prefix}", Config.getChat("Prefix")) : text.replace("{plugin_prefix}", "");
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
		Main.getInstance().getServer().getConsoleSender().sendMessage(Format(text));
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
	
/*
	*//**
	 * Chat with Double Hovertext and Actions (Does currently not work on the main server, conflict)
	 * @param p - Player Instance
	 * @param text1 - String
	 * @param text2 - String
	 * @param suggest1 - String (Suggest/Run as command)
	 * @param suggest2 - String (Suggest/Run as command)
	 * @param hoverText1 - String
	 * @param hoverText2 - String
	 * @param hoverColor - ChatColor
	 * @param textColor - ChatColor
	 * @param action - boolean (True :: Suggestive Command | False :: Run Command)
	 *//*
	public static void JsonChat(Player p, String text1, String text2, String command1, String command2, String hover1, String hover2, boolean suggestive){

		text1 = Format(placeholders(text1));
		text2 = Format(placeholders(text2));
		hover1 = Format(placeholders(hover1));
		hover2 = Format(placeholders(hover2));

		FancyMessage msg = new FancyMessage(text1);
		if(suggestive){
			msg.suggest(command1);
			msg.tooltip(hover1);
			msg.then(text2);
			msg.suggest(command2);
			msg.tooltip(hover2);
		}else{
			msg.command(command1);
			msg.tooltip(hover1);
			msg.then(text2);
			msg.command(command2);
			msg.tooltip(hover2);
		}
		msg.send(p);
	}

	*//**
	 * Chat with Hovertext and Actions (Does currently not work on the main server, conflict)
	 * @param p - Player Instance
	 * @param text - String
	 * @param suggest - String (Suggest/Run as command)
	 * @param hoverText - String
	 * @param hoverColor - ChatColor
	 * @param textColor - ChatColor
	 * @param action - boolean (True :: Suggestive Command | False :: Run Command)
	 *//*
	public static void JsonChat(Player p, String text, String command, String hover, boolean suggestive){
		text = Format(placeholders(text));
		hover = Format(placeholders(hover));

		FancyMessage msg = new FancyMessage(text);
		if(suggestive)
			msg.suggest(command);
		else
			msg.command(command);
		msg.tooltip(hover);
		msg.send(p);
	}

	*//**
	 * Chat with Hovertext and Actions (Does currently not work on the main server, conflict)
	 * @param p - Player Instance
	 * @param text - String
	 * @param suggest - String (Suggest/Run as command)
	 * @param hoverText - String
	 * @param hoverColor - ChatColor
	 * @param textColor - ChatColor
	 * @param action - boolean (True :: Suggestive Command | False :: Run Command)
	 *//*
	public static void JsonChat(Player p, String text, String command, ArrayList<String> hover, boolean suggestive){
		text = Format(placeholders(text));
		String hoverText = Format(placeholders("&6|============{plugin_prefix}===========|\n"));
		int i = 0;
		FancyMessage msg = new FancyMessage(text);
		if(suggestive)
			msg.suggest(command);
		else
			msg.command(command);
		for(String str : hover){
			if (i == hover.size())
				break;
			str = Format(placeholders(str));
			hoverText += str + "\n";
			i++;
		}
		hoverText += Format(placeholders("&6|=================================|"));
		msg.tooltip(hoverText);
		msg.send(p);
	}
*/

}
