package eu.kingconquest.conquest.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.core.gui.HomeGUI;
import net.milkbowl.vault.permission.Permission;

public class Commands{
	public static Permission perms = null;
	private static Player p = null;

	/**
	 * On Command Do..
	 * @param Commands Player & Console
	 * @param pl
	 * @param sender
	 * @param args
	 */
	public static void Main(CommandSender sender, String[] args) {

//# Player based commands
		if (sender instanceof Player){
			p = (Player) sender;
			new HomeGUI(p);
		}
	}
}
