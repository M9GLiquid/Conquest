package eu.kingconquest.conquest.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.gui.HomeGUI;
import eu.kingconquest.conquest.util.Config;
import net.milkbowl.vault.permission.Permission;

public class Commands{
	public static Permission perms = null;

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
			Player player = (Player) sender;

			Config.getWorlds().forEach(world->{
				if (player.getWorld().equals(world)){
					new HomeGUI(player);
				}
			});
		}
	}
}
