package eu.kingconquest.conquest.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.hook.Vault;
import net.md_5.bungee.api.ChatColor;

public class ChatManager {
	private static Kingdom kingdom;
	private static Town town;
	private static Village village;

	/**
	 * Output to Chat
	 * @param p - Player Instance
	 * @param text - String
	 * @return void
	 */
	public static void Chat(Player p, String str){
		String text = "{plugin_prefix} ";
		text += str;
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
		text = text.contains("{player}") 				? text.replace("{player}", p.getName()) : text;
		text = text.contains("{user}") 					? text.replace("{user}", Cach.StaticPlayer.getName()) : text;
		text = text.contains("{player_prefix}") 	? text.replace("{player_prefix}", Vault.chat.getPlayerPrefix(p.getWorld().getName(), p)) : text;
		text = text.contains("{tpDelay}") 			? text.replace("{tpDelay}", Config.TeleportDelay.get(p.getWorld()).toString()) : text.replace("{tpDelay}", "");
		text = placeholders(text);
		return text;
	}	

	/**
	 * General Placeholders
	 * @param text - String
	 * @return String
	 */
	public static String placeholders(String text){
		if (Cach.StaticKingdom != null)
			kingdom = Cach.StaticKingdom;
		if (Cach.StaticTown != null)
			town = Cach.StaticTown;
		if (Cach.StaticVillage != null)
			village = Cach.StaticVillage;

		try {
			text = text.contains("{plugin_prefix}") 	? text.replace("{plugin_prefix}", Config.getChat("Prefix")) : text.replace("{plugin_prefix}", "");
			text = text.contains("{town}") 				? text.replace("{town}", town.getName()) : text.replace("{town}", "");
			text = text.contains("{village}") 			? text.replace("{village}", village.getName()) : text.replace("{village}", "");
			text = text.contains("{kingdom}") 			? text.replace("{kingdom}", kingdom.getName()) : text.replace("{kingdom}", "");
			text = text.contains("{color}") 			? text.replace("{color}", kingdom.getColorSymbol()) : text.replace("{color}", "");
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
	/**
	 * Broadcast message to the server
	 * @param text - String
	 * @return void
	 */
	public static void Broadcast(String text){
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
				ColorManager.extendColorCodes(text)).replace("_", " "));
	}
}
