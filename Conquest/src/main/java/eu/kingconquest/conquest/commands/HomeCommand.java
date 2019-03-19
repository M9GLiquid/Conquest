package eu.kingconquest.conquest.commands;

import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.gui.HomeGUI;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (sender instanceof Player) {
			Player player = (Player) sender;
            YmlStorage.getWorlds().forEach(world -> {
                if (world.equals(player.getWorld().getUID()))
                    if (Validate.hasPerm(player, ".basic"))
                        while (player.getOpenInventory() == null)
                            new HomeGUI(player); //Open again if not opened.

			});
		}
        return false;
	}
}
